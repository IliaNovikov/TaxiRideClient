package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentOrderTaxiBinding
import com.novikov.taxixml.singleton.NavigationController

class OrderTaxiFragment : Fragment() {

    private lateinit var binding: FragmentOrderTaxiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentOrderTaxiBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSettings.setOnClickListener {
            NavigationController.navHost.navigate(R.id.settingsFragment)
        }
        binding.btnToStartAddress.setOnClickListener {
            NavigationController.navHost.navigate(R.id.mainFragment)
        }
        binding.btnToEndAddress.setOnClickListener {
            NavigationController.navHost.navigate(R.id.mainFragment)
        }
    }
}