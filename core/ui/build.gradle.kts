plugins {
    id("android-core-lib-convention")
}

android {
    namespace = "core.ui"
}

dependencies {
    implementation(libs.data.store)
    implementation(projects.core.common)
    implementation(libs.lifecycle.runtime.ktx)
    api(libs.material)
    api(platform(libs.compose.bom))
    api(libs.ui)
    api(libs.ui.graphics)
    api(libs.ui.tooling.preview)
    api(libs.material3)
}