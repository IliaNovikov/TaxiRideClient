package com.novikov.taxixml.domain.repository

import com.novikov.taxixml.domain.model.UserData

interface UserDataRepository {
    suspend fun setData(data: UserData): Boolean

    suspend fun getData(uid: String): UserData
}