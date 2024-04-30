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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentAddCardBinding
import com.novikov.taxixml.databinding.FragmentSettingsBinding
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.helpers.Validator
import com.novikov.taxixml.presentation.viewmodel.AddCardFragmentViewModel
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddCardFragment : Fragment() {

    private lateinit var binding: FragmentAddCardBinding

    private val viewModel: AddCardFragmentViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddCardBinding.inflate(inflater)

        loadingDialog = AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog).apply {
            setView(R.layout.dialog_loading)
        }.create()
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnGoBack.setOnClickListener {
            NavigationController.navHost.navigate(R.id.settingsFragment)
        }
        binding.btnAddCard.setOnClickListener {
            viewModel.cardNumber.value = binding.etCardNumber.unMasked
            viewModel.cardMonthYear.value = binding.etCardMonthYear.unMasked
            viewModel.cardCVC.value = binding.etCardCVC.unMasked
            viewModel.validate()

            if(viewModel.canAddCard.value == true){
                lifecycleScope.launch {
                    loadingDialog.show()
                    viewModel.addCard()
                }.invokeOnCompletion {
                    loadingDialog.dismiss()
                }
            }


            Log.i("add card fragment", binding.etCardNumber.unMasked)
        }

        binding.etCardNumber.addTextChangedListener {
            if (it.isNullOrEmpty().not()){
                if (it!!.startsWith("2"))
                    binding.tilCardNumber.endIconDrawable = requireContext().getDrawable(R.drawable.mir_logo)
                else if (it.startsWith("4"))
                    binding.tilCardNumber.endIconDrawable = requireContext().getDrawable(R.drawable.visa_logo)
                else if (it.startsWith("5"))
                    binding.tilCardNumber.endIconDrawable = requireContext().getDrawable(R.drawable.mastercard_icon)
                else
                    binding.tilCardNumber.endIconDrawable = null
            }
        }

        viewModel.cardNumberErrorEnabled.observe(requireActivity()) {
            Log.i("observer", it.toString())
            binding.tilCardNumber.error = viewModel.cardNumberError.value
            binding.tilCardNumber.isErrorEnabled = it
        }
        viewModel.cardMonthYearErrorEnabled.observe(requireActivity()) {
            Log.i("observer", it.toString())
            binding.tilCardMonthYear.error = viewModel.cardMonthYearError.value
            binding.tilCardMonthYear.isErrorEnabled = it
        }
        viewModel.cardCVCErrorEnabled.observe(requireActivity()) {
            Log.i("observer", it.toString())
            binding.tilCardCVC.error = viewModel.cardCVCError.value
            binding.tilCardCVC.isErrorEnabled = it
        }

        return binding.root
    }

}