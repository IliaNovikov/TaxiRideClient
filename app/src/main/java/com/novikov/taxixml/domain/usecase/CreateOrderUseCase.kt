package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.model.OrderData
import com.novikov.taxixml.domain.repository.OrderRepository

class CreateOrderUseCase(private val orderRepository: OrderRepository) {
    suspend fun execute(orderData: OrderData){
        orderRepository.createOrder(orderData)
    }
}