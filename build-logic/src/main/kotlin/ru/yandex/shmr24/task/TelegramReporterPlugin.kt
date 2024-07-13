package ru.yandex.shmr24.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.yandex.shmr24.practice.TelegramApi
import ru.yandex.shmr24.practice.TelegramReporterTask
import java.io.File

class TelegramReporterPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: throw GradleException("Android not found")

        val telegramExtension = project.extensions.create("telegram", TelegramReporterExtension::class.java)
        val androidExtension = project.extensions.getByType(BaseAppModuleExtension::class.java)
        val telegramApi = TelegramApi(HttpClient(OkHttp))

        androidComponents.onVariants { variant ->
            val artifacts = variant.artifacts.get(SingleArtifact.APK)
            val variantName = variant.name.capitalize()
            val versionCode = androidExtension.defaultConfig.versionCode ?: 1

            // Register ValidateApkSizeFor* task
            val validateTask = project.tasks.register("validateApkSizeFor$variantName", ValidateApkSizeTask::class.java, telegramApi).configure {
                apkFile.set(artifacts.map { it.asFile }.get())
                token.set(telegramExtension.token)
                chatId.set(telegramExtension.chatId)
                maxApkSizeMb.set(telegramExtension.maxApkSizeMb)
                validateApkSizeEnabled.set(telegramExtension.validateApkSizeEnabled)
            }

            // Register ReportTelegramApkFor* task
            val reportTask = project.tasks.register("reportTelegramApkFor$variantName", TelegramReporterTask::class.java, telegramApi).configure {
                dependsOn(validateTask)
                apkDir.set(artifacts.map { it.asFile.parentFile }.get())
                token.set(telegramExtension.token)
                chatId.set(telegramExtension.chatId)
            }

            // Rename APK file
            project.tasks.named("package${variantName.capitalize()}").configure {
                doLast {
                    val apkFile = artifacts.get().asFile
                    val newFileName = "todolist-$variantName-$versionCode.apk"
                    val newFile = File(apkFile.parent, newFileName)
                    apkFile.renameTo(newFile)
                }
            }
        }
    }
}
