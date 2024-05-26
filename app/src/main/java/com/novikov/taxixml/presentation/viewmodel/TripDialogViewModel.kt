package com.novikov.taxixml.presentation.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.novikov.taxixml.domain.model.DriverInfo
import com.novikov.taxixml.domain.usecase.GetDriverInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TripDialogViewModel @Inject constructor(private val getDriverInfoUseCase: GetDriverInfoUseCase) : ViewModel() {

    val driverInfo = MutableLiveData(DriverInfo())

    suspend fun getDriverInfo(driverUid: String){

        driverInfo.value = getDriverInfoUseCase.execute(driverUid)

    }

}