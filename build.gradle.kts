plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false // ✅ أضف هذا
    id("com.google.gms.google-services") version "4.4.3" apply false
    alias(libs.plugins.google.firebase.firebase.perf) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false

}


tasks.register("clean", Delete::class) {
    @Suppress("DEPRECATION")
    delete(rootProject.buildDir)
}
