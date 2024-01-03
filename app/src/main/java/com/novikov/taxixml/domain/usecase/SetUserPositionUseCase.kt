package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.model.Position
import com.novikov.taxixml.domain.repository.UserPositionRepository

class SetUserPositionUseCase(private val userPositionRepository: UserPositionRepository) {
    suspend fun execute(position: Position){
        userPositionRepository.setPosition(position)
    }
}