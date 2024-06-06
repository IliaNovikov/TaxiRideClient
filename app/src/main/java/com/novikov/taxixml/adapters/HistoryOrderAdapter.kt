package com.novikov.taxixml.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.HistoryOrder

class HistoryOrderAdapter(private val context: Context, private val orders: List<HistoryOrder>) : RecyclerView.Adapter<HistoryOrderAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val tvStartAddress = itemView.findViewById<TextView>(R.id.tvStartAddress)
        val tvEndAddress = itemView.findViewById<TextView>(R.id.tvEndAddress)
        val tvDriverName = itemView.findViewById<TextView>(R.id.tvDriverName)
        val tvCarBrand = itemView.findViewById<TextView>(R.id.tvCarBrand)
        val tvCarNumber = itemView.findViewById<TextView>(R.id.tvCarNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_order_history_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        holder.tvDate.text = order.date
        holder.tvStartAddress.text = order.startAddress
        holder.tvEndAddress.text = order.endAddress
        holder.tvDriverName.text = order.driverName
        holder.tvCarBrand.text = order.carBrand
        holder.tvCarNumber.text = order.carNumber

    }
}