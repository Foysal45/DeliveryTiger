package com.bd.deliverytiger.app.ui.add_order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.bd.deliverytiger.app.databinding.ItemViewTimeSlotBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class AddOrderTimeSlotAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<QuickOrderTimeSlotData> = mutableListOf()
    var onItemClick: ((model:QuickOrderTimeSlotData, position: Int) -> Unit)? = null

    var selectedPosition: Int = -1

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

            val timeMsg = "${model.slotName}\n${DigitConverter.formatTimeRange(model.startTime, model.endTime)}"
            binding.timeSlot.text = timeMsg

            if (selectedPosition == position) {
                binding.timeSlot.setBackgroundResource(R.drawable.bg_time_slot_selected)
            }else{
                binding.timeSlot.setBackgroundResource(R.drawable.bg_time_slot_unselected)
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewTimeSlotBinding ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    selectedPosition = absoluteAdapterPosition
                    onItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun setSelectedPositions(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
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