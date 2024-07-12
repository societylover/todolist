package com.homework.data.bus

import com.homework.common.result.Result
import kotlinx.coroutines.flow.Flow

/**
 * Global error notifier for errors in application
 */
interface ErrorBus {

    /**
     * Get flow of errors
     */
    fun getErrorNotificationFlow() : Flow<Result.Error>

    /**
     * Suspendable notify about error in application
     * @param error Error to notify
     */
    suspend fun notifyAboutError(error: Result.Error)

    /**
     * Notify about error in application
     * @param error Error to notify about
     * @return Result of notify action
     */
    fun tryNotifyAboutError(error: Result.Error) : Boolean
}