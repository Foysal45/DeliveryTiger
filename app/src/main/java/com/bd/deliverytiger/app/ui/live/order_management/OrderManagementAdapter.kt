package com.bd.deliverytiger.app.ui.live.order_management

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.parseColor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.live.order_management.OrderManagementResponseModel
import com.bd.deliverytiger.app.databinding.ItemViewOrderCardBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bumptech.glide.Glide

class OrderManagementAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext: Context? = null
    private val dataList: MutableList<OrderManagementResponseModel> = mutableListOf()
    var onItemClicked: ((model: OrderManagementResponseModel) -> Unit)? = null
    private val fragmentTag = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewOrderCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding
            Glide.with(binding.itemOrderImage).load(model.imageLink).into(binding.itemOrderImage)

            binding.itemOrderDealTitleText.text = model.orderInfo?.dealTitle
            var statusBng = "স্ট্যাটাসঃ "+model.merchantStatusBng
            binding.itemOrderStatusText.text = statusBng
            binding.itemOrderStatusText.setTextColor(parseColor(model.statusColor.toString()))
            binding.itemOrderDealCodeText.text = DigitConverter.toBanglaDigit(model.orderInfo?.dealId.toString())
            binding.itemOrderBookingCodeText.text = DigitConverter.toBanglaDigit(model.couponId.toString())

            val parts: List<String> = model.orderInfo?.bookingDate?.split(" ") ?: listOf("")
            binding.itemOrderDateText.text = DigitConverter.toBanglaDate(parts.first(), "MM/dd/yyyy")
            /*val couponId = DigitConverter.toBanglaDigit(model.couponId, false)
            binding.itemOrderDealCodeText.text = couponId*/

        }

    }

    inner class ViewModel(val binding: ItemViewOrderCardBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION)
                    onItemClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    fun pagingLoad(list: List<OrderManagementResponseModel>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun initLoad(list: List<OrderManagementResponseModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}