plugins {
    id("android-core-lib-convention")
}

android {
    namespace = "core.database"

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/gradle/"
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.kotlinx.coroutines.core)
}