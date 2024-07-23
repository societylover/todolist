import com.android.build.gradle.BaseExtension
import java.time.LocalDateTime
import java.util.Locale

fun BaseExtension.baseAndroidConfig() {
    namespace = AndroidConst.NAMESPACE
    setCompileSdkVersion(AndroidConst.COMPILE_SDK)
    defaultConfig {
        minSdk = AndroidConst.MIN_SDK

        vectorDrawables {
            useSupportLibrary = true
        }

        versionCode = 1
        versionName = "1.0-hw-5"

        buildConfigField("String", "BUILD_TIME", "\"${LocalDateTime.now().asString()}\"")
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

internal fun LocalDateTime.asString(
    pattern: String = "d.MM.yyyy HH:mm:ss",
    locale: Locale = Locale.getDefault()
): String =
    this.format(java.time.format.DateTimeFormatter.ofPattern(pattern, locale))

