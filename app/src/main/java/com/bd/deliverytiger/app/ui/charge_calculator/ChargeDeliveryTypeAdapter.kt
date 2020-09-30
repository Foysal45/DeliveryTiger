package com.bd.deliverytiger.app.ui.charge_calculator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.calculator.DeliveryType
import com.bd.deliverytiger.app.databinding.ItemViewChargeDeliveryTypeBinding
import com.bumptech.glide.Glide

class ChargeDeliveryTypeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<DeliveryType> = mutableListOf()
    var onItemClick: ((position: Int, model: DeliveryType) -> Unit)? = null
    private var selectedItem: Int = -1
    //private val requestOption = RequestOptions().placeholder(R.drawable.ic_delivery_regular_unselected)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewChargeDeliveryTypeBinding = ItemViewChargeDeliveryTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            val imageUrl = if (position == selectedItem) {
                model.onImage
            } else {
                model.offImage
            }
            Glide.with(binding.typeImage.context)
                .load(imageUrl)
                .into(binding.typeImage)
        }
    }

    internal inner class ViewHolder(val binding: ItemViewChargeDeliveryTypeBinding) : RecyclerView.ViewHolder(binding.root) {

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

    fun selectItemPosition(position: Int) {
        selectedItem = position
        notifyDataSetChanged()
    }

    fun clearSelectedItemPosition() {
        selectedItem = -1
    }

    fun initLoad(list: List<DeliveryType>) {
        dataList.clear()
        dataList.addAll(list)
        selectedItem = 0
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<DeliveryType>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}