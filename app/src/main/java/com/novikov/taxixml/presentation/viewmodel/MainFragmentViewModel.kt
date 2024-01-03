package com.novikov.taxixml.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.novikov.taxixml.domain.model.Position
import com.novikov.taxixml.domain.usecase.GetUserPositionUseCase
import com.novikov.taxixml.domain.usecase.SetUserPositionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val app: Application,
    private val getUserPositionUseCase: GetUserPositionUseCase,
    private val setUserPositionUseCase: SetUserPositionUseCase
) : AndroidViewModel(app) {

    var position: MutableLiveData<Position> = MutableLiveData()

    suspend fun getCurrentPosition(){
        position.value = getUserPositionUseCase.execute()
    }

}