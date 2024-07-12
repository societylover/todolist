plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    baseAndroidConfig()
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.hilt.android)
    api(libs.hilt.compiler)
}
