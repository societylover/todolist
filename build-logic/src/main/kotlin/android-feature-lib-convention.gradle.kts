plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    baseAndroidConfig()
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
//    implementation(libs.core.ktx)
//    implementation(libs.lifecycle.runtime.ktx)
//    implementation(libs.activity.compose)
//    implementation(platform(libs.compose.bom))
//    implementation(libs.compose.ui)
//    implementation(libs.compose.ui.graphics)
//    implementation(libs.compose.ui.tooling.preview)
//    implementation(libs.compose.material3)
//    implementation(libs.compose.navigation)
//    testImplementation(libs.junit)
}
