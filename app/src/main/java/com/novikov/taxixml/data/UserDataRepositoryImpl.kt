package com.novikov.taxixml.data

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.novikov.taxixml.domain.model.Address
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.domain.model.UserData
import com.novikov.taxixml.domain.repository.UserDataRepository
import kotlinx.coroutines.tasks.await
import java.util.ArrayList

class UserDataRepositoryImpl : UserDataRepository {

    private val dbRef = Firebase.database.reference

    override suspend fun setData(data: UserData): Boolean {

        Log.i("user repo", "start")
        var result = false

        try {
//            val json = Json.encodeToString(data)
//            val gson = Gson()
//
//            val json = gson.toJson(data)
//
//            dbRef.child("clients").child(data.uid).setValue(json).addOnCompleteListener {
//                result = it.isSuccessful
//            }.await()


            val ref = dbRef.child("clients").child(data.uid)

            val map = HashMap<String, Any>()

            map["name"] = data.name
            map["phone"] = data.phone

            ref.setValue(map).addOnCompleteListener {
                result = it.isSuccessful
            }.await()

            val cardsPushedKey = dbRef.child("clients").child(data.uid).child("cards")
//            for (i in data.cards){
//                cardsPushedKey.setValue(i).await()
//            }
            cardsPushedKey.setValue(data.cards)

            val addressesPushedKey = dbRef.child("clients").child(data.uid).child("addresses")
            addressesPushedKey.setValue(data.savedAddresses)

//            dbRef.child("clients").child(data.uid).child("name").setValue(data.name).addOnCompleteListener {
//                result = it.isSuccessful
//            }.await()
        }
        catch (e: Exception){
            Log.e("data repo", e.message.toString())
        }

        Log.i("user repo", "end")

        return result
    }

    override suspend fun getData(uid: String): UserData {

        val cardsList = ArrayList<Card>()
        val addressesList = ArrayList<Address>()

        Log.i("user repo get", "start")

        try {
//            val gson = Gson()
//
//            val json = dbRef.child("clients").child(uid).get().await().value
//
//            Log.i("json", json)
//
//            Log.i("user repo get name", userData.name)

            for(item in dbRef.child("clients").child(uid).child("cards").get().await().children){
                val card = Card(number = item.child("number").value.toString(),
                    month = item.child("month").value.toString().toInt(),
                    year = item.child("year").value.toString().toInt(),
                    cvc = item.child("cvc").value.toString().toInt())
                cardsList.add(card)
                Log.i("uc 1 card", (item.child("number").value.toString()))
            }

            for(item in dbRef.child("clients").child(uid).child("addresses").get().await().children){
                val address = Address(city = item.child("city").value.toString(),
                    street = item.child("street").value.toString(),
                    house = item.child("house").value.toString())
                addressesList.add(address)
                Log.i("uc 1 address", (item.child("number").value.toString()))
            }

            Log.i("uc cards", dbRef.child("clients").child(uid).child("cards").get().await().value.toString())

            val userData = UserData(
                name = dbRef.child("clients").child(uid).child("name").get().await().value.toString(),
                phone = dbRef.child("clients").child(uid).child("phone").get().await().value.toString(),
                uid = uid,
                cards = cardsList,
                savedAddresses = addressesList
            )

            return userData
        }
        catch (e: Exception){
            Log.e("user repo", e.message.toString())

            return UserData()
        }
    }
}