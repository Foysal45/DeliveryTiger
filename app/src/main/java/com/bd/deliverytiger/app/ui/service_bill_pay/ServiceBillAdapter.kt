package com.bd.deliverytiger.app.ui.service_bill_pay

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.service_bill_pay.OrderData
import com.bd.deliverytiger.app.databinding.ItemViewServiceBillPayBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class ServiceBillAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderData> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewServiceBillPayBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_service_bill_pay, parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            var paidStatus = ""
            if (model.isCashCollected == 0) {
                paidStatus = "আনপেইড"
                holder.binding.orderCode.setTextColor(ContextCompat.getColor(holder.binding.orderCode.context, R.color.black_90))
                holder.binding.totalChange.setTextColor(ContextCompat.getColor(holder.binding.totalChange.context, R.color.black_90))
            } else {
                paidStatus = "পেইড"
                holder.binding.orderCode.setTextColor(ContextCompat.getColor(holder.binding.orderCode.context, R.color.colorPrimary))
                holder.binding.totalChange.setTextColor(ContextCompat.getColor(holder.binding.totalChange.context, R.color.colorPrimary))
            }
            holder.binding.orderCode.text =  "${model.orderCode} ($paidStatus)"
            holder.binding.totalChange.text = "${DigitConverter.toBanglaDigit(model.totalAmount)} ৳"
        }
    }

    inner class ViewModel(val binding: ItemViewServiceBillPayBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

        }
    }

    fun initLoad(list: List<OrderData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<OrderData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

}