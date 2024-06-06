package com.novikov.taxixml.domain.model

data class HistoryOrder(
    var id: String = "",
    var date: String = "",
    var driverName: String = "",
    var driverUid: String = "",
    var startAddress: String = "",
    var endAddress: String = "",
    var carBrand: String = "",
    var carNumber: String = "")