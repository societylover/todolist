package com.homework.todolist.data.datasource.remote

import com.homework.todolist.data.datasource.remote.dto.TodoDetailsDTO
import com.homework.todolist.data.datasource.remote.dto.TodoDetailsInputDTO
import com.homework.todolist.data.datasource.remote.dto.TodosListDTO
import com.homework.todolist.data.datasource.remote.dto.TodosListInputDTO
import com.homework.todolist.data.datasource.remote.dto.toTodo
import com.homework.todolist.data.datasource.remote.dto.toTodoDTO
import com.homework.todolist.data.datasource.remote.dto.toTodoList
import com.homework.todolist.shared.data.result.BadRequestRemoteErrors
import com.homework.todolist.shared.data.result.StorageError
import com.homework.todolist.shared.data.result.Result
import com.homework.todolist.shared.data.result.RepeatableRemoteErrors
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.data.provider.ApiParamsProvider
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiParamsProvider: ApiParamsProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteDataSource {

    private val client: HttpClient = HttpClient(apiParamsProvider)

    override suspend fun loadTodos(): Result<List<TodoItem>> {
        return withContext(dispatcher) {
            val response = client.get(DEFAULT_PATH_BEGIN)
            if (response.status.isSuccess()) {
                val resultDto = response.body<TodosListDTO>()
                return@withContext Result.Success(resultDto.toTodoList())
            } else {
                return@withContext handleError(response)
            }
        }
    }

    override suspend fun loadTodoById(todoId: String): Result<TodoItem> {
        return withContext(dispatcher) {
            val response = client.get("$DEFAULT_PATH_BEGIN/$todoId")
            if (response.status.isSuccess()) {
                val resultDto = response.body<TodoDetailsDTO>()
                return@withContext Result.Success(resultDto.toTodo())
            } else {
                return@withContext handleError(response)
            }
        }
    }

    override suspend fun createNewTodo(todo: TodoItem): Result<TodoItem> {
        return withContext(dispatcher) {
            val response = client.post(DEFAULT_PATH_BEGIN) {
                setBody(TodoDetailsInputDTO(todo.toTodoDTO()))
            }
            if (response.status.isSuccess()) {
                val resultDto = response.body<TodoDetailsDTO>()
                return@withContext Result.Success(resultDto.toTodo())
            } else {
                return@withContext handleError(response)
            }
        }
    }

    override suspend fun deleteTodoById(todoId: String): Result<Boolean> {
        return withContext(dispatcher) {
            val response = client.delete("$DEFAULT_PATH_BEGIN/$todoId")
            if (response.status.isSuccess()) {
                return@withContext Result.Success(true)
            } else {
                return@withContext handleError(response)
            }
        }
    }

    override suspend fun updateTodoById(updatedTodo: TodoItem): Result<TodoItem> {
        return withContext(dispatcher) {
            val response = client.put("$DEFAULT_PATH_BEGIN/${updatedTodo.id}") {
                setBody(TodoDetailsInputDTO(updatedTodo.toTodoDTO()))
            }
            if (response.status.isSuccess()) {
                val result = response.body<TodoDetailsDTO>()
                return@withContext Result.Success(result.toTodo())
            } else {
                return@withContext handleError(response)
            }
        }
    }

    override suspend fun setTodosList(todos: List<TodoItem>): Result<List<TodoItem>> {
        return withContext(dispatcher) {
            val response = client.patch(DEFAULT_PATH_BEGIN) {
                setBody(TodosListInputDTO(elements = todos.map { it.toTodoDTO()}))
            }
            if (response.status.isSuccess()) {
                val result = response.body<TodosListDTO>()
                return@withContext Result.Success(result.toTodoList())
            } else {
                return@withContext handleError(response)
            }
        }
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
                return if (message == "unsynchronized data") {
                    BadRequestRemoteErrors.UNSYNCHRONIZED_DATA
                } else {
                    BadRequestRemoteErrors.MALFORMED_REQUEST
                }
            }
            else -> RepeatableRemoteErrors.SERVER_ERROR
        }
    }

    companion object {
        private const val DEFAULT_PATH_BEGIN = "list"
    }
}