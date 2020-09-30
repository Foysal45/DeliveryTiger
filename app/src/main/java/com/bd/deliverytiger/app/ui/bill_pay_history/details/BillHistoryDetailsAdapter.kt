package com.bd.deliverytiger.app.ui.bill_pay_history.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.bill_pay_history.OrderAmount
import com.bd.deliverytiger.app.databinding.ItemViewBillHistoryDetailsBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class BillHistoryDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderAmount> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewBillHistoryDetailsBinding = ItemViewBillHistoryDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.orderCode.text =  "${model.orderCode}"
            binding.totalAmount.text = "${DigitConverter.toBanglaDigit(model.totalAmount, true)} ৳"
        }
    }

    inner class ViewModel(val binding: ItemViewBillHistoryDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

        }
    }

    fun initLoad(list: List<OrderAmount>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<OrderAmount>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

}