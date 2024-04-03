package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentAddAddressBinding
import com.novikov.taxixml.databinding.FragmentGeoInfoBinding

class GeoInfoFragment : Fragment() {

    private lateinit var binding: FragmentGeoInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGeoInfoBinding.inflate(inflater)

        try{
            val address = arguments?.getString("address")

            binding.tvAddress.text = address
        }
        catch (e: Exception){
            Log.e("geo info", e.message.toString())
        }

        return binding.root
    }
}