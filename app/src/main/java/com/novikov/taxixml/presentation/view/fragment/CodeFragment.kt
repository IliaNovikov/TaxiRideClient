package com.novikov.taxixml.presentation.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.CalendarContract
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
import com.novikov.taxixml.databinding.FragmentCodeBinding
import com.novikov.taxixml.presentation.view.activity.MainActivity
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CodeFragment : Fragment() {

    private lateinit var binding: FragmentCodeBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var codeId: String

    private var counter: Int = 60

    private lateinit var phoneCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCodeBinding.inflate(inflater)

        auth = FirebaseAuth.getInstance()
        codeId = arguments?.getString("code_id").toString()

        Toast.makeText(requireContext(), codeId, Toast.LENGTH_LONG).show()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnReRequestCode.isEnabled = false
        binding.btnReRequestCode.setTextColor(Color.GRAY)

        binding.chronometerReRequest.base = SystemClock.elapsedRealtime() + (counter*1000).toLong()

        binding.chronometerReRequest.start()

        binding.chronometerReRequest.setOnChronometerTickListener {
            if(counter == 0){
                binding.btnReRequestCode.isEnabled = true
                binding.btnReRequestCode.setTextColor(resources.getColor(R.color.accentColor))
                counter = 60
                binding.chronometerReRequest.base = SystemClock.elapsedRealtime() + (counter*1000).toLong()
            }

            counter--
        }

        phoneCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                auth.signInWithCredential(p0).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "Welcome", Toast.LENGTH_SHORT).show()
                        NavigationController.navHost.navigate(R.id.orderTaxiFragment)
                    } else
                        Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG)
                            .show()

                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(requireContext(), p0.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignIn.setOnClickListener {
            if(binding.etCode.unMasked.length == 6
                && binding.etCode.unMasked.all { it.isDigit() }){
                val credential = PhoneAuthProvider.getCredential(codeId, binding.etCode.unMasked)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(requireContext(), "Welcome", Toast.LENGTH_SHORT).show()
                        NavigationController.navHost.navigate(R.id.authorizationNameFragment)
                    }
                    else
                        Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            else{
                if(binding.etCode.unMasked.length != 6)
                    binding.etCodeLayout.error = "Должно быть 6 цифр"
                if(!binding.etCode.unMasked.all { it.isDigit() })
                    binding.etCodeLayout.error = "Должны быть только цифры"
            }

        }

        binding.btnReRequestCode.setOnClickListener {
            Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                UserInfo.phone,
                60,
                TimeUnit.SECONDS,
                activity as MainActivity,
                phoneCallback
            )
        }

    }

}