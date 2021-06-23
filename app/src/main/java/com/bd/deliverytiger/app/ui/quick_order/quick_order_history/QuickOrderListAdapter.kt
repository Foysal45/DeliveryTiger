package com.bd.deliverytiger.app.ui.quick_order.quick_order_history

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.quick_order.quick_order_history.QuickOrderList
import com.bd.deliverytiger.app.databinding.ItemViewAllQuickOrderBinding

class QuickOrderListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<QuickOrderList> = mutableListOf()
    var onItemClick: ((dataList: QuickOrderList, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewAllQuickOrderBinding = ItemViewAllQuickOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.date.text = "23 June, 2021"
            binding.parcelCount.text = "${model.requestOrderAmount}"
            binding.address.text = "${model.districtsViewModel?.thana}"

        }
    }

    @SuppressLint("SetTextI18n")
    internal inner class ViewModel(val binding: ItemViewAllQuickOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<QuickOrderList>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<QuickOrderList>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}