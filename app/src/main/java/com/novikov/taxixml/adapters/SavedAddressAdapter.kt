package com.novikov.taxixml.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.Address

class SavedAddressAdapter(val context: Context, val savedAddresses: List<Address>): RecyclerView.Adapter<SavedAddressAdapter.ViewHolder>() {

    class ViewHolder(itemView: View,
                     private val tvAddress: TextView? = itemView.findViewById(R.id.tvAddress)
    ) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.rv_saved_addresses_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return savedAddresses.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = savedAddresses[position]

        holder.itemView.findViewById<TextView>(R.id.tvAddress).text = "${address.city}, ${address.street} ${address.house}"
    }
}