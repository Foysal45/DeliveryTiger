package com.bd.deliverytiger.app.ui.live.live_schedule_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.live_schedule_list.MyLiveSchedule
import com.bd.deliverytiger.app.databinding.ItemViewLiveListV2Binding
import com.bd.deliverytiger.app.enums.LiveType
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class LiveScheduleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<MyLiveSchedule> = mutableListOf()
    private val filterDataList: MutableList<MyLiveSchedule> = mutableListOf()
    private val options = RequestOptions().placeholder(R.drawable.ic_live_placeholder_1).error(R.drawable.ic_live_placeholder_1)
    var onClick: ((model: MyLiveSchedule, position: Int) -> Unit)? = null
    var onShareClicked: ((model: MyLiveSchedule, position: Int) -> Unit)? = null
    var onProductAddClicked: ((model: MyLiveSchedule, position: Int) -> Unit)? = null
    var onLiveStartClicked: ((model: MyLiveSchedule, position: Int) -> Unit)? = null
    var onProductDetailsClicked: ((model: MyLiveSchedule) -> Unit)? = null

    // For getting time from the function
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private var remainingDuration: Long = 0
    private var liveDuration: Long = 0
    private var remainingDays: Long = 0
    private var remainingHours: Long = 0
    private var remainingMinutes: Long = 0
    private var liveStartDateTime: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewLiveListV2Binding = ItemViewLiveListV2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = filterDataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = filterDataList[position]
            val binding = holder.binding

            binding.liveStatus.text = model.liveStatus?.capitalize()


            when (model.liveStatus) {

                "upcoming" -> {
                    binding.shareLayout.visibility = View.VISIBLE
                    binding.productAddLayout.visibility = View.VISIBLE
                    binding.remainingTimeLayout.visibility = View.VISIBLE

                    binding.liveTitle.visibility = View.GONE
                    binding.productCountLayout.visibility = View.GONE
                    binding.liveStartLayout.visibility = View.GONE

                    remainingDuration(model, binding)
                    upcomingState(model, binding)
                }
                "live" -> {
                    binding.shareLayout.visibility = View.VISIBLE
                    binding.productAddLayout.visibility = View.VISIBLE
                    binding.liveStartLayout.visibility = View.VISIBLE

                    binding.liveTitle.visibility = View.GONE
                    binding.productCountLayout.visibility = View.GONE
                    binding.remainingTimeLayout.visibility = View.GONE

                    remainingDuration(model, binding)
                    liveState(model, binding)
                }
                "replay" -> {

                    binding.productCountLayout.visibility = View.GONE

                    binding.liveTitle.visibility = View.VISIBLE
                    binding.shareLayout.visibility = View.GONE
                    binding.productAddLayout.visibility = View.VISIBLE
                    binding.liveStartLayout.visibility = View.GONE
                    binding.remainingTimeLayout.visibility = View.GONE

                    replayState(model, binding)
                }
            }

            Glide.with(binding.videoCover)
                .load(model.coverPhoto)
                .apply(options)
                .into(binding.videoCover)


        }
    }

    inner class ViewHolder(val binding: ItemViewLiveListV2Binding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
            }

            binding.shareLayout.setOnClickListener {
                onShareClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
            }

            binding.productAddLayout.setOnClickListener {
                onProductAddClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
            }

            binding.liveStartLayout.setOnClickListener {
                onLiveStartClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
            }

            binding.productCountLayout.setOnClickListener {
                onProductDetailsClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    fun initLoad(list: List<MyLiveSchedule>) {
        dataList.clear()
        dataList.addAll(list)
        filterDataList.clear()
        filterDataList.addAll(dataList)
        //notifyDataSetChanged()
    }

    fun lazyLoad(list: List<MyLiveSchedule>) {
        //val start = filterDataList.size
        dataList.addAll(list)
        //filterDataList.addAll(list)
        //notifyItemRangeInserted(start, list.size)
    }

    fun lazyLoadWithFilter(list: List<MyLiveSchedule>, liveType: LiveType) {
        val start = filterDataList.size
        dataList.addAll(list)
        when (liveType) {
            LiveType.ALL -> {
                filterDataList.addAll(list)
                notifyItemRangeInserted(start, list.size)
            }
            LiveType.LIVE -> {
                val filter = list.filter { it.liveStatus == "live" }
                filterDataList.addAll(filter)
                notifyItemRangeInserted(start, filter.size)
            }
            LiveType.UPCOMING -> {
                val filter = list.filter { it.liveStatus == "upcoming" }
                filterDataList.addAll(filter)
                notifyItemRangeInserted(start, filter.size)
            }
            LiveType.REPLAY -> {
                val filter = list.filter { it.liveStatus == "replay" }
                filterDataList.addAll(filter)
                notifyItemRangeInserted(start, filter.size)
            }
        }
    }

    fun getList(): List<MyLiveSchedule> = filterDataList

    fun filter(liveType: LiveType): Boolean {
        when (liveType) {
            LiveType.ALL -> {
                filterDataList.clear()
                filterDataList.addAll(dataList)
                notifyDataSetChanged()
            }
            LiveType.LIVE -> {
                filterDataList.clear()
                filterDataList.addAll(dataList.filter { it.liveStatus == "live" })
                notifyDataSetChanged()
            }
            LiveType.UPCOMING -> {
                filterDataList.clear()
                filterDataList.addAll(dataList.filter { it.liveStatus == "upcoming" })
                notifyDataSetChanged()
            }
            LiveType.REPLAY -> {
                filterDataList.clear()
                filterDataList.addAll(dataList.filter { it.liveStatus == "replay" })
                notifyDataSetChanged()
            }
        }
        return filterDataList.isEmpty()
    }

    private fun remainingDuration(model: MyLiveSchedule, binding: ItemViewLiveListV2Binding) {
        try {
            val liveDate = model.liveDate?.split("T")?.first()
            val liveStartTime = model.fromTime
            val liveEndTime = model.toTime
            liveStartDateTime = "$liveDate $liveStartTime" // 2020-12-04 10:00:00

            // here, if the remaining live time of a User is less than 15, button will disable, this is hardcoded
            val minGoLiveTime = 15
            liveDuration = SimpleDateFormat("HH:mm").parse(liveEndTime).time - SimpleDateFormat("HH:mm").parse(liveStartTime).time
            liveDuration = TimeUnit.MILLISECONDS.toMinutes(liveDuration) - minGoLiveTime

            val liveAirDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(liveStartDateTime)
            //val test = SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-03-01 23:50:00")
            val today = Date()

            // Calculating Here
            remainingDuration = liveAirDate?.time!! - today.time
            remainingDays = TimeUnit.MILLISECONDS.toDays(remainingDuration)
            remainingHours = TimeUnit.MILLISECONDS.toHours(remainingDuration)
            remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingDuration)
            Timber.d("Remaining time: $remainingDuration, $remainingDays, $remainingHours, $remainingMinutes")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun upcomingState(model: MyLiveSchedule, binding: ItemViewLiveListV2Binding) {

        if (remainingDays > 0) {
            binding.remainingTime.text = DigitConverter.toBanglaDigit((remainingDays).toString())
            //binding.tvRemainingTime.text = "দিন বাকি"
            binding.tvRemainingTime.text = HtmlCompat.fromHtml("<b>দিন বাকি</b>", HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else if (remainingHours in 1..24) {
            binding.remainingTime.text = DigitConverter.toBanglaDigit(remainingHours.toString())
            //binding.tvRemainingTime.text = "ঘন্টা বাকি"
            binding.tvRemainingTime.text = HtmlCompat.fromHtml("<b><font color='#008fd3'>ঘন্টা বাকি</font></b>", HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            if (remainingMinutes > 0) {
                binding.remainingTime.text = DigitConverter.toBanglaDigit(remainingMinutes.toString())
                //binding.tvRemainingTime.text = "মিনিট বাকি"
                binding.tvRemainingTime.text = HtmlCompat.fromHtml("<b><font color='#1B5E20'>মিনিট বাকি</font></b>", HtmlCompat.FROM_HTML_MODE_COMPACT)
            } else if (remainingMinutes <= 0 && remainingMinutes > -liveDuration) {
                binding.remainingTimeLayout.visibility = View.GONE
                binding.liveStartLayout.visibility = View.VISIBLE

                binding.tvLiveStart.text = HtmlCompat.fromHtml("<b><font color='#e74c3c'>লাইভ করুন</font></b>", HtmlCompat.FROM_HTML_MODE_COMPACT)
            } else {
                binding.remainingTimeLayout.visibility = View.GONE
                binding.liveStartLayout.visibility = View.GONE
            }
        }


        val date = sdf.parse(liveStartDateTime)
        if (date != null) {
            binding.liveSchedule.text = DigitConverter.relativeWeekday(date)
            Timber.d("dateTesting $date")
        }

    }

    private fun liveState(model: MyLiveSchedule, binding: ItemViewLiveListV2Binding) {

        Timber.d("remainingMinutes $remainingMinutes")

        binding.liveStartLayout.isEnabled = false
        binding.tvLiveStart.text = HtmlCompat.fromHtml("<b><font color='#e74c3c'>লাইভ চলছে</font></b>", HtmlCompat.FROM_HTML_MODE_COMPACT)

        val date = sdf.parse(liveStartDateTime)
        if (date != null) {
            binding.liveSchedule.text = DigitConverter.relativeWeekday(date)
            Timber.d("dateTesting $date")
        }
    }

    private fun replayState(model: MyLiveSchedule, binding: ItemViewLiveListV2Binding) {

        binding.liveTitle.text = model.liveTitle

        binding.productCount.text = DigitConverter.toBanglaDigit(model.totalOrderCount.toString())
        binding.tvProductCount.text = HtmlCompat.fromHtml("<b><font color='#008fd3'>টি অর্ডার</font></b>", HtmlCompat.FROM_HTML_MODE_COMPACT)

        val liveDate = model.liveDate?.split("T")?.first()
        var date = DigitConverter.toBanglaDate(liveDate, "yyyy-MM-dd")
        date = DigitConverter.formatDate(date, "yyyy-MM-dd", "dd-MM-yyyy")
        if (date != null) {
            binding.liveSchedule.text = date
            Timber.d("dateTesting $date")
        }
    }

}