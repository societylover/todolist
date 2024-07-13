package ru.yandex.shmr24.practice

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import java.io.File

class TelegramReporterPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: throw GradleException("Android not found")

        val extension = project.extensions.create("tgReporter", TelegramExtension::class)

        val telegramApi = TelegramApi(HttpClient(OkHttp))
        val androidExtension = project.extensions.getByType(BaseAppModuleExtension::class.java)

        androidComponents.onVariants { variant ->
            val variantName = variant.name.capitalize()

            // Register ValidateApkSizeFor* task
            val validateTask = project.tasks.register("validateApkSizeFor${variantName}", ValidateApkSizeTask::class.java, telegramApi).apply {
                configure {
                    val apkArtifact = variant.artifacts.get(SingleArtifact.APK)
                    apkFile.set(apkArtifact.get().asFile)
                    token.set(extension.token)
                    chatId.set(extension.chatId)
                    maxApkSizeMb.set(extension.maxApkSizeMb)
                    validateApkSizeEnabled.set(extension.validateApkSizeEnabled)
                }
            }

            // Register ReportTelegramApkFor* task
            project.tasks.register("reportTelegramApkFor${variantName}", TelegramReporterTask::class.java, telegramApi).apply {
                configure {
                    dependsOn(validateTask)
                    val apkArtifact = variant.artifacts.get(SingleArtifact.APK)
                    apkDir.set(apkArtifact.get())
                    token.set(extension.token)
                    chatId.set(extension.chatId)
                }
            }

            val versionCode = androidExtension.defaultConfig.versionCode ?: 1

            // Rename APK file after assembling
            project.tasks.named("assemble${variantName.capitalize()}").configure {
                doLast {
                    val apkArtifact = variant.artifacts.get(SingleArtifact.APK)
                    val apkFile = apkArtifact.get().asFile
                    val newFileName = "todolist-${variantName}-${versionCode}.apk"
                    val newFile = File(apkFile.parentFile, newFileName)
                    if (apkFile.exists()) {
                        apkFile.renameTo(newFile)
                    }
                }
            }
        }
    }
}

interface TelegramExtension {
    val chatId: Property<String>
    val token: Property<String>
    val maxApkSizeMb: Property<Int>
    val validateApkSizeEnabled: Property<Boolean>
}
