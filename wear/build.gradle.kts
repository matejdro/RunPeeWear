plugins {
   id("com.android.application")
   id("dagger.hilt.android.plugin")
   kotlin("android")
   kotlin("kapt")
   id("kotlin-parcelize")
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
      sourceCompatibility(JavaVersion.VERSION_11)
      targetCompatibility(JavaVersion.VERSION_11)
   }

   composeOptions {
      kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
   }
}

kotlin {
   jvmToolchain(11)
}

dependencies {
   implementation(project(":common"))

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.compose.compiler)
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
