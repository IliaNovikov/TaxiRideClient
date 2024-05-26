package com.novikov.taxixml.domain.usecase

import com.novikov.taxixml.domain.repository.OrderRepository
import com.novikov.taxixml.presentation.view.dialog.RatingDialog

class SetDriverRatingUseCase(private val orderRepository: OrderRepository) {

    suspend fun execute(driverUid: String, rating: Float){
        orderRepository.setDriverRating(driverUid, rating)
    }

}