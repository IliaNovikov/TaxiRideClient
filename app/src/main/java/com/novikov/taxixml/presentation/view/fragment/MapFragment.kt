package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentMapBinding
import com.yandex.mapkit.MapKitFactory

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mvMain.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        binding.mvMain.onStop()
        super.onStop()
    }

}