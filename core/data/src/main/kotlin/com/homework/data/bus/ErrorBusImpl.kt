package com.homework.data.bus

import com.homework.common.result.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

internal class ErrorBusImpl : ErrorBus {
    private val errorChannel = Channel<Result.Error>(capacity = CONFLATED)

    override fun getErrorNotificationFlow(): Flow<Result.Error> {
        return errorChannel.consumeAsFlow()
    }

    override suspend fun notifyAboutError(error: Result.Error) =
        errorChannel.send(error)

    override fun tryNotifyAboutError(error: Result.Error) : Boolean {
        return errorChannel.trySend(error).isSuccess
    }
}