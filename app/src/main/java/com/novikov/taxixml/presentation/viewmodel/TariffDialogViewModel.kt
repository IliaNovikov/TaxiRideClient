package com.novikov.taxixml.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.novikov.taxixml.domain.usecase.CreateOrderUseCase
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TariffDialogViewModel @Inject constructor(
    private val app: Application,
    private val createOrderUseCase: CreateOrderUseCase
) : AndroidViewModel(app) {

    val mldTariff = MutableLiveData("econom")
    val mldPaymentMethod = MutableLiveData(0)

    suspend fun createOrder(){
        UserInfo.orderData.date = LocalDate.now().toString()
        Log.i("orderDate", UserInfo.orderData.date.toString())
        createOrderUseCase.execute(UserInfo.orderData)
    }

}