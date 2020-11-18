package com.bd.deliverytiger.app.ui.bill_pay

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthData
import com.bd.deliverytiger.app.databinding.ItemViewServiceBillPayMonthBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class ServiceBillMonthAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<MonthData> = mutableListOf()
    var onItemClick: ((model: MonthData)-> Unit)? = null
    var onPaymentClick: ((model: MonthData)-> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewServiceBillPayMonthBinding = ItemViewServiceBillPayMonthBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.monthName.text =  "${model.monthName},${model.yearOrder} (${DigitConverter.toBanglaDigit(model.orderList.size)}টি)"
            binding.totalChangeMonthly.text = "${DigitConverter.toBanglaDigit(model.totalAmount, true)} ৳"

        }
    }

    inner class ViewModel(val binding: ItemViewServiceBillPayMonthBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[adapterPosition])
                }
            }
            binding.payBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onPaymentClick?.invoke(dataList[adapterPosition])
                }
            }
        }
    }

    fun initLoad(list: List<MonthData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<MonthData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

}