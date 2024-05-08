package com.novikov.taxixml.data

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.novikov.taxixml.domain.model.OrderData
import com.novikov.taxixml.domain.repository.OrderRepository

class OrderRepositoryImpl : OrderRepository {

    private val dbRef = Firebase.database.reference

    override suspend fun createOrder(orderData: OrderData) {
        val key = dbRef.child("orders").push().key
        val map = HashMap<String, Any>()
        map[key!!] = orderData
        dbRef.child("orders").updateChildren(map)
    }

    override suspend fun changeOrderStatus(status: String) {
        TODO("Not yet implemented")
    }

}