package com.bd.deliverytiger.app.ui.live.live_schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.DateData
import com.bd.deliverytiger.app.databinding.ItemViewScheduleDateBinding

class ScheduleDateAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<DateData> = mutableListOf()
    var onItemClicked: ((model: DateData) -> Unit)? = null
    var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewScheduleDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.monthName.text = model.monthName
            binding.date.text = "${model.date}"
            binding.dayName.text = model.dateName

            if (selectedPosition == position) {
                binding.parent.background = ContextCompat.getDrawable(binding.parent.context, R.drawable.bg_seba_schedule)
                binding.monthName.setTextColor(ContextCompat.getColor(binding.parent.context, R.color.black_70))
                binding.date.setTextColor(ContextCompat.getColor(binding.parent.context, R.color.black_90))
                binding.dayName.setTextColor(ContextCompat.getColor(binding.parent.context, R.color.black_70))
            } else {
                binding.parent.background = ContextCompat.getDrawable(binding.parent.context, R.drawable.bg_live_schedule_date_unselected)
                binding.monthName.setTextColor(ContextCompat.getColor(binding.parent.context, R.color.black_70))
                binding.date.setTextColor(ContextCompat.getColor(binding.parent.context, R.color.black_90))
                binding.dayName.setTextColor(ContextCompat.getColor(binding.parent.context, R.color.black_70))
            }

        }
    }

    inner class ViewModel(val binding: ItemViewScheduleDateBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked?.invoke(dataList[absoluteAdapterPosition])
                    selectedPosition = absoluteAdapterPosition
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun initLoad(list: List<DateData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<DateData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}