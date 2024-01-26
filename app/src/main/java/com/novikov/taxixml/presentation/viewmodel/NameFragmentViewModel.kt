package com.novikov.taxixml.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.novikov.taxixml.domain.model.UserData
import com.novikov.taxixml.domain.usecase.SetUserDataUseCase
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NameFragmentViewModel @Inject constructor(private val setUserDataUseCase: SetUserDataUseCase): ViewModel() {

    suspend fun setUserData(){
        Log.i("name vm", "start")
        val userData = UserData(
            UserInfo.uid,
            UserInfo.name,
            UserInfo.phone,
            UserInfo.cards,
            UserInfo.savedAddresses
        )

        setUserDataUseCase.execute(userData)
        Log.i("name vm", "end")
    }

}