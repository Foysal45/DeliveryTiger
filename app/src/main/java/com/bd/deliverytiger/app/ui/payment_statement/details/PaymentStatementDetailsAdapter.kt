package com.bd.deliverytiger.app.ui.payment_statement.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_statement.OrderHistoryData
import com.bd.deliverytiger.app.databinding.ItemViewPaymentHistoryDetailsBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class PaymentStatementDetailsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderHistoryData> = mutableListOf()
    var onItemClicked: ((model: OrderHistoryData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewPaymentHistoryDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_payment_history_details, parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            holder.binding.orderCode.text = model.orderCode
            holder.binding.collectedAmount.text = "${DigitConverter.toBanglaDigit(model.collectedAmount)} ৳"
            holder.binding.totalCharge.text = "${DigitConverter.toBanglaDigit(model.totalCharge)} ৳"
            holder.binding.netAmount.text = "${DigitConverter.toBanglaDigit(model.amount)} ৳"
        }
    }

    inner class ViewModel(val binding: ItemViewPaymentHistoryDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
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

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }
}