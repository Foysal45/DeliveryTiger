package com.bd.deliverytiger.app.ui.return_statement

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.return_statement.ReturnStatementData
import com.bd.deliverytiger.app.databinding.ItemViewReturnStatementBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class ReturnStatementAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ReturnStatementData> = mutableListOf()
    var onItemClick: ((model: ReturnStatementData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val binding: ItemViewReturnStatementBinding = ItemViewReturnStatementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            model.date?.let {
                val dates = it.split("T")
                if (dates.isNotEmpty()) {
                    holder.binding.date.text = dates.first()
                }
            }

            holder.binding.count.text = "${DigitConverter.toBanglaDigit(model.order, true)} টি"
        }
    }

    inner class ViewModel(val binding: ItemViewReturnStatementBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    onItemClick?.invoke(dataList[adapterPosition])
            }
        }
    }

    fun initLoad(list: List<ReturnStatementData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<ReturnStatementData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

}