plugins {
   id("com.android.library")
   id("kotlin-android")
   id("com.squareup.wire")
}

android {
   compileSdk = 33

   namespace = "com.matejdro.runpeewear.common"

   defaultConfig {
      minSdk = 26
      targetSdk = 31
   }

   compileOptions {
      sourceCompatibility(JavaVersion.VERSION_11)
      targetCompatibility(JavaVersion.VERSION_11)
   }
}

kotlin {
   jvmToolchain(11)
}

wire {
   kotlin {}
}

dependencies {
   api(libs.wire.runtime)

   implementation(libs.androidx.compose.ui)
}
