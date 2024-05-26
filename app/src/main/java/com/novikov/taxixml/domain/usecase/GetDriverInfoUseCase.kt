package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.model.DriverInfo
import com.novikov.taxixml.domain.repository.OrderRepository

class GetDriverInfoUseCase(private val orderRepository: OrderRepository) {

    suspend fun execute(driverUid: String): DriverInfo {
        return orderRepository.getDriverInfo(driverUid)
    }

}