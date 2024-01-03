package com.novikov.taxixml.domain.repository

import com.novikov.taxixml.domain.model.Position

interface UserPositionRepository {
    suspend fun getPosition(): Position
    suspend fun setPosition(position: Position)
}