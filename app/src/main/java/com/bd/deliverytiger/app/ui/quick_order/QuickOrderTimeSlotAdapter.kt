package com.bd.deliverytiger.app.ui.quick_order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.bd.deliverytiger.app.databinding.ItemViewTimeSlotBinding
import timber.log.Timber

class QuickOrderTimeSlotAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<QuickOrderTimeSlotData> = mutableListOf()
    var onItemClick: ((model:QuickOrderTimeSlotData, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewTimeSlotBinding = ItemViewTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {

            val model = dataList[position]
            val binding = holder.binding

            binding.timeSlot.text = "${model.startTime}"

        }
    }

    inner class ViewHolder(val binding: ItemViewTimeSlotBinding ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<QuickOrderTimeSlotData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

}