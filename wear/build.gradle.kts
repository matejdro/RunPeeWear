plugins {
   id("com.android.application")
   kotlin("android")
}

android {
   compileSdk = 31

   defaultConfig {
      applicationId = "com.matejdro.runpeewear.wear"
      minSdk = 26
      targetSdk = 31

      versionCode = 1
      versionName = "1.0"
   }

   buildFeatures {
      compose = true
   }

   compileOptions {
      sourceCompatibility(JavaVersion.VERSION_1_8)
      targetCompatibility(JavaVersion.VERSION_1_8)
   }

   composeOptions {
      kotlinCompilerExtensionVersion = libs.versions.androidx.compose.asProvider().get()
   }

   kotlinOptions {
      jvmTarget = "1.8"
   }
}


dependencies {
   implementation(project(":common"))

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.compose.compiler)
   implementation(libs.androidx.compose.wear.foundation)
   implementation(libs.androidx.compose.wear.material)
   implementation(libs.androidx.compose.wear.navigation)
   implementation(libs.androidx.wear)
   implementation(libs.logcat)
   implementation(libs.playServices.wearable)
}
