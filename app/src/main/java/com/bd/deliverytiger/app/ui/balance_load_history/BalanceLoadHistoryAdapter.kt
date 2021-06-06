package com.bd.deliverytiger.app.ui.balance_load_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.balance_load.BalanceLimitResponse
import com.bd.deliverytiger.app.databinding.ItemViewBalanceHistoryListBinding

class BalanceLoadHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<BalanceLimitResponse> = mutableListOf()
    var onItemClick: ((dataList: BalanceLimitResponse, position: Int) -> Unit)? = null

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

            binding.date.text ="6 May 2021"
            binding.bkashTransactionNo.text = "ID9856325"
            binding.paymentMedium.text = "Bkash"
            binding.totalAmount.text = model.minAmount.toString()

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

    fun initLoad(list: List<BalanceLimitResponse>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<BalanceLimitResponse>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}