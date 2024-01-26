package com.novikov.taxixml.presentation.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.novikov.taxixml.domain.usecase.GetUserDataUseCase
import com.novikov.taxixml.domain.usecase.SetUserDataUseCase
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentViewModel @Inject constructor( private val getUserDataUseCase: GetUserDataUseCase,
    private val setUserDataUseCase: SetUserDataUseCase): ViewModel() {

    var name = MutableLiveData<String>("")
    var phone = MutableLiveData<String>("")


    suspend fun getUserData(){
        name.value = UserInfo.name
        phone.value = UserInfo.phone
    }

}