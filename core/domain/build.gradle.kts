plugins {
    id("android-core-lib-convention")
}

android {
    namespace = "core.domain"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
}