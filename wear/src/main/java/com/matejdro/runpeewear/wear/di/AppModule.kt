package com.matejdro.runpeewear.wear.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Vibrator
import androidx.core.content.getSystemService
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
   @Provides
   fun provideDataClient(application: Application): DataClient {
      return Wearable.getDataClient(application)
   }

   @Provides
   fun provideVibrator(application: Application): Vibrator {
      return application.getSystemService<Vibrator>()!!
   }

   @Provides
   fun provideSharedPreferences(application: Application): SharedPreferences {
      return application.getSharedPreferences("settings", Context.MODE_PRIVATE)
   }
}
