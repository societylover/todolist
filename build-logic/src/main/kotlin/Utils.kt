import com.android.build.gradle.BaseExtension

fun BaseExtension.baseAndroidConfig() {
    namespace = AndroidConst.NAMESPACE
    setCompileSdkVersion(AndroidConst.COMPILE_SDK)
    defaultConfig {
        minSdk = AndroidConst.MIN_SDK

        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = AndroidConst.COMPILE_JDK_VERSION
        targetCompatibility = AndroidConst.COMPILE_JDK_VERSION
    }
    kotlinOptions {
        jvmTarget = AndroidConst.KOTLIN_JVM_TARGET
    }
}

