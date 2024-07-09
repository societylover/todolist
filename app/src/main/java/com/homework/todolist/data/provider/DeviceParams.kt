package com.homework.todolist.data.provider

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

/**
 * Device params (id) provider
 */
@SuppressLint("HardwareIds")
class DeviceParams(
    context: Context
) {

    val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
