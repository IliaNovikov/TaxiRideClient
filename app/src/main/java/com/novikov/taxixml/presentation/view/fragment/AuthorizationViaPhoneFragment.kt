package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentAuthorizationViaPhoneBinding
import com.novikov.taxixml.presentation.view.activity.MainActivity
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AuthorizationViaPhoneFragment : Fragment() {

    private lateinit var phone: String
    private lateinit var phoneCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var auth: FirebaseAuth

    lateinit var binding: FragmentAuthorizationViaPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAuthorizationViaPhoneBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        phone = ""

        phoneCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                auth.signInWithCredential(p0).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(requireContext(), "Welcome", Toast.LENGTH_SHORT).show()
                        NavigationController.navHost.navigate(R.id.mainFragment)
                    }
                    else
                        Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG).show()

                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(requireContext(), p0.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                UserInfo.phone = "+7" + binding.etPhoneNumber.unMasked
                NavigationController.navHost.navigate(R.id.action_authorizationViaPhoneFragment_to_codeFragment, bundleOf("code_id" to p0))
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnRequestCode.setOnClickListener {
            authUser()
        }

    }

    private fun authUser(){
        phone = "+7" + binding.etPhoneNumber.unMasked
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,
            60,
            TimeUnit.SECONDS,
            activity as MainActivity,
            phoneCallback
        )
    }
}