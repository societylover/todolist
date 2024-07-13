import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        buildConfigField("String", "BASE_URL", providers.environmentVariable("BASE_URL").toString())
        manifestPlaceholders["YANDEX_CLIENT_ID"] = providers.environmentVariable("CLIENT_ID")
    }
}

dependencies {
}
