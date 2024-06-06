package com.novikov.taxixml.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.domain.model.Message
import com.novikov.taxixml.singleton.UserInfo

class MessagesAdapter(private val context: Context, private val messages: List<Message>) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage = itemView.findViewById<TextView>(R.id.tvMessage)
        val tvSender = itemView.findViewById<TextView>(R.id.tvSender)
        val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
        val cvbackground = itemView.findViewById<MaterialCardView>(R.id.cvBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_messages_item, parent, false)
        return MessagesAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessagesAdapter.ViewHolder, position: Int) {
        val message = messages[position]

        holder.tvMessage.text = message.text
        holder.tvMessage.setTextColor(if (message.senderUid == UserInfo.uid) context.getColor(R.color.accentColor) else context.getColor(R.color.secondaryColor))
        holder.tvSender.text = if (message.senderUid == UserInfo.uid) context.getString(R.string.you) else context.getString(R.string.driver)
        holder.tvTime.text = message.time
        holder.cvbackground.setCardBackgroundColor(if (message.senderUid == UserInfo.uid) context.getColor(R.color.secondaryColor) else context.getColor(R.color.primaryColor))
    }
}