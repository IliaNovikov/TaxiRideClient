package com.novikov.taxixml.presentation.view.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.OrderData
import com.novikov.taxixml.presentation.viewmodel.RatingDialogViewModel
import com.novikov.taxixml.singleton.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RatingDialog : DialogFragment() {

    private lateinit var rbTripScore: RatingBar

    private val viewModel: RatingDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = layoutInflater.inflate(R.layout.dialog_rating, container, false)

        rbTripScore = view.findViewById(R.id.rbTripScore)
        rbTripScore.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            lifecycleScope.launch {
                try {
                    viewModel.setDriverRating(rating)
                    viewModel.setOrderStatus(UserInfo.orderId, "оценен")
                }
                catch (ex: Exception){
                    Toast.makeText(requireContext(), "Упс... Кажется, произошла ошибка", Toast.LENGTH_LONG).show()
                    Log.e("rating", ex.message.toString())
                }

            }.invokeOnCompletion {
                Toast.makeText(requireContext(), requireContext().getString(R.string.thanks_for_the_feedback), Toast.LENGTH_LONG).show()
                this@RatingDialog.dismiss()
                UserInfo.orderData = OrderData()
            }
        }

        return view
    }

}