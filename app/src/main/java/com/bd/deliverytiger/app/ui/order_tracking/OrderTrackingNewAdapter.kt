package com.bd.deliverytiger.app.ui.order_tracking

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackData
import com.bd.deliverytiger.app.databinding.ItemViewOrderTrackBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class OrderTrackingNewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderTrackData> = mutableListOf()
    var onItemClick: ((model:OrderTrackData, position: Int) -> Unit)? = null
    var onLocationClick: ((model:OrderTrackData, position: Int) -> Unit)? = null
    var onCallPress: ((model:OrderTrackData, position: Int) -> Unit)? = null
    var onActionClicked: ((model:OrderTrackData, position: Int) -> Unit)? = null

    private val sdf1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
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

            binding.statusName.text = model.trackingName

            if (model.trackingColor == "green") {
                val greenTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.ic_done_green)
                binding.statusImage.setImageDrawable(greenTic)
                if (model.subTrackingShipmentName.name?.isNotEmpty() == true) {
                    binding.hubInfo.isVisible = true
                    binding.subStatusName.text = model.subTrackingShipmentName.name
                    if (!model.subTrackingShipmentName.latitude.isNullOrEmpty() && !model.subTrackingShipmentName.longitude.isNullOrEmpty()) {
                        binding.locationTrack.isVisible = true
                    } else {
                        binding.locationTrack.isVisible = false
                    }
                } else {
                    binding.hubInfo.isVisible = false
                }

            } else if (model.trackingColor == "red") {
                val redTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.ic_done_red)
                binding.statusImage.setImageDrawable(redTic)
                if (model.subTrackingReturnName.name?.isNotEmpty() == true) {
                    binding.hubInfo.isVisible = true
                    binding.subStatusName.text = model.subTrackingReturnName.name
                    if (!model.subTrackingReturnName.latitude.isNullOrEmpty() && !model.subTrackingReturnName.longitude.isNullOrEmpty()) {
                        binding.locationTrack.isVisible = true
                    } else {
                        binding.locationTrack.isVisible = false
                    }
                } else {
                    binding.hubInfo.isVisible = false
                }
            } else {
                val gryTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.bg_circle_gray)
                binding.statusImage.setImageDrawable(gryTic)
                binding.hubInfo.isVisible = false
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
                            if (model.trackStateCount == 2) {
                                binding.car.visibility = View.VISIBLE
                                binding.car.setImageResource(R.drawable.ic_car_green)
                                binding.stepTitle.text = "শিপমেন্টে আছে"
                            }
                        }
                        //middle
                        2 -> {
                            binding.road.visibility = View.VISIBLE
                            binding.road.setImageResource(R.drawable.ic_road_mid)
                            binding.car.visibility = View.VISIBLE
                            binding.car.setImageResource(R.drawable.ic_car_green)
                            binding.stepTitle.text = "শিপমেন্টে আছে"
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
                            if (model.trackStateCount == 2) {
                                binding.car.visibility = View.VISIBLE
                                binding.car.setImageResource(R.drawable.ic_car_red)
                                binding.stepTitle.text = "রিটার্নে আছে"
                            }
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

            if (model.statusGroupId == 0) {
                try {
                    val lastDate = DigitConverter.toBanglaDate(model.expectedDeliveryDate,"yyyy-MM-dd'T'HH:mm:ss", true)
                    if (model.expectedFirstDeliveryDate.isNullOrEmpty()) {
                        binding.subStatusName.text = lastDate
                    } else {
                        val firstDate = sdf2.format(sdf1.parse(model.expectedFirstDeliveryDate))
                        binding.subStatusName.text = "${DigitConverter.toBanglaDigit(firstDate)}-$lastDate"
                    }
                    binding.hubInfo.isVisible = true
                    binding.locationTrack.isVisible = false
                    binding.date.text = ""
                    binding.time.text = ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val formattedDate = DigitConverter.toBanglaDate(model.trackingDate,"yyyy-MM-dd'T'HH:mm:ss", true)
                binding.date.text = formattedDate
                val formatTime = DigitConverter.formatDate(model.trackingDate, "yyyy-MM-dd'T'HH:mm:ss", "hh:mm a")
                binding.time.text = DigitConverter.toBanglaDigit(formatTime)
            }

            if (model.courierDeliveryMan?.courierDeliveryManMobile?.isNotEmpty() == true) {
                binding.deliveryManInfo.isVisible = true
                val deliveryManName = "ডেলিভারি করছেন - <b>${model.courierDeliveryMan?.courierDeliveryManName}</b>"
                binding.deliveryManName.text = HtmlCompat.fromHtml(deliveryManName, HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding.mobileNumber.text = model.courierDeliveryMan?.courierDeliveryManMobile
                if (model.courierDeliveryMan?.eDeshMobileNo?.isNotEmpty() == true){
                    binding?.dcMobileNumber?.isVisible = model.courierDeliveryMan?.eDeshMobileNo?.isEmpty() ?: false
                    binding.dcMobileNumber.text = model.courierDeliveryMan?.eDeshMobileNo
                }
                binding.courierComment.text = model.courierDeliveryMan?.courierComment
            } else {
                binding.deliveryManInfo.isVisible = false
            }

            //Timber.d("order tracking status ${model.status}")
            when (model.status) {
                // কাস্টমার প্রোডাক্ট নিতে চায়নি, ক্রেতা ফোনে পাওয়া যায়নি
                26, 33 , 47, 27-> {
                    binding.reattemptStatus.isVisible = false
                    if (position == 0) {
                        binding.actionBtn.isVisible = true
                        binding.actionBtn.text = "আবার ডেলিভারি এটেম্পট নিন"
                    } else {
                        binding.actionBtn.isVisible = false
                    }
                }
                64 -> {
                    binding.reattemptStatus.isVisible = true
                    binding.reattemptStatus.text = "রি-এটেম্পট রিকোয়েস্ট করা হয়েছে"
                }
                else -> {
                    binding.reattemptStatus.isVisible = false
                    binding.actionBtn.isVisible = false
                }
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
            binding.mobileNumber.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onCallPress?.invoke(dataList[adapterPosition], adapterPosition)
                }
            }
            binding.actionBtn.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onActionClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
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