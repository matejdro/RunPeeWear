package com.matejdro.runpeewear

import android.app.Application
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger

@HiltAndroidApp
class MobileApplication : Application() {
   override fun onCreate() {
      super.onCreate()

      AndroidLogcatLogger.installOnDebuggableApp(this)

      Shell.enableVerboseLogging = BuildConfig.DEBUG;
      Shell.setDefaultBuilder(
         Shell.Builder.create()
            .setFlags(Shell.FLAG_REDIRECT_STDERR or Shell.FLAG_MOUNT_MASTER)
            .setTimeout(10)
      );
   }
}
