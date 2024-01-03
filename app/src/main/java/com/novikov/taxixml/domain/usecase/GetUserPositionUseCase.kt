package com.novikov.taxixml.domain.usecase

import android.util.Log
import com.novikov.taxixml.domain.model.Position
import com.novikov.taxixml.domain.repository.UserPositionRepository

class GetUserPositionUseCase(private val userPositionRepository: UserPositionRepository) {
    suspend fun execute() : Position{
        return userPositionRepository.getPosition()
    }
}