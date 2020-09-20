package com.bd.deliverytiger.app.ui.order_tracking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.utils.DigitConverter

class OrderTrackingAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderTrackMainResponse> = mutableListOf()
    var onItemClick: ((model:OrderTrackMainResponse, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_track_order, parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {

            val model = dataList[position]

            val formattedDate = DigitConverter.toBanglaDate(model.dateTime,"yyyy-MM-dd'T'HH:mm:ss")
            holder.orderTrackDate.text =formattedDate

            holder.orderTrackStatus.text = model.orderTrackStatusGroup

            if (position == dataList.lastIndex) {
                holder.verticalView.visibility = View.GONE
            } else {
                holder.verticalView.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val orderTrackDate: TextView = itemView.findViewById(R.id.orderTrackDate)
        val orderTrackStatus: TextView = itemView.findViewById(R.id.orderTrackStatus)
        val verticalView: View = itemView.findViewById(R.id.verticalView)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<OrderTrackMainResponse>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

}