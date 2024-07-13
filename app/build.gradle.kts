@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("android-app-convention")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization) version libs.versions.kotlin
    kotlin("kapt")
    id("telegram-reporter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tgReporter {
    token.set(providers.environmentVariable("TG_TOKEN"))
    chatId.set(providers.environmentVariable("TG_CHAT"))
    maxApkSizeMb.set(10)
    validateApkSizeEnabled.set(true)
}

android {
    flavorDimensions += "plan"
    productFlavors {
        create("paid") {
            dimension = "plan"
            buildConfigField("boolean", "PAID",  "true")
        }
        create("free") {
            dimension = "plan"
            buildConfigField("boolean", "PAID",  "false")
        }
    }
}

dependencies {
    implementation(libs.material3)
    implementation(libs.material)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)


    implementation(libs.androidx.navigation.compose)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.compose.lifecycle)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.data.store)
    kapt(libs.room.compiler)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    kapt(libs.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.auth.sdk)

    implementation(libs.work.manager)

    implementation(libs.bundles.ktor)
}