package com.matejdro.runpeewear.wear.di

import android.app.Application
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
}
