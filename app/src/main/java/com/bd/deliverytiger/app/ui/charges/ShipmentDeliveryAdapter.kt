package com.bd.deliverytiger.app.ui.charges

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.charge.DeliveryDayChargeModel
import com.bd.deliverytiger.app.utils.DigitConverter

class ShipmentDeliveryAdapter(private val mContext: Context?, private var dataList: List<DeliveryDayChargeModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_shipment_delivery,
                parent,
                false
            )
        )
    }

    /**
     * getItemCount
     */
    override fun getItemCount(): Int {
        return dataList.size
    }

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {

            val model = dataList[position]
            holder.deliveryTV.text = "${model.days} দিনে"
            holder.priceTV.text = "৳ ${DigitConverter.toBanglaDigit(model.chargeAmount, true)}"
        }
    }



    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val deliveryTV: TextView = view.findViewById(R.id.item_view_shipment_delivery_time)
        internal val priceTV: TextView = view.findViewById(R.id.item_view_shipment_delivery_price)


        init {
            view.setOnClickListener {

            }
        }
    }
}