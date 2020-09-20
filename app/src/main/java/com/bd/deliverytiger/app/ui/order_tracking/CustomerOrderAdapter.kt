package com.bd.deliverytiger.app.ui.order_tracking

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.databinding.ItemViewCustomerOrderBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class CustomerOrderAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<CourierOrderViewModel> = mutableListOf()
    var onItemClick: ((model:CourierOrderViewModel, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewCustomerOrderBinding = ItemViewCustomerOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {

            val model = dataList[position]
            val binding = holder.binding

            binding.orderId.text = model?.courierOrdersId.toString()

            val formattedDate = DigitConverter.toBanglaDate(model?.courierOrderDateDetails?.orderDate, "MM-dd-yyyy HH:mm:ss")
            binding.date.text = formattedDate

            binding.reference.text = model?.courierOrderInfo?.collectionName
            binding.customerName.text = model?.customerName
            var mobile = "${model?.courierAddressContactInfo?.mobile}"
            if (model?.courierAddressContactInfo?.otherMobile?.isEmpty() == false) {
                mobile += ",${model?.courierAddressContactInfo?.otherMobile}"
            }
            binding.customerPhone.text = mobile
            binding.address.text = model?.courierAddressContactInfo?.address
            val district = "${model?.courierAddressContactInfo?.thanaName},${model?.courierAddressContactInfo?.districtName}"
            binding.district.text = district

            binding.serviceCharge.text = "${DigitConverter.toBanglaDigit(model?.courierPrice?.totalServiceCharge)} ৳"
            binding.collectionAmount.text = "${DigitConverter.toBanglaDigit(model?.courierPrice?.collectionAmount)} ৳"
            binding.status.text = model?.status
        }
    }

    inner class ViewHolder(val binding: ItemViewCustomerOrderBinding ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.trackBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<CourierOrderViewModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

}