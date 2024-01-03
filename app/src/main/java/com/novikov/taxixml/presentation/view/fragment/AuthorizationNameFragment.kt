package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentAuthorizationNameBinding
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationNameFragment : Fragment() {

    private lateinit var binding: FragmentAuthorizationNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentAuthorizationNameBinding.inflate(inflater)

        binding.etName.setText(UserInfo.name)

        binding.btnContinue.setOnClickListener {
            UserInfo.name = binding.etName.text.toString()
            NavigationController.navHost.navigate(R.id.action_authorizationViaEmailFragment_to_authorizationViaPhoneFragment)
        }

        return binding.root
    }
}