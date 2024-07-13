package ru.yandex.shmr24.task

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import ru.yandex.shmr24.practice.TelegramApi
import java.io.File

abstract class ValidateApkSizeTask : DefaultTask() {

    @get:InputFile
    abstract val apkFile: RegularFileProperty

    init {
        group = "verification"
        description = "Validates the size of the APK file"
    }

    @TaskAction
    fun validateApkSize() {
        val apkSizeMb = apkFile.get().asFile.length() / (1024 * 1024) // Size in MB

        println("APK file size: $apkSizeMb MB")

        val maxSizeMb = project.extensions.getByType(TelegramReporterExtension::class.java).maxApkSizeMb.get()
        if (apkSizeMb > maxSizeMb) {
            val message = "APK file size exceeded maximum allowed size of $maxSizeMb MB."
            project.logger.error(message)
            val telegramApi = TelegramApi(HttpClient(OkHttp))
            val token = project.extensions.getByType(TelegramReporterExtension::class.java).token.get()
            val chatId = project.extensions.getByType(TelegramReporterExtension::class.java).chatId.get()
            try {
                runBlocking {
                    telegramApi.sendMessage(message, token, chatId).apply {
                        println("Status = $status")
                    }
                }
            } catch (e: Exception) {
                project.logger.error("Failed to send Telegram message: ${e.message}")
            }
            throw RuntimeException(message)
        }
    }
}
