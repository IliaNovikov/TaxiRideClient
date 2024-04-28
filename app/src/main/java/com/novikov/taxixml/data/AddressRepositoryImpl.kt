package com.novikov.taxixml.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.novikov.taxixml.domain.repository.AddressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CountDownLatch

class AddressRepositoryImpl : AddressRepository {

    private val client = OkHttpClient()
    private var json = ""

    override suspend fun getAddressesByString(string: String): Array<String> {
        try {
            val countDownLatch = CountDownLatch(1)
            Log.i("repo", "start")

            val gson = Gson()

            val request = Request.Builder()
                .url("https://suggest-maps.yandex.ru/v1/suggest?apikey=f9357051-4b65-46bf-b42d-9823b520b6d2&text=${string}&ll=55.9678,54.7431")
                .get()
                .build()

            Log.i("response", "awaiting")

            val response = client.newCall(request).also {

                it.enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("response", e.message.toString())
                        countDownLatch.countDown()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        json = response.body!!.string()
                        countDownLatch.countDown()
//                        Log.i("response", response.body!!.string())
                    }

                })
            }
            withContext(Dispatchers.IO) {
                countDownLatch.await()
            }

            Log.i("json", json)

            val mapAdapter = gson.getAdapter(object : TypeToken<Map<String, Any>>() {})

            val map = mapAdapter.fromJson(json)
            Log.i("results", (map["results"] as List<Any?>).size.toString())

            val addressesList = ArrayList<String>()

            for (i in (map["results"] as List<Any?>)){
                if (((i as Map<*, *>)["subtitle"] as Map<*, *>)["text"].toString().split(",")[0] == "Уфа")
                    addressesList.add(((i as Map<*, *>)["subtitle"] as Map<*, *>)["text"].toString().split(",")[0] + ", " + ((i as Map<*, *>)["title"] as Map<*, *>)["text"].toString())
            }
            Log.i("address", addressesList[1])

            return addressesList.toTypedArray().copyOfRange(0, 3)
        }
        catch (ex : Exception){
            Log.e("repo addresses", ex.message.toString())
            return arrayOf("")
        }

    }
}