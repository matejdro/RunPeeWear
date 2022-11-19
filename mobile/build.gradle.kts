plugins {
   id("com.android.application")
   id("dagger.hilt.android.plugin")
   kotlin("android")
   kotlin("kapt")
}

android {
   compileSdk = 33

   defaultConfig {
      applicationId = "com.matejdro.runpeewear"
      minSdk = 26
      targetSdk = 31

      versionCode = 1
      versionName = "1.0"
   }

   compileOptions {
      sourceCompatibility(JavaVersion.VERSION_1_8)
      targetCompatibility(JavaVersion.VERSION_1_8)
   }

   buildFeatures {
      compose = true
   }

   composeOptions {
      kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
   }

   kotlinOptions {
      jvmTarget = "1.8"
   }
}

dependencies {
   implementation(project(":common"))

   implementation(libs.androidx.activity)
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.lifecycle.viewmodel)
   implementation(libs.androidx.compose.foundation)
   implementation(libs.androidx.compose.material)
   implementation(libs.androidx.compose.ui)
   implementation(libs.dagger.hilt.runtime)
   implementation(libs.kotlin.coroutines.playServices)
   implementation(libs.logcat)
   implementation(libs.libsu)
   implementation(libs.libsu.io)
   implementation(libs.playServices.wearable)
   kapt(libs.dagger.hilt.compiler)
}
