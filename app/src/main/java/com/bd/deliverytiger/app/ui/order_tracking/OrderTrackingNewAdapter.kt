package com.bd.deliverytiger.app.ui.order_tracking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackData
import com.bd.deliverytiger.app.databinding.ItemViewOrderTrackBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class OrderTrackingNewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderTrackData> = mutableListOf()
    var onItemClick: ((model:OrderTrackData, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewOrderTrackBinding = ItemViewOrderTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            val formattedDate = DigitConverter.toBanglaDate(model.trackingDate,"yyyy-MM-dd'T'HH:mm:ss.SSS", true)
            binding.date.text = formattedDate
            val formatTime = DigitConverter.formatDate(model.trackingDate, "yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm a")
            binding.time.text = DigitConverter.toBanglaDigit(formatTime)

            binding.statusName.text = model.trackingName

            val greenTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.ic_done_green)
            val redTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.ic_done_red)
            val gryTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.bg_circle_gray)

            binding.statusImage.setImageDrawable(greenTic)

            if (position == (dataList.lastIndex - 1)) {
                binding.road.visibility = View.VISIBLE
                binding.road.setImageResource(R.drawable.ic_road_bottom)
                binding.car.visibility = View.GONE
                binding.stepTitle.text = ""
            } else if (position == (dataList.lastIndex - 2)) {
                binding.road.visibility = View.VISIBLE
                binding.road.setImageResource(R.drawable.ic_road_mid)
                binding.car.visibility = View.VISIBLE
                binding.car.setImageResource(R.drawable.ic_car_green)
                binding.stepTitle.text = "শিপমেন্ট আছে"
            } else if (position == (dataList.lastIndex - 3)) {
                binding.road.visibility = View.VISIBLE
                binding.road.setImageResource(R.drawable.ic_road_top)
                binding.car.visibility = View.GONE
                binding.stepTitle.text = ""
            } else {
                binding.road.visibility = View.GONE
                binding.car.visibility = View.GONE
                binding.stepTitle.text = ""
            }

            if (position == dataList.lastIndex) {
                binding.verticalView.visibility = View.GONE
            } else {
                binding.verticalView.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewOrderTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<OrderTrackData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

}