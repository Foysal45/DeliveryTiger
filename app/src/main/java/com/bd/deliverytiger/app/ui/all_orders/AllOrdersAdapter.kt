package com.bd.deliverytiger.app.ui.all_orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.button.MaterialButton

class AllOrdersAdapter(
    var context: Context,
    var courierOrderAmountDetailList: ArrayList<CourierOrderViewModel?>?
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
        return courierOrderAmountDetailList!!.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        formattedDate =
            DigitConverter.toBanglaDate(
                courierOrderAmountDetailList?.get(position)?.courierOrderDateDetails!!.orderDate,
                "MM-dd-yyyy HH:mm:ss"
            )

        holder.tvAllOrderCounter.text = DigitConverter.toBanglaDigit(position + 1)
        holder.tvAllOrderOrderId.text =
            courierOrderAmountDetailList?.get(position)?.courierOrdersId.toString()
        holder.tvAllOrderOrderDate.text = formattedDate

        holder.tvAllOrderCustomerInfo.text = getAddress(courierOrderAmountDetailList?.get(position)!!)

        holder.tvAllOrderPackageInfo.text = courierOrderAmountDetailList?.get(position)?.courierOrderInfo?.collectionName
        //holder.tvBillingFragileItemCharge.text ="৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.breakableCharge)

        holder.tvAllOrderCollectionAmount.text = "৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.courierPrice?.collectionAmount)

        holder.tvAllOrderTotalServiceCharge.text = "৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.courierPrice?.totalServiceCharge)

        //holder.tvBillingTotalServiceCharge.text ="৳ " + DigitConverter.toBanglaDigit(courierOrderAmountDetailList?.get(position)?.to)

        holder.tvAllOrderStatus.text = courierOrderAmountDetailList?.get(position)?.status

        /*if (courierOrderAmountDetailList?.get(position)?.serviceBillingStatus.equals("Received")) {
            holder.billingItemMainLay.setBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            holder.billingItemMainLay.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }*/

        if (courierOrderAmountDetailList?.get(position)?.buttonFlag == true){

            holder.changeInfoBtn.isEnabled = true
            holder.changeInfoBtn.icon = ContextCompat.getDrawable(holder.changeInfoBtn.context, R.drawable.all_order_edit)
            //holder.changeInfoBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_color)))
            holder.changeInfoBtn.text = "এডিট"

        } else {
            holder.changeInfoBtn.isEnabled = false
            holder.changeInfoBtn.text = "প্রযোজ্য নয়"
            holder.changeInfoBtn.icon = null
            //holder.changeInfoBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_500)))
        }
        //holder.changeInfoBtn.isEnabled = courierOrderAmountDetailList?.get(position)?.buttonFlag ?: false

    }


    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addOrderItemMainLay: LinearLayout = itemView.findViewById(R.id.addOrderItemMainLay)
        val tvAllOrderCounter: TextView = itemView.findViewById(R.id.tvAllOrderCounter)
        val tvAllOrderOrderId: TextView = itemView.findViewById(R.id.tvAllOrderOrderId)
        val tvAllOrderOrderDate: TextView = itemView.findViewById(R.id.tvAllOrderOrderDate)
        val tvAllOrderCustomerInfo: TextView =
            itemView.findViewById(R.id.tvAllOrderCustomerInfo)
        val tvAllOrderPackageInfo: TextView = itemView.findViewById(R.id.tvAllOrderPackageInfo)
        val tvAllOrderCollectionAmount: TextView =
            itemView.findViewById(R.id.tvAllOrderCollectionAmount)
        val tvAllOrderTotalServiceCharge: TextView =
            itemView.findViewById(R.id.tvAllOrderTotalServiceCharge)
        val tvAllOrderStatus: TextView = itemView.findViewById(R.id.tvAllOrderStatus)
        val allOrderDetailsLay: LinearLayout = itemView.findViewById(R.id.allOrderDetailsLay)
        val changeInfoBtn: MaterialButton = itemView.findViewById(R.id.changeInfoBtn)
        //val layAllOrderChangeInfo: LinearLayout = itemView.findViewById(R.id.layAllOrderChangeInfo)

        init {
            allOrderDetailsLay.setOnClickListener {
                onOrderItemClick?.invoke(adapterPosition)
            }

            changeInfoBtn.setOnClickListener {
                onEditItemClick?.invoke(adapterPosition)
            }
        }
    }

    var onOrderItemClick: ((position: Int) -> Unit)? = null
    var onEditItemClick: ((position: Int) -> Unit)? = null

    private fun getAddress(courierOrderViewModel: CourierOrderViewModel): String {
        var mAddress: String = courierOrderViewModel?.customerName + "\n" +
                courierOrderViewModel?.courierAddressContactInfo?.mobile + "," +
                courierOrderViewModel?.courierAddressContactInfo?.otherMobile + "\n\n" +
                courierOrderViewModel.courierAddressContactInfo?.address + "," +
                courierOrderViewModel.courierAddressContactInfo?.areaPostalCode + "," +
                courierOrderViewModel.courierAddressContactInfo?.thanaName + "," +
                courierOrderViewModel.courierAddressContactInfo?.districtName

        return mAddress
    }
}