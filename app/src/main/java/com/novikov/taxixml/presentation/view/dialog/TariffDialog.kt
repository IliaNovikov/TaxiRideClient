package com.novikov.taxixml.presentation.view.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.novikov.taxixml.R
import com.novikov.taxixml.presentation.viewmodel.TariffDialogViewModel
import com.novikov.taxixml.utilites.Coefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TariffDialog(var distance: Float = 0.0f, var traffic: Float = 0.0f) : BottomSheetDialogFragment() {

    private lateinit var btnEconom: Button
    private lateinit var btnComfort: Button
    private lateinit var btnBusiness: Button

    private val viewModel: TariffDialogViewModel by viewModels()

    private var economPrice = Coefs.MIN_ECONOM
    private var comfortPrice = Coefs.MIN_COMFORT
    private var businessPrice = Coefs.MIN_BUSINESS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.dialog_tariff, container, false)

        btnEconom = view.findViewById(R.id.btnEconom)
        btnComfort = view.findViewById(R.id.btnComfort)
        btnBusiness = view.findViewById(R.id.btnBusiness)

        economPrice =
            if ((distance / 1000f * Coefs.ECONOM + Coefs.ECONOM * traffic).toInt() >= Coefs.MIN_ECONOM)
                (distance / 1000f * Coefs.ECONOM + Coefs.ECONOM * traffic).toInt()
            else
                Coefs.MIN_ECONOM

        comfortPrice =
            if ((distance / 1000f * Coefs.COMFORT + Coefs.COMFORT * traffic).toInt() >= Coefs.MIN_COMFORT)
                (distance / 1000f * Coefs.COMFORT + Coefs.COMFORT * traffic).toInt()
            else
                Coefs.MIN_COMFORT

        businessPrice =
            if ((distance / 1000f * Coefs.BUSINESS + Coefs.BUSINESS * traffic).toInt() >= Coefs.MIN_BUSINESS)
                (distance / 1000f * Coefs.BUSINESS + Coefs.BUSINESS * traffic).toInt()
            else
                Coefs.MIN_BUSINESS

        btnEconom.text = "Econom $economPrice"
        btnComfort.text = "Comfort $comfortPrice"
        btnBusiness.text = "Business $businessPrice"

        btnEconom.setOnClickListener {
//            it.setBackgroundColor(requireContext().getColor(R.color.secondaryColor))
//            btnEconom.setTextColor(requireContext().getColor(R.color.primaryColor))
            viewModel.mldTariff.value = "econom"
        }
        btnComfort.setOnClickListener {
            viewModel.mldTariff.value = "comfort"
        }
        btnBusiness.setOnClickListener {
            viewModel.mldTariff.value = "business"
        }

        viewModel.mldTariff.observe(requireActivity(), Observer {

            btnEconom.setBackgroundColor(requireContext().getColor(R.color.primaryColor))
            btnComfort.setBackgroundColor(requireContext().getColor(R.color.primaryColor))
            btnBusiness.setBackgroundColor(requireContext().getColor(R.color.primaryColor))

            when(it){
                "econom" -> btnEconom.setBackgroundColor(Color.LTGRAY)
                "comfort" -> btnComfort.setBackgroundColor(Color.LTGRAY)
                "business" -> btnBusiness.setBackgroundColor(Color.LTGRAY)
            }
        })

        return view
    }

}