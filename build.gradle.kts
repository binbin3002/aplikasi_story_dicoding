// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.24" apply false
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.0" apply false

}

buildscript{
    repositories{
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.androidx.androidx.navigation.safeargs.gradle.plugin)
        classpath(libs.symbol.processing.api)

       }
}

