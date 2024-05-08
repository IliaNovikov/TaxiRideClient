package com.novikov.taxixml.presentation.view.dialog

import com.novikov.taxixml.domain.model.Card

interface OnCardChooseListener {

    fun onCardChoose(card: Card)

}