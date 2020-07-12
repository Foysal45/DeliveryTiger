package com.bd.deliverytiger.app.ui.all_orders

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.databinding.ItemViewAllOrderBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class AllOrdersAdapter(var context: Context, var dataList: MutableList<CourierOrderViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onOrderItemClick: ((position: Int) -> Unit)? = null
    var onEditItemClick: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewAllOrderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_all_order, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            val model = dataList[position]

            holder.binding.orderId.text = model?.courierOrdersId.toString()

            val formattedDate = DigitConverter.toBanglaDate(model?.courierOrderDateDetails?.orderDate, "MM-dd-yyyy HH:mm:ss")
            holder.binding.date.text = formattedDate

            holder.binding.reference.text = model?.courierOrderInfo?.collectionName
            holder.binding.customerName.text = model?.customerName
            var mobile = "${model?.courierAddressContactInfo?.mobile}"
            if (model?.courierAddressContactInfo?.otherMobile?.isEmpty() == false) {
                mobile += ",${model?.courierAddressContactInfo?.otherMobile}"
            }
            holder.binding.customerPhone.text = mobile
            holder.binding.address.text = model?.courierAddressContactInfo?.address
            val district = "${model?.courierAddressContactInfo?.thanaName},${model?.courierAddressContactInfo?.districtName}"
            holder.binding.district.text = district

            holder.binding.serviceCharge.text = "${DigitConverter.toBanglaDigit(model?.courierPrice?.totalServiceCharge)} ৳"
            holder.binding.collectionAmount.text = "${DigitConverter.toBanglaDigit(model?.courierPrice?.collectionAmount)} ৳"
            holder.binding.status.text = model?.status

            if (model?.buttonFlag == true) {
                holder.binding.editBtn.visibility = View.VISIBLE
            } else {
                holder.binding.editBtn.visibility = View.GONE
            }
        }

    }

    inner class ViewHolder(val binding: ItemViewAllOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.trackBtn.setOnClickListener {
                onOrderItemClick?.invoke(adapterPosition)
            }
            binding.editBtn.setOnClickListener {
                onEditItemClick?.invoke(adapterPosition)
            }
        }
    }

}