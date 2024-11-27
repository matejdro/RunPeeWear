plugins {
   id("com.android.application")
   id("dagger.hilt.android.plugin")
   kotlin("android")
   kotlin("kapt")
   id("kotlin-parcelize")
   id("org.jetbrains.kotlin.plugin.compose")
}

android {
   compileSdk = 34

   namespace = "com.matejdro.runpeewear"

   defaultConfig {
      applicationId = "com.matejdro.runpeewear"
      minSdk = 26
      targetSdk = 32

      versionCode = 1
      versionName = "1.0"
   }

   buildFeatures {
      compose = true
   }

   compileOptions {
      sourceCompatibility(JavaVersion.VERSION_17)
      targetCompatibility(JavaVersion.VERSION_17)

      isCoreLibraryDesugaringEnabled = true
   }
}

kotlin {
   jvmToolchain(17)
}

dependencies {
   implementation(project(":common"))

   coreLibraryDesugaring(libs.desugarJdkLib)

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.compose.wear.foundation)
   implementation(libs.horologist.layout)
   implementation(libs.androidx.wear)
   implementation(libs.androidx.compose.wear.material)
   implementation(libs.androidx.compose.wear.navigation)
   implementation(libs.dagger.hilt.runtime)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlin.coroutines.playServices)
   implementation(libs.logcat)
   implementation(libs.playServices.wearable)

   kapt(libs.dagger.hilt.compiler)
}
