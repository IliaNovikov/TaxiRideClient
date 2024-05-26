package com.novikov.taxixml.presentation.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.novikov.taxixml.R
import com.novikov.taxixml.presentation.viewmodel.TripDialogViewModel
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TripDialog : BottomSheetDialogFragment() {

    private lateinit var tvDriverPhone: TextView
    private lateinit var tvDriverName: TextView
    private lateinit var tvDriverRating: TextView
    private lateinit var tvCarBrand: TextView
    private lateinit var tvCarNumber: TextView

    private val viewModel: TripDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.dialog_trip, container, false)

        tvDriverPhone = view.findViewById(R.id.tvDriverPhone)
        tvDriverName = view.findViewById(R.id.tvDriverName)
        tvDriverRating = view.findViewById(R.id.tvDriverRating)
        tvCarBrand = view.findViewById(R.id.tvCarBrand)
        tvCarNumber = view.findViewById(R.id.tvCarNumber)

        lifecycleScope.launch {
            viewModel.getDriverInfo(UserInfo.orderData.driverUid.toString())
        }

        viewModel.driverInfo.observe(requireActivity(), Observer {
            tvDriverPhone.text = it.phone
            tvDriverName.text = it.name
            tvDriverRating.text = it.rating.toString() + "★"
            tvCarBrand.text = it.carBrand
            tvCarNumber.text = it.carNumber
        })

        return view
    }

}