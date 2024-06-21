package com.homework.todolist.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Extension for tracking user timezone/locale for updating date view
 * @param context Application context to make subscription
 * @return Flow of settings change emits
 */
fun CoroutineScope.systemSettingsFlow(context: Context): Flow<Unit> = callbackFlow {
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            trySend(Unit)
        }
    }

    val intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_LOCALE_CHANGED)
        addAction(Intent.ACTION_TIMEZONE_CHANGED)
    }

    context.registerReceiver(broadcastReceiver, intentFilter)

    awaitClose {
        context.unregisterReceiver(broadcastReceiver)
    }
}

