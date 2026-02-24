plugins {
    // Prevent plugins loading multiple times
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    // ✅ REQUIRED for Firebase
    id("com.google.gms.google-services") version "4.4.2" apply false
}