package com.novikov.taxixml.domain.model

import com.yandex.mapkit.geometry.Point
import java.time.LocalDate
import java.util.Date

data class OrderData(var startAddress: String = "",
                     var endAddress: String = "",
                     var startPoint: Point = Point(),
                     var endPoint: Point = Point(),
                     var tariff: String = "",
                     var price: Int = 0,
                     var status: String = "",
                     var paymentType: String = "",
                     var clientUid: String = "",
                     var driverUid: String? = "",
                     var date: String = "")