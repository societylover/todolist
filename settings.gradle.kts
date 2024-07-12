rootProject.name = "todolist"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":core")
include(":core:network")
include(":core:model")
include(":core:common")
include(":core:datastore")
include(":core:database")
include(":core:ui")
include(":core:data")
include(":core:domain")
include(":app")


pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

