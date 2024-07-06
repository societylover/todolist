package com.homework.todolist.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.homework.todolist.data.repository.TodoItemsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Sync data from remote source with periodic worker
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TodoItemsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val result = repository.fetchItems()
            if (result is com.homework.todolist.shared.data.result.Result.Error) {
                Result.failure()
            } else {
                Result.success()
            }
        }
    }

    companion object {
        internal const val WORKER_TAG = "SYNC-ITEMS-WORKER"
    }
}