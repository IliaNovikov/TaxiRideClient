package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.model.OrderData
import com.novikov.taxixml.domain.repository.OrderRepository

class GetOrderDataUseCase(private val orderRepository: OrderRepository) {

    suspend fun execute(orderId: String) : OrderData{
        return orderRepository.getOrderData(orderId)
    }

}