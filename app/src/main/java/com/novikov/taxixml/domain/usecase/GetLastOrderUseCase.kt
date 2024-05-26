package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.model.OrderData
import com.novikov.taxixml.domain.repository.OrderRepository

class GetLastOrderUseCase(private val orderRepository: OrderRepository) {

    suspend fun execute() : OrderData{
        return orderRepository.getLastOrder()
    }

}