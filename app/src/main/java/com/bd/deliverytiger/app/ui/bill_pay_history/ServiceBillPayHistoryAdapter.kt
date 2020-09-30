package com.bd.deliverytiger.app.ui.bill_pay_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.bill_pay_history.BillPayHistoryResponse
import com.bd.deliverytiger.app.databinding.ItemViewBillPayHistoryBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import java.text.SimpleDateFormat
import java.util.*

class ServiceBillPayHistoryAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<BillPayHistoryResponse> = mutableListOf()
    var onItemClicked: ((model: BillPayHistoryResponse) -> Unit)? = null

    private val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    private val sdf1 = SimpleDateFormat("dd-MMM-yyyy", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewBillPayHistoryBinding = ItemViewBillPayHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            var transactionDate = ""
            if (!model.paymentDate.isNullOrEmpty()) {
                try {
                    sdf.parse(model.paymentDate!!)?.let {
                        transactionDate = sdf1.format(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.date.text = transactionDate
            binding.referenceNo.text = model.referanceText
            binding.orderCount.text = "${DigitConverter.toBanglaDigit(model.orderList.size)} টি"
            binding.totalAmount.text = "${DigitConverter.toBanglaDigit(model.netPaidAmount)} ৳"
        }
    }

    inner class ViewModel(val binding: ItemViewBillPayHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
    }

    fun initLoad(list: List<BillPayHistoryResponse>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<BillPayHistoryResponse>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}