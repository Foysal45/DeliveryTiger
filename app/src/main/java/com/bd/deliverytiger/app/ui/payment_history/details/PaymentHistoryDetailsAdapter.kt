package com.bd.deliverytiger.app.ui.payment_history.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_history.OrderHistoryData
import com.bd.deliverytiger.app.databinding.ItemViewPaymentHistoryDetailsBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class PaymentHistoryDetailsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderHistoryData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewPaymentHistoryDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_payment_history_details, parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            holder.binding.bookingCode.text = DigitConverter.toBanglaDigit(model.bookingCode)
            holder.binding.quantity.text = "${DigitConverter.toBanglaDigit(model.quantity)} টি"
            holder.binding.amount.text = "${DigitConverter.toBanglaDigit(model.paidAmount)} ৳"
        }
    }

    inner class ViewModel(val binding: ItemViewPaymentHistoryDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    fun initLoad(list: List<OrderHistoryData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<OrderHistoryData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}