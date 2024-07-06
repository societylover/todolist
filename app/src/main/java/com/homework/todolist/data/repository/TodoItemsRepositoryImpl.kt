package com.homework.todolist.data.repository

import com.homework.todolist.data.datasource.local.LocalDataSource
import com.homework.todolist.data.datasource.local.LocalDataSourceStub
import com.homework.todolist.data.datasource.remote.RemoteDataSource
import com.homework.todolist.data.datasource.remote.RemoteDataSourceStub
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.shared.data.result.LocalErrors
import com.homework.todolist.shared.data.result.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/**
 * To do items repository implementation
 */
class TodoItemsRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : TodoItemsRepository {

    override fun getItemsList(): Flow<List<TodoItem>> =
        localDataSource.getItemsList()

    override suspend fun fetchItems(): Result<Boolean> {
        val loadResult = remoteDataSource.loadTodos()
        return if (loadResult is Result.Error) {
            loadResult
        } else {
            val remoteItems = (loadResult as Result.Success).data
            localDataSource.setItemsList(remoteItems)
            return Result.Success(true)
        }
    }

    override suspend fun getItemDetails(id: String): Result<TodoItem?> {
        return Result.Success(localDataSource.getItemDetails(id))
    }

    override suspend fun removeItemById(id: String): Result<Boolean> {
        val deleteResult = remoteDataSource.deleteTodoById(id)
        return if (deleteResult is Result.Error) {

            deleteResult
        } else {
            val localDeleteResult = localDataSource.removeItemById(id)
            return Result.Success(localDeleteResult)
        }
    }

    override suspend fun createItem(
        text: String,
        importance: Importance,
        deadlineAt: LocalDate?
    ): Result<Boolean> {
        val createResult = remoteDataSource.createNewTodo(TodoItem(
            id = generateNewId(),
            text = text,
            importance = importance,
            done = false,
            createdAt = LocalDate.now(),
            deadlineAt = deadlineAt
        ))
        return if (createResult is Result.Error) {
            createResult
        } else {
            val localAddResult = localDataSource.addItem((createResult as Result.Success).data)
            Result.Success(localAddResult)
        }
    }

    override suspend fun updateItem(
        id: String,
        text: String,
        done: Boolean,
        importance: Importance,
        deadlineAt: LocalDate?,
        updated: LocalDate
    ): Result<Boolean> {
        val existItem = localDataSource.getItemDetails(id)
            ?: return Result.Error(errorType = LocalErrors.NOT_FOUND, "Item not found locally")
        val updateResult = remoteDataSource.updateTodoById(TodoItem(id, text, importance, done,
            createdAt = existItem.createdAt, deadlineAt = deadlineAt, updatedAt = updated))
        return if (updateResult is Result.Error) {
            updateResult
        } else {
            val result = localDataSource.updateItem((updateResult as Result.Success).data)
            if (!result) {
                return Result.Error(LocalErrors.UNKNOWN_ERROR, "Error while updating item")
            }
            return Result.Success(true)
        }
    }

    private suspend fun generateNewId(): String {
        var newId = UUID.randomUUID().toString()
        while (localDataSource.getItemDetails(newId) != null) {
            newId = UUID.randomUUID().toString()
        }
        return newId
    }
}

internal fun TodoItemsRepositoryStub() : TodoItemsRepository {
    return TodoItemsRepositoryImpl(LocalDataSourceStub(), RemoteDataSourceStub())
}