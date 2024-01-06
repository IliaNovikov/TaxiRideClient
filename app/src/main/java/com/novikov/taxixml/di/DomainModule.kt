package com.novikov.taxixml.di

import com.novikov.taxixml.domain.repository.UserPositionRepository
import com.novikov.taxixml.domain.usecase.GetUserPositionUseCase
import com.novikov.taxixml.domain.usecase.SearchByAddressUseCase
import com.novikov.taxixml.domain.usecase.SetUserPositionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideGetUserPositionUseCase(userPositionRepository: UserPositionRepository): GetUserPositionUseCase{
        return GetUserPositionUseCase(userPositionRepository)
    }

    @Provides
    fun provideSetUserPositionUseCase(userPositionRepository: UserPositionRepository): SetUserPositionUseCase {
        return SetUserPositionUseCase(userPositionRepository)
    }

}