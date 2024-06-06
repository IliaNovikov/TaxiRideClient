package com.novikov.taxixml.presentation.view.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.usecase.GetOrderDataUseCase
import com.novikov.taxixml.presentation.viewmodel.TripAwaitingFragmentViewModel
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TripAwaitingDialog() : BottomSheetDialogFragment() {

    private lateinit var btnCancelTrip: Button
    private val viewModel: TripAwaitingFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.dialog_trip_awaiting, container, false)

        btnCancelTrip = view.findViewById(R.id.btnCancelTrip)

        this.isCancelable = false

        btnCancelTrip.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteOrder()
            }.invokeOnCompletion {
                this.dismiss()
            }
        }

        return view

    }

}