package com.novikov.taxixml.adapters.interfaces

import com.novikov.taxixml.domain.model.Card

interface OnCardClickListener {
    fun onClick(card: Card, position: Int)
}