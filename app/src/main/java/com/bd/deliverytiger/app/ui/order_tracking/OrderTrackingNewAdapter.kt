package com.bd.deliverytiger.app.ui.order_tracking

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackData
import com.bd.deliverytiger.app.databinding.ItemViewOrderTrackBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import java.text.SimpleDateFormat
import java.util.*

class OrderTrackingNewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderTrackData> = mutableListOf()
    var onItemClick: ((model:OrderTrackData, position: Int) -> Unit)? = null
    var onLocationClick: ((model:OrderTrackData, position: Int) -> Unit)? = null

    private val sdf1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
    private val sdf2 = SimpleDateFormat("dd", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewOrderTrackBinding = ItemViewOrderTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            if (model.statusGroupId == 0) {
                try {
                    val firstDate = sdf2.format(sdf1.parse(model.expectedFirstDeliveryDate))
                    val lastDate = DigitConverter.toBanglaDate(model.expectedDeliveryDate,"yyyy-MM-dd'T'HH:mm:ss.SSS", true)
                    binding.date.text = "${DigitConverter.toBanglaDigit(firstDate)}-$lastDate"
                    binding.time.text = ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val formattedDate = DigitConverter.toBanglaDate(model.trackingDate,"yyyy-MM-dd'T'HH:mm:ss.SSS", true)
                binding.date.text = formattedDate
                val formatTime = DigitConverter.formatDate(model.trackingDate, "yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm a")
                binding.time.text = DigitConverter.toBanglaDigit(formatTime)
            }

            binding.statusName.text = model.trackingName

            if (model.trackingColor == "green") {
                val greenTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.ic_done_green)
                binding.statusImage.setImageDrawable(greenTic)
                binding.subStatusName.text = model.subTrackingShipmentName.name
                if (!model.subTrackingShipmentName.latitude.isNullOrEmpty() && !model.subTrackingShipmentName.longitude.isNullOrEmpty()) {
                    binding.locationTrack.visibility = View.VISIBLE
                }

            } else if (model.trackingColor == "red") {
                val redTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.ic_done_red)
                binding.statusImage.setImageDrawable(redTic)
                binding.subStatusName.text = model.subTrackingReturnName.name
                if (!model.subTrackingReturnName.latitude.isNullOrEmpty() && !model.subTrackingReturnName.longitude.isNullOrEmpty()) {
                    binding.locationTrack.visibility = View.VISIBLE
                }

            } else {
                val gryTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.bg_circle_gray)
                binding.statusImage.setImageDrawable(gryTic)
                binding.subStatusName.text = ""
                binding.locationTrack.visibility = View.GONE
            }

            if (model.trackingFlag) {
                if (model.trackingColor == "green") {
                    when (model.trackState) {
                        //top
                        1 -> {
                            binding.road.visibility = View.VISIBLE
                            binding.road.setImageResource(R.drawable.ic_road_top)
                            binding.car.visibility = View.GONE
                            binding.stepTitle.text = ""
                        }
                        //middle
                        2 -> {
                            binding.road.visibility = View.VISIBLE
                            binding.road.setImageResource(R.drawable.ic_road_mid)
                            binding.car.visibility = View.VISIBLE
                            binding.car.setImageResource(R.drawable.ic_car_green)
                            binding.stepTitle.text = "শিপমেন্ট আছে"
                        }
                        //bottom
                        3 -> {
                            binding.road.visibility = View.VISIBLE
                            binding.road.setImageResource(R.drawable.ic_road_bottom)
                            binding.car.visibility = View.GONE
                            binding.stepTitle.text = ""
                        }
                    }
                } else if (model.trackingColor == "red") {
                    when (model.trackState) {
                        //top
                        1 -> {
                            binding.road.visibility = View.VISIBLE
                            binding.road.setImageResource(R.drawable.ic_road_top)
                            binding.car.visibility = View.GONE
                            binding.stepTitle.text = ""
                        }
                        //middle
                        2 -> {
                            binding.road.visibility = View.VISIBLE
                            binding.road.setImageResource(R.drawable.ic_road_mid)
                            binding.car.visibility = View.VISIBLE
                            binding.car.setImageResource(R.drawable.ic_car_red)
                            binding.stepTitle.text = "রিটার্নে আছে"
                        }
                        //bottom
                        3 -> {
                            binding.road.visibility = View.VISIBLE
                            binding.road.setImageResource(R.drawable.ic_road_bottom)
                            binding.car.visibility = View.GONE
                            binding.stepTitle.text = ""
                        }
                    }
                }
            } else {
                binding.road.visibility = View.GONE
                binding.car.visibility = View.GONE
                binding.stepTitle.text = ""
            }

            if (position == 0) {
                binding.verticalView.visibility = View.GONE
            } else {
                binding.verticalView.visibility = View.VISIBLE
            }
            if (position == dataList.lastIndex) {
                binding.verticalView1.visibility = View.GONE
            } else {
                binding.verticalView1.visibility = View.VISIBLE
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
            binding.locationTrack.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onLocationClick?.invoke(dataList[adapterPosition], adapterPosition)
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