package com.novikov.taxixml.di

import com.novikov.taxixml.domain.repository.AddressRepository
import com.novikov.taxixml.domain.repository.UserDataRepository
import com.novikov.taxixml.domain.repository.UserPositionRepository
import com.novikov.taxixml.domain.usecase.GetAddressesByStringUseCase
import com.novikov.taxixml.domain.usecase.GetUserDataUseCase
import com.novikov.taxixml.domain.usecase.GetUserPositionUseCase
import com.novikov.taxixml.domain.usecase.SearchByAddressUseCase
import com.novikov.taxixml.domain.usecase.SetUserDataUseCase
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

    @Provides
    fun provideSetUserDataUseCase(userDataRepository: UserDataRepository): SetUserDataUseCase{
        return SetUserDataUseCase(userDataRepository)
    }

    @Provides
    fun provideGetUserDataUseCase(userDataRepository: UserDataRepository): GetUserDataUseCase{
        return GetUserDataUseCase(userDataRepository)
    }

    @Provides
    fun provideGetAddressesByStringUseCase(addressRepository: AddressRepository) : GetAddressesByStringUseCase{
        return GetAddressesByStringUseCase(addressRepository)
    }

}