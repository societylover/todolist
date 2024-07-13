package com.homework.todolist.data.repository

import com.homework.todolist.data.datasource.local.LocalDataSource
import com.homework.todolist.data.datasource.local.LocalDataSourceStub
import com.homework.todolist.data.datasource.local.TodoEntity
import com.homework.todolist.data.datasource.local.toTodoEntity
import com.homework.todolist.data.datasource.local.toTodoItem
import com.homework.todolist.data.datasource.remote.RemoteDataSource
import com.homework.todolist.data.datasource.remote.RemoteDataSourceStub
import com.homework.todolist.data.model.Importance
import com.homework.todolist.data.model.TodoItem
import com.homework.todolist.di.ApplicationScope
import com.homework.todolist.shared.data.result.LocalErrors
import com.homework.todolist.shared.data.result.Result
import com.homework.todolist.utils.LocalDateConverter.toMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/**
 * To do items repository implementation
 */
class TodoItemsRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    @ApplicationScope private val externalScope: CoroutineScope
) : TodoItemsRepository {

    override fun getItemsList(): Flow<List<TodoItem>> =
        localDataSource.getItemsList().map { entities -> entities.map { it.toTodoItem() } }

    override suspend fun fetchItems(): Result<Boolean> {
        val loadResult = remoteDataSource.loadTodos()
        return if (loadResult is Result.Error) {
            loadResult
        } else {
            externalScope.launch {
                val remoteItems = (loadResult as Result.Success).data
                val newItems = resolve(localDataSource.getItemsSnapshot(), remoteItems)
                localDataSource.setItemsList(newItems)
                remoteDataSource.setTodosList(newItems.map { it.toTodoItem() })
            }
            return Result.Success(true)
        }
    }

    override suspend fun getItemDetails(id: String): Result<TodoItem?> {
        return Result.Success(localDataSource.getItemDetails(id)?.toTodoItem())
    }

    override suspend fun removeItemById(id: String): Result<Boolean> {
        val deleteResult = localDataSource.markItemAsDeleted(id)

        externalScope.launch {
            val result = remoteDataSource.deleteTodoById(id)
            if (result is Result.Success) {
                localDataSource.removeItemById(id)
            }
        }

        return Result.Success(deleteResult)
    }

    override suspend fun createItem(
        text: String,
        importance: Importance,
        deadlineAt: LocalDate?
    ): Result<Boolean> {
        val item = TodoItem(
            id = generateNewId(),
            text = text,
            importance = importance,
            done = false,
            createdAt = LocalDate.now(),
            deadlineAt = deadlineAt
        )
        val localAddResult = localDataSource.addItem(item.toTodoEntity(true))

        externalScope.launch {
            val resultRemote = remoteDataSource.createNewTodo(item)
            if (resultRemote is Result.Success) {
                localDataSource.updateItem(resultRemote.data.toTodoEntity(false))
            }
            return@launch
        }

        return if (localAddResult) {
             Result.Success(true)
        } else {
            Result.Error(LocalErrors.UNKNOWN_ERROR, "Can't save locally.")
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
        val itemDetails = localDataSource.getItemDetails(id)
            ?: return Result.Error(errorType = LocalErrors.NOT_FOUND, "Item not found locally")

        val updated = itemDetails.copy(text = text, done = done, importance = importance, deadlineAt = deadlineAt.toMillis(),
            updatedAt = updated.toMillis())

        val result = localDataSource.updateItem(updated)

        externalScope.launch {
            val remoteResult = remoteDataSource.updateTodoById(updated.toTodoItem())
            if (remoteResult is Result.Success) {
                localDataSource.updateItem(remoteResult.data.toTodoEntity(false))
            }
        }

        return Result.Success(result)
    }

    private suspend fun generateNewId(): String {
        var newId = UUID.randomUUID().toString()
        while (localDataSource.getItemDetails(newId) != null) {
            newId = UUID.randomUUID().toString()
        }
        return newId
    }

    private fun resolve(localItems: List<TodoEntity>, remoteItems: List<TodoItem>) : List<TodoEntity> {
        val locallyDeletedFilter = localItems.filter { it.deletedLocally }.map { it.id }
        val nonRelevantLocalFilter = localItems.filter { it.localOnly } +
                remoteItems.filter { it.id !in locallyDeletedFilter }.map { it.toTodoEntity(false) }
        return nonRelevantLocalFilter.sortedByDescending { it.updatedAt }.distinctBy { it.id }
    }
}

internal fun TodoItemsRepositoryStub() : TodoItemsRepository {
    return TodoItemsRepositoryImpl(LocalDataSourceStub(), RemoteDataSourceStub(), CoroutineScope(Dispatchers.IO))
}