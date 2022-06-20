plugins {
   id("com.android.application")
   kotlin("android")
}

android {
   compileSdk = 31

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

   kotlinOptions {
      jvmTarget = "1.8"
   }
}

dependencies {
   implementation(project(":common"))

   implementation(libs.androidx.activity)
   implementation(libs.logcat)
}
