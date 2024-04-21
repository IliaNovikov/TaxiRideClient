package com.novikov.taxixml.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.novikov.taxixml.domain.repository.AddressRepository
import com.novikov.taxixml.domain.usecase.GetAddressesByStringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderTaxiFragmentViewModel @Inject constructor(
    private val app: Application,
    private val getAddressesByStringUseCase: GetAddressesByStringUseCase
) : AndroidViewModel(app) {

    private val searchString = ""
    val addressArray = MutableLiveData<Array<String>>()
    suspend fun getAddressesByString(){
        Log.i("vm", "start")
        addressArray.value = getAddressesByStringUseCase.execute(searchString)
    }
    suspend fun getAddressesByString(string: String){
        Log.i("vm", "start")
        addressArray.value = getAddressesByStringUseCase.execute(string)
    }

}