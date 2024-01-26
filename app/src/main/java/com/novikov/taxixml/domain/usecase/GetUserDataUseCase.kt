package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.model.UserData
import com.novikov.taxixml.domain.repository.UserDataRepository

class GetUserDataUseCase(private val userDataRepository: UserDataRepository) {

    suspend fun execute(uid: String): UserData{
        return userDataRepository.getData(uid)
    }
}