package ru.yandex.shmr24.task

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class TelegramReporterPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: throw GradleException("Android not found")

        val telegramExtension = project.extensions.create("sizeValidator", TelegramReporterExtension::class.java)

        val androidExtension = project.extensions.getByType(BaseAppModuleExtension::class.java)

        androidComponents.onVariants { variant ->
            val artifacts = variant.artifacts.get(SingleArtifact.APK)

            val variantName = variant.name.capitalize()

            val versionCode = androidExtension.defaultConfig.versionCode ?: 1

            project.tasks.register("validateApkSizeFor$variantName", ValidateApkSizeTask::class.java).configure {
                apkFile.set(artifacts.get().asFile)
                project.logger.lifecycle("APK file for $variantName variant: ${artifacts.get().asFile}")

                token.set(telegramExtension.token)
                chatId.set(telegramExtension.chatId)
                maxApkSizeMb.set(telegramExtension.maxApkSizeMb)
                validateApkSizeEnabled.set(telegramExtension.validateApkSizeEnabled)

                project.tasks.named("reportTelegramApkFor$variantName").configure {
                    dependsOn(this@configure)
                }
            }

            artifacts.get().asFile.renameTo(File(artifacts.get().asFile.parent, "todolist-$variantName-$versionCode.apk"))
        }
    }
}
