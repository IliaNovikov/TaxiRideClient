package com.novikov.taxixml.domain.repository

import com.novikov.taxixml.domain.model.DriverInfo
import com.novikov.taxixml.domain.model.OrderData

interface OrderRepository {

    suspend fun createOrder(orderData: OrderData)

    suspend fun changeOrderStatus(orderId: String, status: String)

    suspend fun deleteOrder(orderId: String)

    suspend fun getOrderData(orderId: String): OrderData

    suspend fun getLastOrder(): OrderData

    suspend fun getDriverInfo(driverUid: String): DriverInfo

    suspend fun setDriverRating(driverUid: String, rating: Float)

}