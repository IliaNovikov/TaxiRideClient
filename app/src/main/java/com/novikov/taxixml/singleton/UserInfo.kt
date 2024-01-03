package com.novikov.taxixml.singleton

import com.novikov.taxixml.domain.model.Address
import com.novikov.taxixml.domain.model.Card

object UserInfo {
    var uid = ""
    var name = ""
    var phone = ""
    var cards =  ArrayList<Card>()
    var savedAddresses = ArrayList<Address>()
}