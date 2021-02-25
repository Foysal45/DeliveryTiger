package com.bd.deliverytiger.app.ui.payment_statement.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_statement.OrderHistoryData
import com.bd.deliverytiger.app.databinding.ItemViewPaymentHistoryDetailsBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class PaymentStatementDetailsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderHistoryData> = mutableListOf()
    var onItemClicked: ((model: OrderHistoryData, tabFlag: Int) -> Unit)? = null
    var tabFlag: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewPaymentHistoryDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_payment_history_details, parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.orderCode.text = model.orderCode
            binding.totalCharge.text = "- ${DigitConverter.toBanglaDigit(model.totalCharge)} ৳"

            when (tabFlag) {
                0 -> {
                    binding.key2.visibility = View.VISIBLE
                    binding.collectedAmount.visibility = View.VISIBLE
                    binding.key3.visibility = View.VISIBLE
                    binding.totalCharge.visibility = View.VISIBLE
                    binding.key4.visibility = View.VISIBLE
                    binding.netAmount.visibility = View.VISIBLE

                    binding.collectedAmount.text = "${DigitConverter.toBanglaDigit(model.collectedAmount, true)} ৳"
                    binding.netAmount.text = "${DigitConverter.toBanglaDigit(model.amount, true)} ৳"
                }
                1 -> {
                    binding.key2.visibility = View.GONE
                    binding.collectedAmount.visibility = View.GONE
                    binding.key3.visibility = View.VISIBLE
                    binding.totalCharge.visibility = View.VISIBLE
                    binding.key4.visibility = View.GONE
                    binding.netAmount.visibility = View.GONE
                }
                2 -> {
                    binding.key2.visibility = View.GONE
                    binding.collectedAmount.visibility = View.GONE
                    binding.key3.visibility = View.GONE
                    binding.totalCharge.visibility = View.GONE
                    binding.key4.visibility = View.VISIBLE
                    binding.netAmount.visibility = View.VISIBLE
                    binding.netAmount.text = "${DigitConverter.toBanglaDigit(model.amount, true)} ৳"
                }
            }

        }
    }

    inner class ViewModel(val binding: ItemViewPaymentHistoryDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked?.invoke(dataList[adapterPosition], tabFlag)
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