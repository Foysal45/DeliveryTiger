package com.bd.deliverytiger.app.ui.live.live_schedule_product

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductData
import com.bd.deliverytiger.app.databinding.ItemViewLiveScheduleProductBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class LiveScheduleProductListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LiveProductData> = mutableListOf()
    var onItemClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var onStockOutClick: ((model: LiveProductData, position: Int) -> Unit)? = null

    private val options = RequestOptions().placeholder(R.drawable.ic_logo_ad).error(R.drawable.ic_logo_ad)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewLiveScheduleProductBinding = ItemViewLiveScheduleProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            Glide.with(binding.productImage)
                .load(model.coverPhoto)
                .apply(options)
                .into(binding.productImage)

            binding.title.text = "প্রোডাক্ট কোড: ${DigitConverter.toBanglaDigit(model.id)}"
            binding.price.text = "৳${DigitConverter.toBanglaDigit(model.productPrice, true)}"
            if (model.isSoldOut) {
                binding.stockOutBtn.text = "স্টক ইন"
                binding.stockOutBtn.backgroundTintList = ContextCompat.getColorStateList(binding.stockOutBtn.context, R.color.green_900)
                binding.stock.text = "স্টক নেই"
            } else {
                binding.stockOutBtn.text = "স্টক আউট"
                binding.stockOutBtn.backgroundTintList = ContextCompat.getColorStateList(binding.stockOutBtn.context, R.color.red_900)
                binding.stock.text = "স্টক আছে"
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewLiveScheduleProductBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.stockOutBtn.setOnClickListener {
                onStockOutClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
            }

        }
    }

    fun initList(list: List<LiveProductData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun stockOutUpdate(flag: Boolean, position: Int) {
        dataList[position].isSoldOut = flag
        notifyItemChanged(position)
    }
}