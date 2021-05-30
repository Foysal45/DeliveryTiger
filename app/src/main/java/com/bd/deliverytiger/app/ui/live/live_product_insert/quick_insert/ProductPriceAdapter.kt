package com.bd.deliverytiger.app.ui.live.live_product_insert.quick_insert

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ItemViewDealSizeBinding


class ProductPriceAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<String> = mutableListOf()
    var onItemClick: ((model: String, position: Int) -> Unit)? = null
    var onAddItemClick: ((model: String, position: Int) -> Unit)? = null
    var selectedIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewDealSizeBinding = ItemViewDealSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {

            val model = dataList[position]
            val binding = holder.binding

            binding.title.text = model

            if (selectedIndex == position) {
                binding.title.setBackgroundResource(R.drawable.bg_deal_size_rectangle_fill)
                binding.title.setTextColor(ContextCompat.getColor(binding.title.context, R.color.white))
            } else {
                binding.title.setBackgroundResource(R.drawable.bg_deal_size_rectangle)
                binding.title.setTextColor(ContextCompat.getColor(binding.title.context, R.color.black_80))
            }

            if (position == dataList.lastIndex) {
                binding.title.setBackgroundResource(R.drawable.bg_deal_price_add)
                binding.title.setTextColor(ContextCompat.getColor(binding.title.context, R.color.white))
            }

        }
    }

    inner class ViewModel(val binding: ItemViewDealSizeBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.title.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    if (absoluteAdapterPosition == dataList.lastIndex) {
                        onAddItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                    } else {
                        onItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                        selectedIndex = absoluteAdapterPosition
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun initList(list: List<String>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun insert(item: String) {
        dataList.removeAt(dataList.lastIndex)
        dataList.add(item)
        dataList.sortWith { p0, p1 -> (p0?.toIntOrNull() ?: 0) - (p1?.toIntOrNull() ?: 0) }
        dataList.add("✚")
        //selectedIndex = dataList.indexOf(item)
        notifyDataSetChanged()
        //Timber.d("selectedIndex $selectedIndex")
    }

    fun clearSelection() {
        selectedIndex = -1
        notifyDataSetChanged()
    }
}