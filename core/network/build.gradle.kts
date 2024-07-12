@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("android-core-lib-convention")
}

android {
    namespace = "core.network"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.datastore)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.coroutines.core)
}