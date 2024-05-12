package com.novikov.taxixml.domain.model

import com.yandex.mapkit.geometry.Point
import java.time.LocalDate
import java.util.Date

data class OrderData(var startAddress: String,
                     var endAddress: String,
                     var startPoint: Point,
                     var endPoint: Point,
                     var tariff: String,
                     var price: Int,
                     var status: String,
                     var paymentType: String,
                     var clientUid: String,
                     var driverUid: String?,
                     var date: String)