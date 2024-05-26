package com.novikov.taxixml.presentation.view.dialog

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.ktx.Firebase

import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.presentation.viewmodel.TariffDialogViewModel
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import com.novikov.taxixml.utilites.Coefs
import com.novikov.taxixml.utilites.PaymentMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TariffDialog(var distance: Float = 0.0f, var traffic: Float = 0.0f) : BottomSheetDialogFragment() {

    private lateinit var btnEconom: Button
    private lateinit var btnComfort: Button
    private lateinit var btnBusiness: Button
    private lateinit var rbCash: RadioButton
    private lateinit var rbCard: RadioButton
    private lateinit var tvCard: TextView
    private lateinit var btnOrderTaxi: Button

    private val viewModel: TariffDialogViewModel by viewModels()

    private var economPrice = Coefs.MIN_ECONOM
    private var comfortPrice = Coefs.MIN_COMFORT
    private var businessPrice = Coefs.MIN_BUSINESS

    private lateinit var loadingDialog: AlertDialog
    private lateinit var chooseCardDialog: ChooseCardDialog

    private val onCardChooseListener = object : OnCardChooseListener{
        override fun onCardChoose(card: Card) {
            tvCard.text = context!!.getString(R.string.points) + card.number.substring(card.number.length - 4, card.number.length)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.dialog_tariff, container, false)

        loadingDialog = AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog).apply {
            setView(R.layout.dialog_loading)
        }.create()
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnEconom = view.findViewById(R.id.btnEconom)
        btnComfort = view.findViewById(R.id.btnComfort)
        btnBusiness = view.findViewById(R.id.btnBusiness)
        rbCash = view.findViewById(R.id.rbCash)
        rbCard = view.findViewById(R.id.rbCard)
        tvCard = view.findViewById(R.id.tvCard)
        btnOrderTaxi = view.findViewById(R.id.btnOrderTaxi)

        chooseCardDialog = ChooseCardDialog(requireContext(), this, onCardChooseListener)

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

        btnEconom.text = requireContext().getString(R.string.economy) + " " + economPrice
        btnComfort.text = requireContext().getString(R.string.comfort) + " " + comfortPrice
        btnBusiness.text = requireContext().getString(R.string.business) + " " + businessPrice

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

        rbCash.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.mldPaymentMethod.value = PaymentMethod.CASH
                rbCard.isChecked = false
            }
            else
                viewModel.mldPaymentMethod.value = 0
        }

        rbCard.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                rbCash.isChecked = false
                chooseCardDialog.show()
            }
        }

        viewModel.mldTariff.observe(requireActivity(), Observer {

            btnEconom.setBackgroundColor(requireContext().getColor(R.color.primaryColor))
            btnComfort.setBackgroundColor(requireContext().getColor(R.color.primaryColor))
            btnBusiness.setBackgroundColor(requireContext().getColor(R.color.primaryColor))

            when(it){
                "econom" ->{
                    btnEconom.setBackgroundColor(Color.LTGRAY)
                    UserInfo.orderData.price = economPrice
                }
                "comfort" -> {
                    btnComfort.setBackgroundColor(Color.LTGRAY)
                    UserInfo.orderData.price = comfortPrice
                }
                "business" -> {
                    btnBusiness.setBackgroundColor(Color.LTGRAY)
                    UserInfo.orderData.price = businessPrice
                }
            }
        })

        btnOrderTaxi.setOnClickListener {
            UserInfo.orderData.tariff = viewModel.mldTariff.value!!
            UserInfo.orderData.clientUid = FirebaseAuth.getInstance().currentUser!!.uid
            UserInfo.orderData.paymentType =
                if (viewModel.mldPaymentMethod.value!! == PaymentMethod.CASH)
                    "cash"
                else
                    "card"
            UserInfo.orderData.status = "в ожидании"
            lifecycleScope.launch {
                loadingDialog.show()
                viewModel.createOrder()
            }.invokeOnCompletion {
                loadingDialog.dismiss()
                this.dismiss()
                TripAwaitingDialog().show(requireFragmentManager(), "tripAwaitingDialog")
                com.google.firebase.Firebase.database.reference.
                child("orders")
                    .child(UserInfo.orderId)
                    .child("status").addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value.toString() == "в ожидании")
                                Toast.makeText(requireContext(), "Ожидайте водителя", Toast.LENGTH_SHORT).show()
                            else if(snapshot.value.toString() == "принят"){
                                Toast.makeText(requireContext(), "Водитель принял заказ", Toast.LENGTH_SHORT).show()
                                TripDialog().show(requireFragmentManager(), "tripDialog")
                                this@TariffDialog.dismiss()
                            }
                            else if(snapshot.value.toString() == "завершен") {
                                RatingDialog().show(requireFragmentManager(), "ratingDialog")
                                Toast.makeText(requireContext(), "Поездка завершена", Toast.LENGTH_SHORT).show()
                            }
                            Log.i("orderStatusListener", snapshot.value.toString())
                            Toast.makeText(requireContext(), "Заказ принят", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }
        }

        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        chooseCardDialog.dismiss()
        viewModel.mldPaymentMethod.value = 0
    }

}