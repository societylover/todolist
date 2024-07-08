package com.homework.todolist.data.sync

import android.content.Context
import com.homework.todolist.data.repository.TodoItemsRepository
import com.homework.todolist.utils.NetworkUtils.getConnectionStateFlow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

/**
 * Sync repository by connection state changes
 */
class SyncConnection @Inject constructor(private val repository: TodoItemsRepository){

    private val scope = CoroutineScope(SupervisorJob()) + CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    internal fun startHandling(context: Context) {
        scope.launch(Dispatchers.IO) {
            context.getConnectionStateFlow().collect { connected ->
                if (connected) {
                    repository.fetchItems()
                }
            }
        }
    }
}