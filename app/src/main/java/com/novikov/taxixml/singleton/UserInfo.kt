package com.novikov.taxixml.singleton

import com.novikov.taxixml.domain.model.Address
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.domain.model.OrderData

object UserInfo {
    var uid = ""
    var name = ""
    var phone = ""
    var cards = arrayListOf<Card>()
    var savedAddresses = arrayListOf<Address>()
    var orderData = OrderData(
        startAddress =  Address("", "", ""),
        endAddress =  Address("", "", ""),
        price = 0f,
        clientUid =  "",
        driverUid =  "")
}