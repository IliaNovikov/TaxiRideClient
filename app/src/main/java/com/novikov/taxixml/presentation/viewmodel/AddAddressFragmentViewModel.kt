package com.novikov.taxixml.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.Address
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.domain.model.UserData
import com.novikov.taxixml.domain.usecase.SetUserDataUseCase
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
    class AddAddressFragmentViewModel @Inject constructor(private val setUserDataUseCase: SetUserDataUseCase,
                                                      private val app: Application) : AndroidViewModel(app) {

    val city = MutableLiveData("")
    val cityErrorEnabled = MutableLiveData(false)
    val cityError = MutableLiveData("")

    val street = MutableLiveData("")
    val streetErrorEnabled = MutableLiveData(false)
    val streetError = MutableLiveData("")

    val house = MutableLiveData("")
    val houseErrorEnabled = MutableLiveData(false)
    val houseError = MutableLiveData("")

    var canAddAddress = MutableLiveData(false)

    fun validate(){
        Log.i("city vm", city.value.toString())
        canAddAddress.value = false

        if(city.value.isNullOrEmpty()){
            cityError.value = app.getString(R.string.required)
            cityErrorEnabled.value = true
        }
        else
            cityErrorEnabled.value = false

        if(street.value.isNullOrEmpty()){
            streetError.value = app.getString(R.string.required)
            streetErrorEnabled.value = true
        }
        else
            streetErrorEnabled.value = false

        if(house.value.isNullOrEmpty()){
            houseError.value = app.getString(R.string.required)
            houseErrorEnabled.value = true
        }
        else
            houseErrorEnabled.value = false

        if (cityErrorEnabled.value == false
            && streetErrorEnabled.value == false
            && houseErrorEnabled.value == false)
            canAddAddress.value = true
    }
    suspend fun addAddress() {
        try {
            UserInfo.savedAddresses.add(
                Address(
                    city = city.value.toString(),
                    street = street.value.toString(),
                    house = house.value.toString()
                )
            )

            setUserDataUseCase.execute(
                UserData(
                    uid = UserInfo.uid,
                    name = UserInfo.name,
                    phone = UserInfo.phone,
                    cards = UserInfo.cards,
                    savedAddresses = UserInfo.savedAddresses
                )
            )
        }
        catch (e: Exception) {
            Log.e("add card exeption", e.message.toString())
        }
    }
}