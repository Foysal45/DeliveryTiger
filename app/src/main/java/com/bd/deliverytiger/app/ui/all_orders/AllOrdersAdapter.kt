package com.bd.deliverytiger.app.ui.all_orders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.billing_service.CourierOrderAmountDetail
import com.bd.deliverytiger.app.utils.DigitConverter

class AllOrdersAdapter(
    var context: Context,
    var courierOrderAmountDetailList: ArrayList<CourierOrderAmountDetail?>?
) :
    RecyclerView.Adapter<AllOrdersAdapter.myViewHolder>() {
    private var formattedDate = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_view_all_orders,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return 15
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

    /*    *//*formattedDate =
            DigitConverter.toBanglaDate(courierOrderAmountDetailList?.get(position)?.)*//*

        holder.tvBillingCounter.text = DigitConverter.toBanglaDigit(position + 1)
        holder.tvBillingOrderId.text =
            courierOrderAmountDetailList?.get(position)?.courierOrdersId.toString()
        //holder.tvBillingOrderDate.text = formattedDate

        *//*   holder.tvBillingShipmentsCharge.text =
               "৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.)*//*

        holder.tvBillingCODCharge.text =
            "৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.codCharge)
        //holder.tvBillingFragileItemCharge.text ="৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.breakableCharge)

        holder.tvBillingCollectionCharge.text =
            "৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.collectionCharge)

        holder.tvBillingReturnCharge.text =
            "৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.returnCharge)

        //holder.tvBillingTotalServiceCharge.text ="৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.to)

        holder.tvBillingPaymentStatus.text =
            courierOrderAmountDetailList?.get(position)?.serviceBillingStatus

        if (courierOrderAmountDetailList?.get(position)?.serviceBillingStatus.equals("Received")) {
            holder.billingItemMainLay.setBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            holder.billingItemMainLay.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }*/

     //   holder.viewAllOrderDivider.setBackgroundColor(Color.parseColor("#1A000000"))
    }


    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val billingItemMainLay: LinearLayout = itemView.findViewById(R.id.billingItemMainLay)
        val tvBillingCounter: TextView = itemView.findViewById(R.id.tvBillingCounter)
        val tvBillingOrderId: TextView = itemView.findViewById(R.id.tvBillingOrderId)
        val tvBillingOrderDate: TextView = itemView.findViewById(R.id.tvBillingOrderDate)
        val tvBillingShipmentsCharge: TextView =
            itemView.findViewById(R.id.tvBillingShipmentsCharge)
        val tvBillingCODCharge: TextView = itemView.findViewById(R.id.tvBillingCODCharge)
        val tvBillingFragileItemCharge: TextView =
            itemView.findViewById(R.id.tvBillingFragileItemCharge)
        val tvBillingCollectionCharge: TextView =
            itemView.findViewById(R.id.tvBillingCollectionCharge)
        val tvBillingReturnCharge: TextView = itemView.findViewById(R.id.tvBillingReturnCharge)
        val tvBillingTotalServiceCharge: TextView =
            itemView.findViewById(R.id.tvBillingTotalServiceCharge)
        val billingDetailsLay: LinearLayout = itemView.findViewById(R.id.billingDetailsLay)
       // val viewAllOrderDivider: View = itemView.findViewById(R.id.viewAllOrderDivider)

        init {
            billingDetailsLay.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }

    var onItemClick: ((position: Int) -> Unit)? = null
}