package com.novikov.taxixml.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.novikov.taxixml.domain.model.UserData
import com.novikov.taxixml.domain.usecase.GetOrderDataUseCase
import com.novikov.taxixml.domain.usecase.GetUserDataUseCase
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getOrderDataUseCase: GetOrderDataUseCase): ViewModel() {

    var uid: MutableLiveData<String> = MutableLiveData()

    var userData = UserData()
    suspend fun getUserData(){
        userData = getUserDataUseCase.execute(uid.value.toString())

    }

    suspend fun getOrderData(orderId: String){
        UserInfo.orderData = getOrderDataUseCase.execute(orderId)
    }
}