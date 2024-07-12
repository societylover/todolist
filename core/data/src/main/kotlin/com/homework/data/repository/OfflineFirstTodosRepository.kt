package com.homework.data.repository

import com.homework.common.network.AppDispatchers
import com.homework.common.network.Dispatcher
import com.homework.common.result.Result
import com.homework.common.result.type.LocalErrors
import com.homework.data.model.toTodo
import com.homework.data.model.toTodoEntity
import com.homework.data.model.toTodoItem
import com.homework.database.dao.TodoEntityDao
import com.homework.datastore.DeviceParams
import com.homework.datastore.di.DeviceSensitive
import com.homework.model.data.TodoItem
import com.homework.network.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class OfflineFirstTodosRepository @Inject constructor(
    private val todoEntityDao: TodoEntityDao,
    private val remoteSource: RemoteDataSource,
    @DeviceSensitive private val deviceParams: DeviceParams,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : TodosRepository {

    override fun getTodosList(): Flow<List<TodoItem>> {
        return todoEntityDao.getItemsList().map {
            itemsList -> itemsList.filter { !it.deletedLocally }.map { it.toTodoItem() }
        }
    }

    override suspend fun removeItemById(id: String): Result<Boolean> {
        val item = todoEntityDao.getItemDetails(id)
            ?: return Result.Error(LocalErrors.NOT_FOUND, LocalErrors.NOT_FOUND.message)

        todoEntityDao.markItemAsDeleted(id)
        return withContext(ioDispatcher) {
            val remoteResult = remoteSource.deleteTodoById(id)
            return@withContext if (remoteResult is Result.Success) {
                todoEntityDao.removeItemById(id)
                Result.Success(true)
            } else {
                remoteResult
            }
        }
    }

    override suspend fun changeItemDoneState(id: String, done: Boolean): Result<Boolean> {
        val item = todoEntityDao.getItemDetails(id)
            ?: return Result.Error(LocalErrors.NOT_FOUND, LocalErrors.NOT_FOUND.message)
        return withContext(ioDispatcher) {
            val result = remoteSource.updateTodoById(item.toTodo(deviceParams.deviceId))
            return@withContext if (result is Result.Error) {
                todoEntityDao.updateItem(item.copy(done = done))
                result
            } else {
                todoEntityDao.updateItem((result as Result.Success).data.element.toTodoEntity())
                Result.Success(true)
            }
        }
    }

    override suspend fun fetchItems(): Result<Boolean> {
        return withContext(ioDispatcher) {
            val todos = remoteSource.loadTodos()
            if (todos is Result.Error) {
                return@withContext todos
            }
            val localData = todoEntityDao.getItemsList().first()
            val relevantList = resolveMergeErrors(localData, (todos as Result.Success).data.items)
            val remoteUpdated = remoteSource.setTodosList(relevantList.map { it.toTodo(deviceParams.deviceId) })

            if (remoteUpdated is Result.Error) {
                todoEntityDao.setItemsList(relevantList)
                return@withContext  remoteUpdated
            } else {
                todoEntityDao.setItemsList((remoteUpdated as Result.Success).data.items.map { it.toTodoEntity() })
                return@withContext  Result.Success(true)
            }
        }
    }
}