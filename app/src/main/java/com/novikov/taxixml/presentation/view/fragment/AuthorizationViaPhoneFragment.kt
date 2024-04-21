package com.novikov.taxixml.presentation.view.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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

    private lateinit var loadingDialog: AlertDialog

    lateinit var binding: FragmentAuthorizationViaPhoneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        loadingDialog = AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog).apply {
            setView(R.layout.dialog_loading)
        }.create()

        binding = FragmentAuthorizationViaPhoneBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        phone = ""

        phoneCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                loadingDialog.dismiss()
                auth.signInWithCredential(p0).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(requireContext(), "Welcome", Toast.LENGTH_SHORT).show()
//                        NavigationController.navHost.navigate(R.id.mainFragment)
                        NavigationController.navHost.popBackStack(R.id.orderTaxiFragment, true)
                    }
                    else
                        Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG).show()

                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), p0.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                loadingDialog.dismiss()
                UserInfo.phone = "+7" + binding.etPhoneNumber.unMasked
                NavigationController.navHost.navigate(R.id.action_authorizationViaPhoneFragment_to_codeFragment, bundleOf("code_id" to p0))
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnRequestCode.setOnClickListener {
            if(binding.etPhoneNumber.unMasked.length == 10
                && binding.etPhoneNumber.unMasked.all { it.isDigit() }){
                loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loadingDialog.show()
                authUser()
            }
            else{
                if(binding.etPhoneNumber.unMasked.length < 10)
                    binding.etPhoneNumberLayout.error = "Мало цифр"
                if (!binding.etPhoneNumber.unMasked.all { it.isDigit() })
                    binding.etPhoneNumberLayout.error = "Должны быть только цифры"
            }
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