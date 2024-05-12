package com.novikov.taxixml.domain.repository

import com.novikov.taxixml.domain.model.OrderData

interface OrderRepository {

    suspend fun createOrder(orderData: OrderData)

    suspend fun changeOrderStatus(status: String)

    suspend fun deleteOrder(orderId: String)
}