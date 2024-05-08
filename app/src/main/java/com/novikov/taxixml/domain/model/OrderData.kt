package com.novikov.taxixml.domain.model

import com.yandex.mapkit.geometry.Point

data class OrderData(var startAddress: String,
                     var endAddress: String,
                     var startPoint: Point,
                     var endPoint: Point,
                     var tariff: String,
                     var price: Int,
                     var status: String,
                     var paymentType: String,
                     var clientUid: String,
                     var driverUid: String?)