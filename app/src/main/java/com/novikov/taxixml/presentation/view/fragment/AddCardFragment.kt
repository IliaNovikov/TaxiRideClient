package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentAddCardBinding
import com.novikov.taxixml.databinding.FragmentSettingsBinding
import com.novikov.taxixml.singleton.NavigationController

class AddCardFragment : Fragment() {

    private lateinit var binding: FragmentAddCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddCardBinding.inflate(inflater)

        binding.btnGoBack.setOnClickListener {
            NavigationController.navHost.navigate(R.id.settingsFragment)
        }



        return binding.root
    }

}