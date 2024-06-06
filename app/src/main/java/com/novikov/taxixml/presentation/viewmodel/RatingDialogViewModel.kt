package com.novikov.taxixml.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.novikov.taxixml.domain.usecase.ChangeOrderStatusUseCase
import com.novikov.taxixml.domain.usecase.SetDriverRatingUseCase
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RatingDialogViewModel @Inject constructor(
    private val setDriverRatingUseCase: SetDriverRatingUseCase,
    private val changeOrderStatusUseCase: ChangeOrderStatusUseCase) : ViewModel() {

    suspend fun setDriverRating(rating: Float){
        setDriverRatingUseCase.execute(UserInfo.orderData.driverUid.toString(), rating)
    }
    suspend fun setOrderStatus(orderId: String, newStatus: String){
        changeOrderStatusUseCase.execute(orderId, newStatus)
    }

}