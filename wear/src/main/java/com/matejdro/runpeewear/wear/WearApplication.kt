package com.matejdro.runpeewear.wear

import android.app.Application
import logcat.AndroidLogcatLogger

class WearApplication : Application() {
   override fun onCreate() {
      super.onCreate()

      AndroidLogcatLogger.installOnDebuggableApp(this)
   }
}
