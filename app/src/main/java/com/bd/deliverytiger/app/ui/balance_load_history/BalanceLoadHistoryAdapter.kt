package com.bd.deliverytiger.app.ui.balance_load_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.balance_load.BalanceLoadHistoryData
import com.bd.deliverytiger.app.databinding.ItemViewBalanceHistoryListBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import java.text.SimpleDateFormat
import java.util.*

class BalanceLoadHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<BalanceLoadHistoryData> = mutableListOf()
    var onItemClick: ((dataList: BalanceLoadHistoryData, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewBalanceHistoryListBinding = ItemViewBalanceHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.date.text = DigitConverter.formatDate(model.advanceDate, "MM/dd/yyyy", "yyyy-MM-dd")
            binding.bkashTransactionNo.text = model.transactionId
            binding.paymentMedium.text = "Bkash"
            binding.totalAmount.text = "${model.advanceAmount}"

        }
    }

    internal inner class ViewModel(val binding: ItemViewBalanceHistoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<BalanceLoadHistoryData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<BalanceLoadHistoryData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}