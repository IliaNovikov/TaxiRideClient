package com.novikov.taxixml.domain.model

import kotlinx.serialization.Serializable

class UserData(val uid: String = "",
               val name: String = "",
               val phone: String = "",
               val cards: List<Card> = arrayListOf(),
               val savedAddresses: List<Address> = arrayListOf())