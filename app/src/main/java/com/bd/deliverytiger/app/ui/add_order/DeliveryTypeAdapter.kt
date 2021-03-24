package com.bd.deliverytiger.app.ui.add_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.charge.WeightRangeWiseData
import com.bd.deliverytiger.app.databinding.ItemViewDeliveryTypeBinding
import com.bumptech.glide.Glide

class DeliveryTypeAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<WeightRangeWiseData> = mutableListOf()
    var onItemClick: ((position: Int, model: WeightRangeWiseData) -> Unit)? = null
    private var selectedItem: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewDeliveryTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            if (position == selectedItem) {
                Glide.with(binding.deliveryTypeImage)
                    .load(model.onImageLink)
                    .into(binding.deliveryTypeImage)
            } else {
                Glide.with(binding.deliveryTypeImage)
                    .load(model.offImageLink)
                    .into(binding.deliveryTypeImage)
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewDeliveryTypeBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(position, dataList[position])
                    selectedItem = position
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun initLoad(list: List<WeightRangeWiseData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun selectPreSelection() {
        if (selectedItem != -1) {
            if (selectedItem in 0..dataList.lastIndex) {
                onItemClick?.invoke(selectedItem, dataList[selectedItem])
            }
        }
    }

    fun clearSelection(){
        selectedItem = -1
    }

}