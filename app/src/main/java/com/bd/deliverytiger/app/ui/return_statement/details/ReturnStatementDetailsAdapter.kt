package com.bd.deliverytiger.app.ui.return_statement.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.return_statement.ReturnOrder
import com.bd.deliverytiger.app.databinding.ItemViewDashboardReturnBinding

class ReturnStatementDetailsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ReturnOrder> = mutableListOf()
    var onItemClicked: ((model: ReturnOrder) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewDashboardReturnBinding = ItemViewDashboardReturnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.orderId.text = model.courierOrdersId
            binding.reference.text = model.collectionName
            binding.customerName.text = model.customerName
            binding.customerPhone.text = model.mobile
            binding.status.text = model.comment

        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewModel(val binding: ItemViewDashboardReturnBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
            }
        }
    }

    fun initLoad(list: List<ReturnOrder>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<ReturnOrder>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

}