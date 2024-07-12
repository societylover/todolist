import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

configure<BaseAppModuleExtension> {
    baseAndroidConfig()

    val clientId: String by project
    val baseUrl: String by project
    val appStoreFile: String by project
    val appStorePassword: String by project
    val appKeyAlias: String by project
    val appKeyPassword: String by project

    defaultConfig {
        buildConfigField("String", "BASE_URL", baseUrl)
        manifestPlaceholders["YANDEX_CLIENT_ID"] = clientId
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    testImplementation(libs.junit)
}
