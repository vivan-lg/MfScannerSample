plugins {
    alias(libs.plugins.android.application) apply false
    kotlin("android") version libs.versions.kotlin.get() apply false
    alias(libs.plugins.dagger.hilt) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
