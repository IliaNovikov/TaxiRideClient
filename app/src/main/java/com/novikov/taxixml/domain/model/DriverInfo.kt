package com.novikov.taxixml.domain.model

import com.google.firebase.auth.PhoneAuthProvider

data class DriverInfo(var uid: String = "",
    var phone: String = "",
    var name: String = "",
    var rating: Float = 0f,
    var carBrand: String = "",
    var carNumber: String = "")
