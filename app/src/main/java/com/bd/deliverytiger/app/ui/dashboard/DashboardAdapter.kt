package com.bd.deliverytiger.app.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.databinding.ItemViewDashboardPaymentBinding
import com.bd.deliverytiger.app.databinding.ItemViewDashboardUnpaidCodBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import java.text.SimpleDateFormat
import java.util.*


class DashboardAdapter(private val mContext: Context?, private var dataList: MutableList<DashboardData>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClick: ((position: Int, model: DashboardData) -> Unit)? = null
    var onPayDetailsClick: ((position: Int, model: DashboardData) -> Unit)? = null
    var onCODCollectionClick: ((position: Int, model: DashboardData) -> Unit)? = null
    var onPaymentRequestClick: ((position: Int, model: DashboardData) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return dataList[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view_dashboard, parent, false))
        } else {
            val binding: ItemViewDashboardUnpaidCodBinding = ItemViewDashboardUnpaidCodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewModel2(binding)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {

            val model = dataList[position]

            var countMsg = "0"
            var titleMsg = ""
            var countText = ""
            when (model.dashboardCountSumView) {
                "countsum" -> {
                    countMsg = DigitConverter.toBanglaDigit(model.count)
                    titleMsg = "<font color='#CC000000'><b>৳${DigitConverter.toBanglaDigit(model.totalAmount.toInt(), true)}</b></font> ${model.name}"
                    if (model.statusGroupId == 6) {
                        countText = "${model.name}"
                        titleMsg = "(৳ ${DigitConverter.toBanglaDigit(model.totalAmount.toInt(), true)} কালেক্ট হয়েছে)"
                    }
                }
                "count" -> {
                    countMsg = DigitConverter.toBanglaDigit(model.count)
                    titleMsg = "${model.name}"
                }
                "sum" -> {
                    countMsg = "৳ ${DigitConverter.toBanglaDigit(model.totalAmount.toInt(), true)}"
                    titleMsg = "${model.name}"
                }
            }
            holder.countTV.text = countMsg
            holder.titleTV.text = HtmlCompat.fromHtml(titleMsg, HtmlCompat.FROM_HTML_MODE_LEGACY)
            holder.countText.text = countText


            var cardColor = 0
            var countTextColor = 0
            //var cardDesign = 0
            when(model.dashboardViewColorType) {
                "positive" -> {
                    cardColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_positive)
                    countTextColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_positive_text)
                    //cardDesign = R.drawable.ic_dashboard_design_3
                }
                "neutral" -> {
                    cardColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_neutral)
                    countTextColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_neutral_text)
                    //cardDesign = R.drawable.ic_dashboard_design_1
                }
                "negative" -> {
                    cardColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_negative)
                    countTextColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_negative_text)
                    //cardDesign = R.drawable.ic_dashboard_design_4
                }
                "waiting" -> {
                    cardColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_waiting)
                    countTextColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_waiting_text)
                    //cardDesign = R.drawable.ic_dashboard_design_2
                }
                else -> { //neutral
                    cardColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_neutral)
                    countTextColor = ContextCompat.getColor(holder.parentLayout.context, R.color.dashboard_neutral_text)
                    //cardDesign = R.drawable.ic_dashboard_design_1
                }
            }
            holder.parentLayout.setBackgroundColor(cardColor)
            holder.countTV.setTextColor(countTextColor)
            //holder.designIV.setImageResource(cardDesign)
            /*Glide.with(holder.iconIV.context)
                .load(model.dashboardImageUrl)
                .into(holder.iconIV)*/

        } else if (holder is ViewModel1) {

            val model = dataList[position]
            val binding = holder.binding
            val amount = model.totalAmount.toInt()
            binding.amount.text = "৳ ${DigitConverter.toBanglaDigit(amount, true)}"
            binding.msg1.text = "${model.name}"
            if (amount > 0) {
                binding.parent.visibility = View.VISIBLE
                binding.msg2.visibility = View.VISIBLE
                if (model.paymentDate.isNotEmpty()) {
                    val banglaDate = DigitConverter.toBanglaDate(model.paymentDate,"MM/dd/yyyy")
                    binding.msg2.text = "($banglaDate)"
                }
            } else {
                binding.parent.visibility = View.GONE
                binding.msg2.visibility = View.GONE
            }
        } else if (holder is ViewModel2) {
            val model = dataList[position]
            val binding = holder.binding
            val amount = model.totalAmount.toInt()
            binding.countTV.text = "৳ ${DigitConverter.toBanglaDigit(amount, true)}"

            if (model.availability && amount > 0) {
                binding.actionLayout.visibility = View.VISIBLE
                binding.paymentMessageLayout.isVisible = false
            } else {
                binding.actionLayout.visibility = View.GONE
                if (model.currentRequestDate.isNotEmpty() && model.currentPaymentStatus == 0) {
                    val requestTime = DigitConverter.formatDate(model.currentRequestDate,"dd-MM-yyyy HH:mm:ss", "hh:mm a',' dd MMM")
                        .replace("AM", "am")
                        .replace("PM", "pm") // (2.28 pm, 28 Feb)
                    binding.paymentAmount.text = "৳ ${DigitConverter.toBanglaDigit(model.currentPaymentAmount, true)}"
                    binding.paymentTime.text = "($requestTime)"
                    binding.paymentMessage.text = "${DigitConverter.toBanglaDigit(model.paymentProcessingTime)} ঘণ্টার মধ্যে পেমেন্ট\nবিকাশ নাম্বারে ট্রান্সফার হবে"
                    binding.paymentMessageLayout.isVisible = true
                } else {
                    binding.paymentMessageLayout.isVisible = false
                }
            }

        }
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val parentLayout: ConstraintLayout = view.findViewById(R.id.item_view_dashboard_parent)
        //internal val designIV: ImageView = view.findViewById(R.id.item_view_dashboard_design)
        internal val countTV: TextView = view.findViewById(R.id.item_view_dashboard_count_tv)
        internal val titleTV: TextView = view.findViewById(R.id.item_view_dashboard_msg_tv)
        //internal val iconIV: ImageView = view.findViewById(R.id.item_view_dashboard_icon)
        internal val countText: TextView = view.findViewById(R.id.countText)

        init {
            itemView.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(absoluteAdapterPosition, dataList[absoluteAdapterPosition])
                }
            }
        }
    }

    internal inner class ViewModel1(val binding: ItemViewDashboardPaymentBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onPayDetailsClick?.invoke(absoluteAdapterPosition, dataList[absoluteAdapterPosition])
                }
            }
        }
    }

    internal inner class ViewModel2(val binding: ItemViewDashboardUnpaidCodBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onCODCollectionClick?.invoke(absoluteAdapterPosition, dataList[absoluteAdapterPosition])
                }
            }
            binding.actionLayout.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onPaymentRequestClick?.invoke(absoluteAdapterPosition, dataList[absoluteAdapterPosition])
                }
            }
            binding.paymentMessageLayout.setOnClickListener {

            }
        }
    }

}