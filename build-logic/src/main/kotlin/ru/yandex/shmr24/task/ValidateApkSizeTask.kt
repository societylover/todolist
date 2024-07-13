package ru.yandex.shmr24.task

import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import ru.yandex.shmr24.practice.TelegramApi
import javax.inject.Inject

abstract class ValidateApkSizeTask @Inject constructor(
    private val telegramApi: TelegramApi
) : DefaultTask() {

    @get:InputFile
    abstract val apkFile: RegularFileProperty

    @get:Input
    abstract val token: Property<String>

    @get:Input
    abstract val chatId: Property<String>

    @get:Input
    abstract val maxApkSizeMb: Property<Int>

    @get:Input
    abstract val validateApkSizeEnabled: Property<Boolean>

    @TaskAction
    fun validateApkSize() {
        if (!validateApkSizeEnabled.get()) return

        val file = apkFile.get().asFile
        val fileSizeMb = file.length() / (1024 * 1024)
        if (fileSizeMb > maxApkSizeMb.get()) {
            runBlocking {
                telegramApi.sendMessage("APK size is too large: $fileSizeMb MB", token.get(), chatId.get())
            }
            throw GradleException("APK size ($fileSizeMb MB) exceeds the maximum allowed size (${maxApkSizeMb.get()} MB).")
        }
    }
}
