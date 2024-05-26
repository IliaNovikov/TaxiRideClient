package com.novikov.taxixml.singleton

import com.novikov.taxixml.domain.model.Address
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.domain.model.OrderData
import com.yandex.mapkit.geometry.Point
import java.time.LocalDate

object UserInfo {
    var uid = ""
    var name = ""
    var phone = ""
    var cards = arrayListOf<Card>()
    var savedAddresses = arrayListOf<Address>()
    var orderId = ""
    var orderData = OrderData(
        startAddress =  "",
        endAddress =  "",
        startPoint = Point(),
        endPoint = Point(),
        tariff = "",
        status = "",
        paymentType = "",
        price = 0,
        clientUid =  "",
        driverUid =  "",
        date = LocalDate.now().toString())
}