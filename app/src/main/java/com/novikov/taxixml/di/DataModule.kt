package com.novikov.taxixml.di

import android.content.Context
import com.novikov.taxixml.data.UserPositionRepositoryImpl
import com.novikov.taxixml.domain.repository.UserPositionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideUserPositionRepository(@ApplicationContext context: Context): UserPositionRepository{
        return UserPositionRepositoryImpl(context)
    }

}