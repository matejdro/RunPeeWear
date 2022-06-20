package com.matejdro.runpeewear

import android.app.Application
import logcat.AndroidLogcatLogger

class MobileApplication : Application() {
   override fun onCreate() {
      super.onCreate()

      AndroidLogcatLogger.installOnDebuggableApp(this)
   }
}
