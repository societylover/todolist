package com.homework.data.repository

import com.homework.common.network.AppDispatchers
import com.homework.common.network.Dispatcher
import com.homework.common.result.Result
import com.homework.common.result.type.LocalErrors
import com.homework.data.bus.ErrorBus
import com.homework.data.model.toTodo
import com.homework.data.model.toTodoItem
import com.homework.database.dao.TodoEntityDao
import com.homework.database.model.TodoEntity
import com.homework.datastore.DeviceParams
import com.homework.datastore.di.DeviceSensitive
import com.homework.model.data.Importance
import com.homework.model.data.TodoItem
import com.homework.network.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

internal class OfflineFirstTodoDetailsRepository @Inject constructor(
    private val todoEntityDao: TodoEntityDao,
    private val remoteSource: RemoteDataSource,
    @DeviceSensitive private val deviceParams: DeviceParams,
    private val errorBus: ErrorBus,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : TodoDetailsRepository {

    override suspend fun getTodoDetails(id: String): Result<TodoItem> {
        val localValue = todoEntityDao.getItemDetails(id)
        return if (localValue == null) {
            Result.Error(LocalErrors.NOT_FOUND, message = LocalErrors.NOT_FOUND.message)
        } else {
            Result.Success(localValue.toTodoItem())
        }
    }

    override suspend fun removeItemById(id: String): Result<Boolean> {
        val updatedCount = todoEntityDao.markItemAsDeleted(id)
        if (updatedCount != 1) {
            return Result.Error(LocalErrors.NOT_FOUND, LocalErrors.NOT_FOUND.message)
        } else {
            withContext(ioDispatcher) {
                val remoteResult = remoteSource.deleteTodoById(id)
                todoEntityDao.removeItemById(id)
                if (remoteResult is Result.Error) {
                    errorBus.notifyAboutError(remoteResult)
                }
            }
        }

        return Result.Success(true)
    }

    override suspend fun createItem(
        text: String,
        importance: Importance,
        deadlineAt: LocalDate?
    ): Result<Boolean> {
        val newItem = TodoEntity(
            id = "${deviceParams.deviceId}-${UUID.randomUUID()}",
            text = text,
            importance = importance,
            done = false,
            createdAt = Instant.now(),
            deadlineAt = deadlineAt,
            updatedAt = Instant.now(),
            localOnly = true
        )

        todoEntityDao.addItem(newItem)

        withContext(ioDispatcher) {
            val remoteResult = remoteSource.createNewTodo(newItem.toTodo(deviceParams.deviceId))
            if (remoteResult is Result.Success) {
                todoEntityDao.updateItem(newItem.copy(localOnly = false))
            } else {
                errorBus.notifyAboutError(remoteResult as Result.Error)
            }
        }
        return Result.Success(true)
    }

    override suspend fun updateItem(
        id: String,
        text: String,
        done: Boolean,
        importance: Importance,
        deadlineAt: LocalDate?,
        updated: Instant
    ): Result<Boolean> {
        val existItem = todoEntityDao.getItemDetails(id)
            ?: return Result.Error(LocalErrors.NOT_FOUND, LocalErrors.NOT_FOUND.message)

        val updatedItem = existItem.copy(
            text = text,
            done = done,
            importance = importance,
            deadlineAt = deadlineAt, updatedAt = updated
        )

        todoEntityDao.updateItem(updatedItem)

        withContext(ioDispatcher) {
            val remoteResult = remoteSource.updateTodoById(updatedItem.toTodo(deviceParams.deviceId))
            if (remoteResult is Result.Error) {
                errorBus.notifyAboutError(remoteResult)
            }
        }
        return Result.Success(true)
    }
}