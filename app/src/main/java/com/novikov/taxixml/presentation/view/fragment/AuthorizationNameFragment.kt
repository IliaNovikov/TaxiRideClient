package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentAuthorizationNameBinding
import com.novikov.taxixml.presentation.viewmodel.NameFragmentViewModel
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthorizationNameFragment : Fragment() {

    private lateinit var binding: FragmentAuthorizationNameBinding

    private val viewModel: NameFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentAuthorizationNameBinding.inflate(inflater)

        binding.etName.setText(UserInfo.name)

        binding.btnContinue.setOnClickListener {
            if(binding.etName.text.toString().all { it.isLetter() } && binding.etName.text.toString().isNotEmpty()){
                try{
                    UserInfo.name = binding.etName.text.toString()
                    UserInfo.uid = FirebaseAuth.getInstance().currentUser!!.uid
                    lifecycleScope.launch {
                        viewModel.setUserData()
                    }.invokeOnCompletion {
                        NavigationController.navHost.navigate(R.id.mainFragment)
                    }
                }
                catch (e: Exception){
                    Log.e("name view", e.message.toString())
                }
            }
            else{
                if (!binding.etName.text.toString().all { it.isLetter() })
                    binding.etNameLayout.error = "Допускаются только буквы"
                if(binding.etName.text.toString().isEmpty())
                    binding.etNameLayout.error = "Пустое поле"
            }

        }

        return binding.root
    }
}