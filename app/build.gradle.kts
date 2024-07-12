@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("android-app-convention")
    kotlin("kapt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(projects.core.ui)

    implementation(libs.material3)
    implementation(libs.material)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.compose.lifecycle)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(project(":core:datastore"))
    kapt(libs.room.compiler)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    kapt(libs.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.auth.sdk)

    implementation(libs.work.manager)
}