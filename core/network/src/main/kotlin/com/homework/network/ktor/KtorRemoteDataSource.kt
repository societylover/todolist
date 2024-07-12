package com.homework.network.ktor

import com.homework.common.network.AppDispatchers
import com.homework.common.network.Dispatcher
import com.homework.common.result.Result
import com.homework.common.result.type.BadRequestRemoteErrors
import com.homework.common.result.type.DeviceError
import com.homework.common.result.type.ErrorType
import com.homework.common.result.type.RepeatableRemoteErrors
import com.homework.network.RemoteDataSource
import com.homework.network.model.Todo
import com.homework.network.model.TodoDetails
import com.homework.network.model.TodoDetailsInput
import com.homework.network.model.TodosList
import com.homework.network.model.TodosListInput
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
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

internal class RemoteDataSourceImpl(
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val client: HttpClient
) : RemoteDataSource {

    override suspend fun loadTodos(): Result<TodosList> {
        return makeCatchableRequest {
            val response = client.get(DEFAULT_PATH_BEGIN)
            if (response.status.isSuccess()) {
                Result.Success(response.body<TodosList>())
            } else {
                handleError(response)
            }
        }
    }

    override suspend fun loadTodoById(todoId: String): Result<TodoDetails> {
        return makeCatchableRequest {
            val response = client.get("$DEFAULT_PATH_BEGIN/$todoId")
            if (response.status.isSuccess()) {
                Result.Success(response.body<TodoDetails>())
            } else {
                handleError(response)
            }
        }
    }

    override suspend fun createNewTodo(todo: Todo): Result<TodoDetails> {
        return makeCatchableRequest {
            val response = client.post(DEFAULT_PATH_BEGIN) {
                setBody(TodoDetailsInput(todo))
            }
            if (response.status.isSuccess()) {
                Result.Success(response.body<TodoDetails>())
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

    override suspend fun updateTodoById(updatedTodo: Todo): Result<TodoDetails> {
        return makeCatchableRequest {
            val response = client.put("$DEFAULT_PATH_BEGIN/${updatedTodo.id}") {
                setBody(TodoDetailsInput(updatedTodo))
            }
            if (response.status.isSuccess()) {
                Result.Success(response.body<TodoDetails>())
            } else {
                handleError(response)
            }
        }
    }

    override suspend fun setTodosList(todos: List<Todo>): Result<TodosList> {
        return makeCatchableRequest {
            val response = client.patch(DEFAULT_PATH_BEGIN) {
                setBody(TodosListInput(elements = todos))
            }
            if (response.status.isSuccess()) {
                Result.Success(response.body<TodosList>())
            } else {
                handleError(response)
            }
        }
    }

    private suspend fun <T> makeCatchableRequest(block: suspend () -> Result<T>): Result<T> {
        return withContext(ioDispatcher){
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                handleException(e)
            }
        }
    }

    private suspend fun handleError(response: HttpResponse): Result.Error {
        val message = response.bodyAsText()
        return Result.Error(getErrorType(response.status, message), message)
    }

    private fun getErrorType(statusCode: HttpStatusCode, message: String): ErrorType {
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