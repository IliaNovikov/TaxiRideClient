package com.novikov.taxixml.di

import com.novikov.taxixml.domain.repository.AddressRepository
import com.novikov.taxixml.domain.repository.OrderRepository
import com.novikov.taxixml.domain.repository.UserDataRepository
import com.novikov.taxixml.domain.repository.UserPositionRepository
import com.novikov.taxixml.domain.usecase.ChangeOrderStatusUseCase
import com.novikov.taxixml.domain.usecase.CreateOrderUseCase
import com.novikov.taxixml.domain.usecase.GetAddressesByStringUseCase
import com.novikov.taxixml.domain.usecase.GetUserDataUseCase
import com.novikov.taxixml.domain.usecase.GetUserPositionUseCase
import com.novikov.taxixml.domain.usecase.SearchByAddressUseCase
import com.novikov.taxixml.domain.usecase.SearchByPointUseCase
import com.novikov.taxixml.domain.usecase.SetUserDataUseCase
import com.novikov.taxixml.domain.usecase.SetUserPositionUseCase
import com.novikov.taxixml.domain.usecase.DeleteOrderUseCase
import com.novikov.taxixml.domain.usecase.GetDriverInfoUseCase
import com.novikov.taxixml.domain.usecase.GetLastOrderUseCase
import com.novikov.taxixml.domain.usecase.GetOrderDataUseCase
import com.novikov.taxixml.domain.usecase.SetDriverRatingUseCase
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

    @Provides
    fun provideSearchByPointUseCase(): SearchByPointUseCase{
        return SearchByPointUseCase()
    }

    @Provides
    fun provideSearchByAddressUseCase(): SearchByAddressUseCase{
        return SearchByAddressUseCase()
    }

    @Provides
    fun provideCreateOrderUseCase(orderRepository: OrderRepository) : CreateOrderUseCase{
        return CreateOrderUseCase(orderRepository)
    }

    @Provides
    fun provideDeleteOrderUseCase(orderRepository: OrderRepository) : DeleteOrderUseCase{
        return DeleteOrderUseCase(orderRepository)
    }

    @Provides
    fun provideGetOrderDataUseCase(orderRepository: OrderRepository) : GetOrderDataUseCase{
        return GetOrderDataUseCase(orderRepository)
    }

    @Provides
    fun provideGetLastOrderUseCase(orderRepository: OrderRepository): GetLastOrderUseCase{
        return GetLastOrderUseCase(orderRepository)
    }

    @Provides
    fun provideGetDriverInfoUseCase(orderRepository: OrderRepository): GetDriverInfoUseCase{
        return GetDriverInfoUseCase(orderRepository)
    }

    @Provides
    fun provideSetDriverRatingUseCase(orderRepository: OrderRepository): SetDriverRatingUseCase{
        return SetDriverRatingUseCase(orderRepository)
    }

    @Provides
    fun provideChangeOrderStatus(orderRepository: OrderRepository): ChangeOrderStatusUseCase{
        return ChangeOrderStatusUseCase(orderRepository)
    }

}