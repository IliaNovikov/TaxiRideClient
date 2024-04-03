package com.novikov.taxixml.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.domain.model.UserData
import com.novikov.taxixml.domain.usecase.SetUserDataUseCase
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddCardFragmentViewModel @Inject constructor(private val setUserDataUseCase: SetUserDataUseCase,
    private val app: Application
): AndroidViewModel(app) {
    val cardNumber = MutableLiveData<String>("")
    val cardNumberErrorEnabled = MutableLiveData<Boolean>(false)
    val cardNumberError = MutableLiveData<String>("")

    val cardMonthYear = MutableLiveData<String>("")
    val cardMonthYearErrorEnabled = MutableLiveData<Boolean>(false)
    val cardMonthYearError = MutableLiveData<String>("")

    val cardCVC = MutableLiveData<String>("")
    val cardCVCErrorEnabled = MutableLiveData<Boolean>(false)
    val cardCVCError = MutableLiveData<String>("")

    val canAddCard = MutableLiveData<Boolean>(false)

    fun validate(){
        canAddCard.value = false
        Log.i("vm cardNumber", cardNumber.value.toString())
        if (cardNumber.value?.length != 16){
            cardNumberError.value = app.getString(R.string.less_than_16_digits)
            cardNumberErrorEnabled.value = true
        }
        else
            cardNumberErrorEnabled.value = false

        if (cardMonthYear.value?.length != 4){
            cardMonthYearError.value = app.getString(R.string.less_than_4_digits)
            cardMonthYearErrorEnabled.value = true
        }
        else{
            if (cardMonthYear.value!!.substring(0, 2).toInt() > 12
                || ("20" + cardMonthYear.value!!.substring(2, 4)).toInt() < LocalDateTime.now().year){
                cardMonthYearError.value = app.getString(R.string.incorrect_date)
                cardMonthYearErrorEnabled.value = true
            }
            else
                cardMonthYearErrorEnabled.value = false
        }

        if (cardCVC.value?.length != 3){
            cardCVCError.value = app.getString(R.string.less_than_3_digits)
            cardCVCErrorEnabled.value = true
        }
        else
            cardCVCErrorEnabled.value = false

        if(cardNumberErrorEnabled.value == false
            && cardMonthYearErrorEnabled.value == false
            && cardCVCErrorEnabled.value == false){
            canAddCard.value = true
        }
    }
    suspend fun addCard(){
        try{
            Log.i("addCard", "start")
            Log.i("month", cardMonthYear.value!!.substring(0, 2))
            Log.i("year", cardMonthYear.value!!.substring(2, 4))
            Log.i("cvc", cardCVC.value!!)

            UserInfo.cards.add(Card(number = cardNumber.value.toString(),
                month = cardMonthYear.value!!.substring(0, 2).toInt(),
                year = cardMonthYear.value!!.substring(2, 4).toInt(),
                cvc = cardCVC.value!!.toInt()))

            Log.i("addCard", "usecase start")

            setUserDataUseCase.execute(UserData(uid = UserInfo.uid,
                name = UserInfo.name,
                phone = UserInfo.phone,
                cards = UserInfo.cards,
                savedAddresses = UserInfo.savedAddresses))
        }
        catch (e: Exception){
            Log.e("add card exeption", e.message.toString())
        }

    }
}