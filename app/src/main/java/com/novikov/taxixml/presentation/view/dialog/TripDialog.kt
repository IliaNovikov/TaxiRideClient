package com.novikov.taxixml.presentation.view.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.novikov.taxixml.R
import com.novikov.taxixml.presentation.viewmodel.TripDialogViewModel
import com.novikov.taxixml.singleton.NavigationController
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
    private lateinit var btnChatWithDriver: ImageButton

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

        btnChatWithDriver = view.findViewById(R.id.btnChatWithDriver)

        lifecycleScope.launch {
            viewModel.getDriverInfo(UserInfo.orderData.driverUid.toString())
        }

        viewModel.driverInfo.observe(requireActivity(), Observer {
            tvDriverPhone.text = it.phone
            tvDriverName.text = it.name
            tvDriverRating.text = "%.2f".format(it.rating) + "★"
            tvCarBrand.text = it.carBrand
            tvCarNumber.text = it.carNumber

            val phoneSpannable = SpannableString(it.phone)
            phoneSpannable.setSpan(UnderlineSpan(), 0, phoneSpannable.length, 0)
            tvDriverPhone.text = phoneSpannable

        })

        btnChatWithDriver.setOnClickListener {
            requireFragmentManager().beginTransaction().remove(this@TripDialog).commit()
            this@TripDialog.dismiss()
            NavigationController.navHost.navigate(R.id.chatFragment)
        }

        tvDriverPhone.setOnClickListener {
            val phoneNumber = Uri.parse("tel:${tvDriverPhone.text}")
            val intent = Intent(Intent.ACTION_DIAL, phoneNumber)
            startActivity(intent)
        }

        return view
    }

}