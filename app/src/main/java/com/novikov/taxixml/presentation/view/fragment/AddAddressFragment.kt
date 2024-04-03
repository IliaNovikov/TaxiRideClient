package com.novikov.taxixml.presentation.view.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentAddAddressBinding
import com.novikov.taxixml.presentation.viewmodel.AddAddressFragmentViewModel
import com.novikov.taxixml.singleton.NavigationController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAddressFragment : Fragment() {

    private lateinit var binding: FragmentAddAddressBinding

    private val viewModel: AddAddressFragmentViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddAddressBinding.inflate(inflater)

        loadingDialog = AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog).apply {
            setView(R.layout.dialog_loading)
        }.create()
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnGoBack.setOnClickListener {
            NavigationController.navHost.navigate(R.id.settingsFragment)
        }

        binding.btnAddAddress.setOnClickListener {
            Log.i("city fr", binding.etCity.text.toString())
            viewModel.city.value = binding.etCity.text.toString()
            viewModel.street.value = binding.etStreet.text.toString()
            viewModel.house.value = binding.etHouse.text.toString()
            viewModel.validate()

            if(viewModel.canAddAddress.value == true){
                lifecycleScope.launch {
                    loadingDialog.show()
                    viewModel.addAddress()
                }.invokeOnCompletion {
                    loadingDialog.dismiss()
                }
            }

            viewModel.cityErrorEnabled.observe(requireActivity(), Observer {
                binding.tilCity.error = viewModel.cityError.value
                binding.tilCity.isErrorEnabled = it
            })
            viewModel.streetErrorEnabled.observe(requireActivity(), Observer {
                binding.tilStreet.error = viewModel.streetError.value
                binding.tilStreet.isErrorEnabled = it
            })
            viewModel.houseErrorEnabled.observe(requireActivity(), Observer {
                binding.tilHouse.error = viewModel.houseError.value
                binding.tilHouse.isErrorEnabled = it
            })
        }

        binding

        return binding.root
    }

}