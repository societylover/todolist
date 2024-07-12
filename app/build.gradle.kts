@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization) version libs.versions.kotlin
    kotlin("kapt")

    id("telegram-reporter")
}

android {
    namespace = "com.homework.todolist"
    compileSdk = 34

    val clientId: String by project
    val baseUrl: String by project
    val appStoreFile: String by project
    val appStorePassword: String by project
    val appKeyAlias: String by project
    val appKeyPassword: String by project

    defaultConfig {
        applicationId = "com.homework.todolist"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Replace with client_id like: manifestPlaceholders["YANDEX_CLIENT_ID"] = "123"
        manifestPlaceholders["YANDEX_CLIENT_ID"] = clientId
        // Replace with basic url like: buildConfigField("String", "BASE_URL", "https://www.example.com/list")
        buildConfigField("String", "BASE_URL", baseUrl)
    }

    signingConfigs {
        create("release") {
            keyAlias = appKeyAlias
            keyPassword = appKeyPassword
            storeFile = file(appStoreFile)
            storePassword = appStorePassword
        }
    }

    buildTypes {
        all {
            signingConfig = signingConfigs.getAt("release")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        compose = true
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

//tgReporter {
//    token.set(providers.environmentVariable("TG_TOKEN"))
//    chatId.set(providers.environmentVariable("TG_CHAT"))
//}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.material)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    kapt(libs.hilt)
    implementation(libs.androidx.hilt.work)
    kapt(libs.hilt.compiler)

    implementation(libs.compose.lifecycle)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.data.store)

    implementation(libs.auth.sdk)
    implementation(libs.bundles.ktor)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.work.manager)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}