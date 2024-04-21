package com.novikov.taxixml.domain.model

data class OrderData(var startAddress: Address, var endAddress: Address, var price: Float, var clientUid: String, var driverUid: String)