package com.bd.deliverytiger.app.ui.order_tracking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.utils.DigitConverter

class OrderTrackingAdapter(
    var context: Context,
    var orderTrackStatusList: ArrayList<OrderTrackMainResponse>
) :
    RecyclerView.Adapter<OrderTrackingAdapter.mTrackViewHolder>() {
    private var formattedDate = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mTrackViewHolder {
        return mTrackViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_track_order,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return orderTrackStatusList.size
    }

    override fun onBindViewHolder(holder: mTrackViewHolder, position: Int) {
        formattedDate = DigitConverter.toBanglaDate(orderTrackStatusList?.get(position)?.dateTime,"yyyy-MM-dd'T'HH:mm:ss")
        holder.orderTrackDate.text =formattedDate

        holder.orderTrackStatus.text = orderTrackStatusList?.get(position)?.orderTrackStatusGroup

        if (position == 0) holder.verticalView.visibility = View.GONE
        else holder.verticalView.visibility = View.VISIBLE
    }

    inner class mTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val orderTrackDate: TextView = itemView.findViewById(R.id.orderTrackDate)
        val orderTrackStatus: TextView = itemView.findViewById(R.id.orderTrackStatus)
        val verticalView: View = itemView.findViewById(R.id.verticalView)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }

    var onItemClick: ((position: Int) -> Unit)? = null
}