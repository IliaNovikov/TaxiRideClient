package com.novikov.taxixml.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TariffDialogViewModel @Inject constructor(private val app: Application) : AndroidViewModel(app) {

    val mldTariff = MutableLiveData("econom")

}