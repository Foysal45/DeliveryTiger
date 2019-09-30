package com.bd.deliverytiger.app.ui.order_tracking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order_track.Statu
import com.bd.deliverytiger.app.ui.district.ThanaOrAriaAdapter
import com.bd.deliverytiger.app.utils.DigitConverter

class OrderTrackingSubAdapter(var context: Context,var statusList: List<Statu?>?): RecyclerView.Adapter<OrderTrackingSubAdapter.mViewHolder>() {

   private var formattedDate = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderTrackingSubAdapter.mViewHolder {
        return mViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_track_bottom,parent,false))
    }

    override fun getItemCount(): Int {
       return statusList!!.size
    }

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {
        holder.tvOrderSubSubStatus.text = statusList?.get(position)?.name
        formattedDate = DigitConverter.toBanglaDate(statusList?.get(position)?.dateTime,"yyyy-MM-dd'T'HH:mm:ss")
        holder.tvOrderSubSubDate.text =formattedDate
    }

    inner class mViewHolder(itemV: View): RecyclerView.ViewHolder(itemV){
        val tvOrderSubSubStatus: TextView = itemV.findViewById(R.id.tvOrderSubSubStatus)
        val tvOrderSubSubDate: TextView = itemV.findViewById(R.id.tvOrderSubSubDate)
    }
}