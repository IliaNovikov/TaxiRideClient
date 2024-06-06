package com.novikov.taxixml.data

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.database.snapshots
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.novikov.taxixml.domain.model.DriverInfo
import com.novikov.taxixml.domain.model.OrderData
import com.novikov.taxixml.domain.repository.OrderRepository
import com.novikov.taxixml.singleton.UserInfo
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {

    private val dbRef = Firebase.database.reference
    private var key = ""

    override suspend fun createOrder(orderData: OrderData){
        try {
            key = dbRef.child("orders").push().key!!
            val map = HashMap<String, Any>()
            map[key] = orderData
            dbRef.child("orders").updateChildren(map)
            UserInfo.orderId = key
        }
        catch (ex: Exception){
            Log.e("createOrder", ex.message.toString())
        }
    }

    override suspend fun changeOrderStatus(orderId: String, status: String) {
        dbRef.child("orders").child(orderId).child("status").setValue(status).await()
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

    override suspend fun getOrderData(orderId: String) : OrderData {
        val gson = Gson()
        val adapter = gson.getAdapter(object : TypeToken<Map<String, Any>>() {})
        var orderData = OrderData()
        var map: Map<String, Any> = mapOf()
        try {
            dbRef.child("orders").child(orderId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val data = gson.toJson(it.result.value)
                    map = adapter.fromJson(data)

                    Log.i("map", map.toString())
                    val startPointData = gson.toJson(map["startPoint"])
                    Log.i("startPointData", startPointData)
                    val startPointMap = adapter.fromJson(startPointData)

                    val endPointData = gson.toJson(map["endPoint"])
                    Log.i("startPointData", endPointData)
                    val endPointMap = adapter.fromJson(endPointData)

                    orderData.apply {
                        this.clientUid = map["clientUid"].toString()
                        this.date = map["date"].toString()
                        this.driverUid = map["driverUid"].toString()
                        this.startPoint = Point(
                            startPointMap["latitude"].toString().toDouble(),
                            startPointMap["longitude"].toString().toDouble()
                        )
                        this.endPoint = Point(
                            endPointMap["latitude"].toString().toDouble(),
                            endPointMap["longitude"].toString().toDouble()
                        )
                        this.startAddress = map["startAddress"].toString()
                        this.endAddress = map["endAddress"].toString()
                        this.paymentType = map["paymentType"].toString()
                        this.price = map["price"].toString().toFloat().toInt()
                        this.tariff = map["tariff"].toString()
                        this.status = map["status"].toString()
                    }
                    Log.i("orderData", orderData.startPoint.latitude.toString())

                }
            }.await()

        }
        catch (ex: Exception){
            Log.i("getOrderData", ex.message.toString())
        }
        return orderData
    }

    override suspend fun getLastOrder(): OrderData {
        var key = ""
        try {
            dbRef.child("orders").orderByChild("clientUid").equalTo(UserInfo.uid).limitToLast(1).get().addOnCompleteListener {
                Log.i("lastOrder", it.result.value.toString())
                if (it.result.hasChildren())
                    key = it.result.children.last().key.toString()
            }.await()
            Log.i("key", key)
            if (!key.isNullOrEmpty()){
                val orderData = getOrderData(key)
//                UserInfo.orderId = key
                return orderData
            }
            else
                return OrderData()

        }
        catch (ex: Exception){
            Log.e("lastOrder", ex.message.toString())
        }
        return OrderData()
    }

    override suspend fun getDriverInfo(driverUid: String): DriverInfo {
        val driverInfo = DriverInfo()
        try{
            dbRef.child("applications").child(driverUid).child("personal").get().addOnCompleteListener {
                driverInfo.uid = driverUid
                driverInfo.phone = it.result.child("phone").value.toString()
                driverInfo.name = it.result.child("name").value.toString()
            }.await()
            dbRef.child("applications").child(driverUid).child("car").get().addOnCompleteListener {
                driverInfo.carBrand = it.result.child("carBrand").value.toString()
                driverInfo.carNumber = it.result.child("carNumber").value.toString()
            }.await()
            dbRef.child("applications").child(driverUid).child("driver").get().addOnCompleteListener {
                Log.i("rating", it.result.child("rating").value.toString())
                driverInfo.rating = it.result.child("rating").value.toString().toFloat()
            }.await()
        }
        catch (ex: Exception){
            Log.e("getDriverInfo", ex.message.toString())
        }
        return driverInfo
    }

    override suspend fun setDriverRating(driverUid: String, rating: Float) {

        var newRating = 0f

        dbRef.child("applications").child(driverUid).child("driver").child("rating").get().addOnCompleteListener {
            newRating = if (it.result.value.toString().toFloat() == 0f)
                rating
            else
                (it.result.value.toString().toFloat() + rating) / 2
        }.await()
        dbRef.child("applications").child(driverUid).child("driver").child("rating").setValue(newRating).await()
    }

}