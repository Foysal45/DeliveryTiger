package com.bd.deliverytiger.app.ui.live.live_schedule

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleData
import com.bd.deliverytiger.app.databinding.ItemViewScheduleTimeBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleTimeAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ScheduleData> = mutableListOf()
    var onItemClicked: ((model: ScheduleData) -> Unit)? = null
    var selectedPosition: Int = -1

    private val sdf24 = SimpleDateFormat("HH:mm:ss", Locale.US)
    private val sdf12 = SimpleDateFormat("hh:mm a", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewScheduleTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            try {
                val startTime = sdf12.format(sdf24.parse(model.fromScheduleTime))
                val endTime = sdf12.format(sdf24.parse(model.toScheduleTime))
                val range = "$startTime - $endTime"
                binding.scheduleTime.text = range
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (model.isTimeActive == 0) {
                if (selectedPosition == position) {
                    binding.scheduleTimeFlag.text = "FREE"
                    binding.scheduleTimeFlag.setTextColor(Color.parseColor("#008FD5"))
                    //binding.parent.background = ContextCompat.getDrawable(binding.parent.context, R.drawable.bg_seba_schedule)
                    binding.scheduleTime.setBackgroundResource(R.drawable.bg_item_selected_border)
                    binding.scheduleTime.setTextColor(Color.parseColor("#ffffff"))
                } else {
                    binding.scheduleTimeFlag.text = "FREE"
                    binding.scheduleTimeFlag.setTextColor(Color.parseColor("#008FD5"))
                    binding.parent.background = null
                    //binding.parent.background = ContextCompat.getDrawable(binding.parent.context, R.drawable.bg_seba_schedule_unselect)
                    binding.scheduleTime.setBackgroundResource(R.drawable.bg_item_unselected_border)
                    binding.scheduleTime.setTextColor(Color.parseColor("#6E6F72"))
                }

            } else {
                binding.scheduleTimeFlag.text = "BOOKED"
                binding.scheduleTimeFlag.setTextColor(Color.parseColor("#F15A2D"))
                //binding.parent.background = ContextCompat.getDrawable(binding.parent.context, R.drawable.bg_seba_schedule_unavailable)
                binding.scheduleTime.setBackgroundResource(R.drawable.bg_item_unselected_border)
                binding.scheduleTime.setTextColor(Color.parseColor("#6E6F72"))
            }




        }
    }

    inner class ViewModel(val binding: ItemViewScheduleTimeBinding) : RecyclerView.ViewHolder(binding.root) {

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

    fun initLoad(list: List<ScheduleData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<ScheduleData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun modelByIndex(position: Int): ScheduleData {
        return dataList[position]
    }
}