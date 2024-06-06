package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.repository.OrderRepository

class ChangeOrderStatusUseCase(private val orderRepository: OrderRepository) {

    suspend fun execute(orderId: String, newStatus: String){
        orderRepository.changeOrderStatus(orderId, newStatus)
    }

}