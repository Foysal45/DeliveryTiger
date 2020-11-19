package com.bd.deliverytiger.app.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.databinding.ItemViewReturnTypeBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class ReturnAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataList: MutableList<DashboardData> = mutableListOf()
    var onItemClick: ((position: Int, model: DashboardData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewReturnTypeBinding = ItemViewReturnTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding
            binding.title.text = model.name
            binding.countTV.text = DigitConverter.toBanglaDigit(model.count)
        }
    }

    internal inner class ViewModel(val binding: ItemViewReturnTypeBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(adapterPosition, dataList[adapterPosition])
                }
            }
        }
    }

    fun initData(list: MutableList<DashboardData>) {
        dataList.clear()
        dataList.addAll(list)
    }




}