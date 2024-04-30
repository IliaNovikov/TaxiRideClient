package com.novikov.taxixml.presentation.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.ViewParent
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.novikov.taxixml.R
import com.novikov.taxixml.adapters.SavedCardAdapter
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo

class ChooseCardDialog(private val context: Context, private val parent: BottomSheetDialogFragment) : AlertDialog(context){

    private lateinit var rvCards: RecyclerView
    private lateinit var btnAddCard: Button

    override fun show() {
        super.show()
        this.setContentView(R.layout.dialog_choose_card)

        rvCards = this.findViewById(R.id.rvCards)
        btnAddCard = this.findViewById(R.id.btnAddCard)

        rvCards.adapter = SavedCardAdapter(context, UserInfo.cards)

        btnAddCard.setOnClickListener {
            NavigationController.navHost.navigate(R.id.addCardFragment)
            parent.dismiss()
            this.dismiss()
        }
    }

}