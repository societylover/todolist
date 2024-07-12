plugins {
    id("android-core-lib-convention")
}

android {
    namespace = "core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)
}