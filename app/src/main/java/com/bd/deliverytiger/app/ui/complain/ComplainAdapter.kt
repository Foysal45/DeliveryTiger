package com.bd.deliverytiger.app.ui.complain

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.complain.ComplainData
import com.bd.deliverytiger.app.databinding.ItemViewComplainBinding

class ComplainAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ComplainData> = mutableListOf()
    var onItemClicked: ((model: ComplainData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewComplainBinding = ItemViewComplainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.orderCode.text = "DT-${model.orderId}"
            if (model.complaintDate != null) {
                val list = model.complaintDate!!.split("T")
                if (list.isNotEmpty()) {
                    binding.date.text = list.first()
                }
            }
            binding.status.text = "স্টেটাস: ${model.complainType}"
            if (model.solvedDate != null && model.solvedDate != "0001-01-01T00:00:00Z") {
                val list = model.solvedDate!!.split("T")
                if (list.isNotEmpty()) {
                    binding.complain.text = "কমপ্লেইন সল্ভ: ${list.first()}"
                }
            }
        }
    }

    inner class ViewModel(val binding: ItemViewComplainBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
    }

    fun initLoad(list: List<ComplainData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<ComplainData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}