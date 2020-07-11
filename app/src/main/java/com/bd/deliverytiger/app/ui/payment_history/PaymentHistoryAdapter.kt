package com.bd.deliverytiger.app.ui.payment_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_history.PaymentData
import com.bd.deliverytiger.app.databinding.ItemViewPaymentHistoryBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class PaymentHistoryAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<PaymentData> = mutableListOf()
    var onItemClicked: ((model: PaymentData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewPaymentHistoryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_payment_history, parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            holder.binding.transactionNo.text = model.transactionNo
            holder.binding.paymentMedium.text = model.modeOfPayment
            holder.binding.totalAmount.text = "${DigitConverter.toBanglaDigit(model.netPaidAmount)} ৳"
        }
    }

    inner class ViewModel(val binding: ItemViewPaymentHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
    }

    fun initLoad(list: List<PaymentData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<PaymentData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}