package com.novikov.taxixml.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.novikov.taxixml.domain.usecase.DeleteOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TripAwaitingFragmentViewModel @Inject constructor(private val deleteOrderUseCase: DeleteOrderUseCase) : ViewModel() {

    val orderId = MutableLiveData("")

    suspend fun deleteOrder(){
        try {
            deleteOrderUseCase.execute(orderId.value!!)
        }
        catch (ex: Exception){
            Log.e("deleteOrderVM", ex.message.toString())
        }
    }

}