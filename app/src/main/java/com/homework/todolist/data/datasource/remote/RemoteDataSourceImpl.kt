package com.homework.todolist.data.datasource.remote

import com.homework.todolist.data.datasource.remote.dto.TodoDetailsDTO
import com.homework.todolist.data.datasource.remote.dto.TodoDetailsInputDTO
import com.homework.todolist.data.datasource.remote.dto.TodosListDTO
import com.homework.todolist.data.datasource.remote.dto.TodosListInputDTO
import com.homework.todolist.data.datasource.remote.dto.toTodo
import com.homework.todolist.data.datasource.remote.dto.toTodoDTO
import com.homework.todolist.data.datasource.remote.dto.toTodoList
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.provider.ApiParamsProvider
import com.homework.todolist.di.ApplicationScope
import com.homework.todolist.shared.data.result.BadRequestRemoteErrors
import com.homework.todolist.shared.data.result.DeviceError
import com.homework.todolist.shared.data.result.RepeatableRemoteErrors
import com.homework.todolist.shared.data.result.Result
import com.homework.todolist.shared.data.result.StorageError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiParamsProvider: ApiParamsProvider,
    @ApplicationScope private val externalScope: CoroutineScope
) : RemoteDataSource {

    private val client: HttpClient = HttpClient(apiParamsProvider)

    override suspend fun loadTodos(): Result<List<TodoItem>> {
        return makeCatchableRequest {
            val response = client.get(DEFAULT_PATH_BEGIN)
            if (response.status.isSuccess()) {
                val resultDto = response.body<TodosListDTO>()
                Result.Success(resultDto.toTodoList())
            } else {
                handleError(response)
            }
        }
    }

    override suspend fun loadTodoById(todoId: String): Result<TodoItem> {
        return makeCatchableRequest {
            val response = client.get("$DEFAULT_PATH_BEGIN/$todoId")
            if (response.status.isSuccess()) {
                val resultDto = response.body<TodoDetailsDTO>()
                Result.Success(resultDto.toTodo())
            } else {
                handleError(response)
            }
        }
    }

    override suspend fun createNewTodo(todo: TodoItem): Result<TodoItem> {
        return makeCatchableRequest {
            val response = client.post(DEFAULT_PATH_BEGIN) {
                setBody(TodoDetailsInputDTO(todo.toTodoDTO(apiParamsProvider.getAndroidId())))
            }
            if (response.status.isSuccess()) {
                val resultDto = response.body<TodoDetailsDTO>()
                Result.Success(resultDto.toTodo())
            } else {
                handleError(response)
            }
        }
    }

    override suspend fun deleteTodoById(todoId: String): Result<Boolean> {
        return makeCatchableRequest {
            val response = client.delete("$DEFAULT_PATH_BEGIN/$todoId")
            if (response.status.isSuccess()) {
                Result.Success(true)
            } else {
                handleError(response)
            }
        }
    }

    override suspend fun updateTodoById(updatedTodo: TodoItem): Result<TodoItem> {
        return makeCatchableRequest {
            val response = client.put("$DEFAULT_PATH_BEGIN/${updatedTodo.id}") {
                setBody(TodoDetailsInputDTO(updatedTodo.toTodoDTO(apiParamsProvider.getAndroidId())))
            }
            if (response.status.isSuccess()) {
                val result = response.body<TodoDetailsDTO>()
                Result.Success(result.toTodo())
            } else {
                handleError(response)
            }
        }
    }

    override suspend fun setTodosList(todos: List<TodoItem>): Result<List<TodoItem>> {
        return makeCatchableRequest {
            val response = client.patch(DEFAULT_PATH_BEGIN) {
                setBody(TodosListInputDTO(elements = todos.map { it.toTodoDTO(apiParamsProvider.getAndroidId()) }))
            }
            if (response.status.isSuccess()) {
                val result = response.body<TodosListDTO>()
                Result.Success(result.toTodoList())
            } else {
                handleError(response)
            }
        }
    }

    private suspend fun <T> makeCatchableRequest(block: suspend () -> Result<T>): Result<T> {
        return externalScope.async {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                handleException(e)
            }
        }.await()
    }

    private suspend fun handleError(response: HttpResponse): Result.Error {
        val message = response.bodyAsText()
        return Result.Error(getErrorType(response.status, message), message)
    }

    private fun getErrorType(statusCode: HttpStatusCode, message: String): StorageError {
        return when (statusCode) {
            HttpStatusCode.Unauthorized -> BadRequestRemoteErrors.UNAUTHORIZED
            HttpStatusCode.NotFound -> BadRequestRemoteErrors.NOT_FOUND
            HttpStatusCode.BadRequest -> {
                return if (message == UNSYNCHRONIZED_TEXT) {
                    BadRequestRemoteErrors.UNSYNCHRONIZED_DATA
                } else {
                    BadRequestRemoteErrors.MALFORMED_REQUEST
                }
            }

            else -> RepeatableRemoteErrors.SERVER_ERROR
        }
    }

    private fun handleException(e: Exception): Result.Error {
        return Result.Error(errorType = DeviceError.DEVICE_ERROR, e.message, e)
    }

    companion object {
        private const val DEFAULT_PATH_BEGIN = "list"
        private const val UNSYNCHRONIZED_TEXT = "unsynchronized data"
    }
}