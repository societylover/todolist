import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
}

configure<BaseAppModuleExtension> {
    baseAndroidConfig()
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    defaultConfig {
        val baseUrl = "\"https://hive.mrdekk.ru/todo/\"" // providers.environmentVariable("BASE_URL")
        buildConfigField("String", "BASE_URL", baseUrl.toString())
        val clientId = "f9466f41f83c4266b6b55d1d67649709" // providers.environmentVariable("CLIENT_ID")
        manifestPlaceholders["YANDEX_CLIENT_ID"] = clientId.toString()
    }
}

