package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.repository.OrderRepository

class DeleteOrderUseCase(private val orderRepository: OrderRepository) {

    suspend fun execute(orderId: String){

        orderRepository.deleteOrder(orderId)

    }

}