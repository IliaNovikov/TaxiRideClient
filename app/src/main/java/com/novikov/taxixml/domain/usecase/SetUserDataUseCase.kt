package com.novikov.taxixml.domain.usecase

import android.util.Log
import com.novikov.taxixml.domain.model.UserData
import com.novikov.taxixml.domain.repository.UserDataRepository

class SetUserDataUseCase(private val userDataRepository: UserDataRepository) {
    suspend fun execute(data: UserData){
        Log.i("user uc", "start")
        userDataRepository.setData(data)
    }
}