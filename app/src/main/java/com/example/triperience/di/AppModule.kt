package com.example.triperience.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) : SharedPreferences{
        return context.getSharedPreferences("TriperiencePref", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharedPrefUtil(sharedPreferences: SharedPreferences) : SharedPrefUtil{
        return SharedPrefUtil(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }
}