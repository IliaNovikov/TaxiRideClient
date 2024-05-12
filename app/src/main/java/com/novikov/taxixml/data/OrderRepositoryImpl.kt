package com.novikov.taxixml.data

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.novikov.taxixml.domain.model.OrderData
import com.novikov.taxixml.domain.repository.OrderRepository
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {

    private val dbRef = Firebase.database.reference
    private var key = ""

    override suspend fun createOrder(orderData: OrderData) {
        try {
            key = dbRef.child("orders").push().key!!
            val map = HashMap<String, Any>()
            map[key] = orderData
            dbRef.child("orders").updateChildren(map)
        }
        catch (ex: Exception){
            Log.e("createOrder", ex.message.toString())
        }
    }

    override suspend fun changeOrderStatus(status: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrder(orderId: String) {
        try {
            dbRef.child("orders").child(key).removeValue().await()
//            Log.i("order", dbRef.child("orders").child(key).get().result.value.toString())
        }
        catch (ex: Exception){
            Log.e("deleteOrder", ex.message.toString())
        }
    }

}