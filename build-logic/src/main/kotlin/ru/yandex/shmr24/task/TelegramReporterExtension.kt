package ru.yandex.shmr24.task

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

interface TelegramReporterExtension {
    @get:Internal
    val token: Property<String>

    @get:Internal
    val chatId: Property<String>

    val maxApkSizeMb: Property<Int>

    val validateApkSizeEnabled: Property<Boolean>
}
