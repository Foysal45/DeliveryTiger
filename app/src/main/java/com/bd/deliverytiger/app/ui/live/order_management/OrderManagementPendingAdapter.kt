package com.bd.deliverytiger.app.ui.live.order_management

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.live.order_management.OrderManagementPendingResponseBody
import com.bd.deliverytiger.app.databinding.ItemViewLiveScheduleProductListBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bumptech.glide.Glide

class OrderManagementPendingAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext: Context? = null
    private val dataList: MutableList<OrderManagementPendingResponseBody> = mutableListOf()
    var onItemClicked: ((model: OrderManagementPendingResponseBody) -> Unit)? = null
    private val fragmentTag = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewLiveScheduleProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding
            Glide.with(binding.productImage).load(model.imageUrl).into(binding.productImage)

            binding.productTitle.text = "অর্ডার কোড: ${DigitConverter.toBanglaDigit(model.orderId)}"
            binding.productPrice.text = "দাম: ৳ ${DigitConverter.toBanglaDigit(model.productPrice)}"
            val parts: List<String> = model.insertedOn?.split("T") ?: listOf("")
            var dateFormat = DigitConverter.toBanglaDate(parts.first(), "yyyy-MM-dd")
            dateFormat = DigitConverter.formatDate(dateFormat, "yyyy-MM-dd","MM-dd-yyyy")
            binding.orderDate.text = "অর্ডার ডেট: ${DigitConverter.toBanglaDate(dateFormat, )}"
        }

    }

    inner class ViewModel(val binding: ItemViewLiveScheduleProductListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION)
                    onItemClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    fun pagingLoad(list: List<OrderManagementPendingResponseBody>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun initLoad(list: List<OrderManagementPendingResponseBody>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}