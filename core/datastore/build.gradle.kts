plugins {
    id("android-core-lib-convention")
}

android {
    namespace = "core.datastore"
}

dependencies {
    implementation(libs.data.store)
    implementation(projects.core.common)
}