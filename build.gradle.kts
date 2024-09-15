// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}

tasks.withType<Copy>().all {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.aliucord.com/snapshots")
        maven("https://jitpack.io")
    }
}
