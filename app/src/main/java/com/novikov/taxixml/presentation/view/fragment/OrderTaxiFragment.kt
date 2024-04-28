package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentOrderTaxiBinding
import com.novikov.taxixml.presentation.viewmodel.OrderTaxiFragmentViewModel
import com.novikov.taxixml.singleton.NavigationController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderTaxiFragment : Fragment() {

    private lateinit var binding: FragmentOrderTaxiBinding
    private val viewModel : OrderTaxiFragmentViewModel by viewModels()

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
            NavigationController.navHost.navigate(R.id.mapFragment)
        }
        binding.btnToEndAddress.setOnClickListener {
            NavigationController.navHost.navigate(R.id.mainFragment)
        }

        binding.etStartAddress.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getAddressesByString(it.toString())
            }
        }

        binding.etEndAddress.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getAddressesByString(it.toString())
            }
        }

        viewModel.addressArray.observe(requireActivity(), Observer {
            if (binding.etStartAddress.isFocused)
                binding.etStartAddress.setAdapter(ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, it))
            else if (binding.etEndAddress.isFocused)
                binding.etEndAddress.setAdapter(ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, it))

        })

        lifecycleScope.launch {
            Log.i("view", "start")
            viewModel.getAddressesByString()
        }
    }


    override fun onStart() {
        super.onStart()
    }
}