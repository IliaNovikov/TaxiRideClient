package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentSettingsBinding
import com.novikov.taxixml.presentation.viewmodel.SettingsFragmentViewModel
import com.novikov.taxixml.singleton.NavigationController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSettingsBinding.inflate(inflater)

        lifecycleScope.launch {
            viewModel.getUserData()
        }.invokeOnCompletion {
            binding.etName.setText(viewModel.name.value.toString())
            binding.etPhoneNumber.setText(viewModel.phone.value.toString())
        }

        binding.addNewCardBlock.setOnClickListener {
            NavigationController.navHost.navigate(R.id.action_settingsFragment_to_addCardFragment)
        }

        return binding.root
    }

}