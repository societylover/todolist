import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.libsDirectory

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins.register("telegram-reporter") {
        id = "telegram-reporter"
        implementationClass = "ru.yandex.shmr24.practice.TelegramReporterPlugin"
    }
    plugins.register("size-validator") {
        id = "size-validator"
        implementationClass = "ru.yandex.shmr24.task.TelegramReporterPlugin"
    }
}

dependencies {
    implementation(libs.agp)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}