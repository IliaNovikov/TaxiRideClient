package com.novikov.taxixml.domain.usecase

import android.util.Log
import com.novikov.taxixml.domain.repository.AddressRepository

class GetAddressesByStringUseCase(private val addressRepository: AddressRepository) {
    suspend fun execute(string: String) : Array<String>{
        Log.i("uc", "start")
        return addressRepository.getAddressesByString(string)
    }
}