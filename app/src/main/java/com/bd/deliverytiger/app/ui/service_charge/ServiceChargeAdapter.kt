package com.bd.deliverytiger.app.ui.service_charge

import android.annotation.SuppressLint
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

class ServiceChargeAdapter : RecyclerView.Adapter<ServiceChargeAdapter.myViewHolder>() {

    private val dataList: MutableList<CourierOrderAmountDetail> = mutableListOf()
    var onItemClick: ((model: CourierOrderAmountDetail, position: Int) -> Unit)? = null
    var onPaymentClick: ((model: CourierOrderAmountDetail, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return myViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view_service_bill, parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        val model = dataList[position]
        //holder.tvBillingCounter.text = DigitConverter.toBanglaDigit(position + 1)
        holder.tvBillingOrderId.text = model.courierOrdersId.toString()
        val orderType = if (model.collectionAmount > 0.0) {
            "COD"
        } else {
            "Only Delivery"
        }
        holder.orderTypeTV.text = "($orderType)"
        holder.tvBillingShipmentsCharge.text = "${DigitConverter.toBanglaDigit(model.deliveryCharge)} ৳"
        holder.tvBillingCODCharge.text = "${DigitConverter.toBanglaDigit(model.codCharge)} ৳"
        holder.tvBillingFragileItemCharge.text = "${DigitConverter.toBanglaDigit(model.breakableCharge)} ৳"
        holder.tvBillingCollectionCharge.text = "${DigitConverter.toBanglaDigit(model.collectionCharge)} ৳"
        holder.tvBillingReturnCharge.text = "${DigitConverter.toBanglaDigit(model.returnCharge)} ৳"
        holder.tvBillingTotalServiceCharge.text = "${DigitConverter.toBanglaDigit(model.totalAmount)} ৳"
        holder.tvBillingPaymentStatus.text = model.serviceBillingStatus + " (সার্ভিস চার্জ)"
        //holder.paymentStatus.text = courierOrderAmountDetailList?.get(position)?.serviceBillingStatus

        if (model.serviceBillingStatus.equals("Received")) {
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
        internal val orderTypeTV: TextView = itemView.findViewById(R.id.orderType)
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
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
                }
            }
            /*paymentBtn.setOnClickListener {
                onPaymentClick?.invoke(dataList[adapterPosition], adapterPosition)
            }*/
        }
    }

    fun initLoad(list: List<CourierOrderAmountDetail>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<CourierOrderAmountDetail>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

}