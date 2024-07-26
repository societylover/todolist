@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("android-app-convention")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization) version libs.versions.kotlin
    kotlin("kapt")
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

android {
    flavorDimensions += "plan"
    productFlavors {
        create("paid") {
            dimension = "plan"
            buildConfigField("boolean", "PAID",  "true")
        }
        create("free") {
            dimension = "plan"
            buildConfigField("boolean", "PAID",  "false")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.material3)
    implementation(libs.material)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.compose.lifecycle)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.data.store)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.com.google.android.material.material)
    implementation(libs.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.ui.viewbinding)

    kapt(libs.room.compiler)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    kapt(libs.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.auth.sdk)

    implementation(libs.work.manager)

    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.bundles.ktor)
    implementation(libs.bundles.divkit)
    testImplementation(libs.bundles.testing.ui)
    androidTestImplementation(libs.bundles.testing.unit)
    androidTestImplementation(libs.espresso.core)
}