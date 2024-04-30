package com.novikov.taxixml.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.Card

class SavedCardAdapter(private val context: Context, private val cards: List<Card>) : RecyclerView.Adapter<SavedCardAdapter.ViewHolder>() {
    class ViewHolder(itemView: View, private val tvCardNumber: TextView? = itemView.findViewById(R.id.tvCardNumber))
        : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_saved_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]

        holder.itemView.findViewById<TextView>(R.id.tvCardNumber).text = card.number.substring(card.number.length - 4, card.number.length)
        if (card.number.startsWith("2"))
            holder.itemView.findViewById<ImageView>(R.id.ivCardLogo).setImageDrawable(context.getDrawable(R.drawable.mir_logo))
        else if (card.number.startsWith("4"))
            holder.itemView.findViewById<ImageView>(R.id.ivCardLogo).setImageDrawable(context.getDrawable(R.drawable.visa_logo))
        else if (card.number.startsWith("5"))
            holder.itemView.findViewById<ImageView>(R.id.ivCardLogo).setImageDrawable(context.getDrawable(R.drawable.mastercard_logo))
    }
}