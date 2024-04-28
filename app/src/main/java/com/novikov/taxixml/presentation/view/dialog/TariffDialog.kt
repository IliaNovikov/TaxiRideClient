package com.novikov.taxixml.presentation.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.novikov.taxixml.R
import com.novikov.taxixml.utilites.Coefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TariffDialog(var distance: Float = 0.0f) : BottomSheetDialogFragment() {

    private lateinit var btnEconom: Button
    private lateinit var btnComfort: Button
    private lateinit var btnBusiness: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.dialog_tariff, container, false)

        btnEconom = view.findViewById(R.id.btnEconom)
        btnComfort = view.findViewById(R.id.btnComfort)
        btnBusiness = view.findViewById(R.id.btnBusiness)

        btnEconom.text = "Econom \n" + (distance / 1000f * Coefs.ECONOM).toString()
        btnComfort.text = "Comfort \n" + (distance / 1000f * Coefs.COMFORT).toString()
        btnBusiness.text = "Business \n" + (distance / 1000f * Coefs.BUSINESS).toString()

        return view
    }

}