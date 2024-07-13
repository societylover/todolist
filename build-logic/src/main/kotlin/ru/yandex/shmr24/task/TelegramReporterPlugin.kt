//package ru.yandex.shmr24.practice
//
//import com.android.build.api.artifact.SingleArtifact
//import com.android.build.api.dsl.BuildType
//import com.android.build.api.variant.AndroidComponentsExtension
//import com.android.build.api.variant.BuildType
//import com.android.build.api.variant.Variant
//import com.android.build.api.variant.VariantOutput
//import org.gradle.api.GradleException
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.gradle.api.provider.Property
//import org.gradle.kotlin.dsl.create
//import ru.yandex.shmr24.task.TelegramReporterExtension
//import ru.yandex.shmr24.task.ValidateApkSizeTask
//
//class TelegramReporterPlugin : Plugin<Project> {
//
//    override fun apply(project: Project) {
//        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
//            ?: throw GradleException("Android not found")
//
//        val telegramExtension = project.extensions.create("telegram", TelegramReporterExtension::class.java)
//
//        androidComponents.onVariants { variant ->
//            val artifacts = variant.artifacts.get(SingleArtifact.APK)
//
//            val variantName = variant.name.capitalize()
//            val buildType = variant.buildType as BuildType
//            val versionCode = buildType.versionCode ?: 1
//
//            // Register validateApkSizeFor* task
//            project.tasks.register("validateApkSizeFor$variantName", ValidateApkSizeTask::class.java).configure {
//                apkFile.set(artifacts.get().outputFile)
//                project.logger.lifecycle("APK file for $variantName variant: ${artifacts.get().outputFile}")
//
//                // Configure TelegramExtension properties
//                telegramExtension.token.set("YOUR_TELEGRAM_BOT_TOKEN")
//                telegramExtension.chatId.set("YOUR_CHAT_ID")
//                telegramExtension.maxApkSizeMb.set(20) // Default maximum APK size in MB
//
//                // Add dependency to ensure validateApkSizeFor* runs before reportTelegramApkFor*
//                project.tasks.getByName("reportTelegramApkFor$variantName").dependsOn(this@register)
//            }
//
//            // Rename APK file
//            artifacts.get().outputFileName = "todolist-$variantName-$versionCode.apk"
//        }
//    }
//}
