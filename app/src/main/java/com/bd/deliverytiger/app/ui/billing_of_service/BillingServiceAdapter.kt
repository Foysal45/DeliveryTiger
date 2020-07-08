package com.bd.deliverytiger.app.ui.billing_of_service

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.billing_service.CourierOrderAmountDetail
import com.bd.deliverytiger.app.utils.DigitConverter

class BillingServiceAdapter(var context: Context, var courierOrderAmountDetailList: ArrayList<CourierOrderAmountDetail?>?) : RecyclerView.Adapter<BillingServiceAdapter.myViewHolder>() {

    var onItemClick: ((position: Int) -> Unit)? = null
    var onPaymentClick: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return myViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_view_service_bill,//R.layout.item_view_billing_collection,
                parent, false)
        )
    }

    override fun getItemCount(): Int {
        return courierOrderAmountDetailList!!.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {


        //holder.tvBillingCounter.text = DigitConverter.toBanglaDigit(position + 1)
        holder.tvBillingOrderId.text = courierOrderAmountDetailList?.get(position)?.courierOrdersId.toString()

        holder.tvBillingShipmentsCharge.text = "${DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.deliveryCharge)} ৳"
        holder.tvBillingCODCharge.text = "${DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.codCharge)} ৳"
        holder.tvBillingFragileItemCharge.text = "${DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.breakableCharge)} ৳"
        holder.tvBillingCollectionCharge.text = "${DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.collectionCharge)} ৳"
        holder.tvBillingReturnCharge.text = "${DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.returnCharge)} ৳"
        holder.tvBillingTotalServiceCharge.text = "${DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.totalAmount)} ৳"
        holder.tvBillingPaymentStatus.text = courierOrderAmountDetailList?.get(position)?.serviceBillingStatus
        //holder.paymentStatus.text = courierOrderAmountDetailList?.get(position)?.serviceBillingStatus

        if (courierOrderAmountDetailList?.get(position)?.serviceBillingStatus.equals("Received")) {
            //holder.billingItemMainLay.setBackgroundColor(Color.parseColor("#E8F5E9"))
            holder.tvBillingPaymentStatus.setTextColor(ContextCompat.getColor(holder.tvBillingPaymentStatus.context, R.color.colorPrimary))
            //holder.paymentBtn.visibility = View.GONE
        } else {
            //holder.billingItemMainLay.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.tvBillingPaymentStatus.setTextColor(ContextCompat.getColor(holder.tvBillingPaymentStatus.context, R.color.black_90))
            //holder.paymentBtn.visibility = View.VISIBLE
        }
        //holder.viewBillingCollectionDivider.setBackgroundColor(Color.parseColor("#1A000000"))
    }


    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //val billingItemMainLay: LinearLayout = itemView.findViewById(R.id.billingItemMainLay)
        //val tvBillingCounter: TextView = itemView.findViewById(R.id.tvBillingCounter)
        internal val tvBillingOrderId: TextView = itemView.findViewById(R.id.tvBillingOrderId)
        //val tvBillingOrderDate: TextView = itemView.findViewById(R.id.tvBillingOrderDate)
        internal val tvBillingShipmentsCharge: TextView = itemView.findViewById(R.id.tvBillingShipmentsCharge)
        internal val tvBillingCODCharge: TextView = itemView.findViewById(R.id.tvBillingCODCharge)
        internal val tvBillingFragileItemCharge: TextView = itemView.findViewById(R.id.tvBillingFragileItemCharge)
        internal val tvBillingCollectionCharge: TextView = itemView.findViewById(R.id.tvBillingCollectionCharge)
        internal val tvBillingReturnCharge: TextView = itemView.findViewById(R.id.tvBillingReturnCharge)
        internal val tvBillingTotalServiceCharge: TextView = itemView.findViewById(R.id.tvBillingTotalServiceCharge)
        internal val tvBillingPaymentStatus: TextView = itemView.findViewById(R.id.tvBillingPaymentStatus)
        //val billingDetailsLay: LinearLayout = itemView.findViewById(R.id.billingDetailsLay)
        //val paymentStatus: TextView = itemView.findViewById(R.id.tvAllOrderStatus)


        internal val track: ImageView = itemView.findViewById(R.id.track)
        //internal val paymentBtn: TextView = itemView.findViewById(R.id.payment)


        init {
            track.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
            /*paymentBtn.setOnClickListener {
                onPaymentClick?.invoke(adapterPosition)
            }*/
        }
    }

}