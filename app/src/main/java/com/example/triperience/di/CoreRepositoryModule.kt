package com.example.triperience.di

import com.example.triperience.features.weatherInfo.data.repository.WeatherRepositoryImpl
import com.example.triperience.features.weatherInfo.domain.repository.WeatherRepository
import com.example.triperience.utils.core.CoreRepository
import com.example.triperience.utils.core.CoreRepositoryImpl
import com.example.triperience.utils.core.GetPostsPublisher
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCoreRepository(coreRepositoryImpl: CoreRepositoryImpl): CoreRepository


}