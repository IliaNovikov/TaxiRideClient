package com.novikov.taxixml.domain.repository

import com.novikov.taxixml.domain.model.Address

interface AddressRepository {
    suspend fun getAddressesByString(string: String): Array<String>

}