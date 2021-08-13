package com.bd.deliverytiger.app.ui.live.live_schedule

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.live_schedule.PriceTemp
import com.bd.deliverytiger.app.databinding.ItemViewPriceFieldBinding
import com.bumptech.glide.request.RequestOptions

class PriceAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<PriceTemp> = mutableListOf()
    private val options = RequestOptions().placeholder(R.drawable.ic_live_placeholder)
    var onItemClick: ((model: PriceTemp) -> Unit)? = null
    var onItemRemove: ((model: PriceTemp, position: Int) -> Unit)? = null
    var onMsg: ((model: PriceTemp, position: Int) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewPriceFieldBinding = ItemViewPriceFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding, CustomTextWatcher())
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            holder.customTextWatcher.updatePosition(holder.absoluteAdapterPosition)
            val price = if (model.price > 0) model.price.toString() else ""
            binding.productPrice.setText(price)
            binding.productPrice.clearFocus()
            binding.parent.requestFocus()
            /*binding.productPrice.doAfterTextChanged {
                model.price = it.toString().toIntOrNull() ?: 0
            }*/
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        (holder as ViewModel).enableTextWatcher()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as ViewModel).disableTextWatcher()
    }

    inner class ViewModel(val binding: ItemViewPriceFieldBinding, val customTextWatcher: CustomTextWatcher): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[absoluteAdapterPosition])
                }
            }
        }

        fun enableTextWatcher() {
            binding.productPrice.addTextChangedListener(customTextWatcher)
        }

        fun disableTextWatcher() {
            binding.productPrice.removeTextChangedListener(customTextWatcher)
        }
    }

    inner class CustomTextWatcher: TextWatcher {

        var position: Int = 0

        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            dataList[position].price = s.toString().toIntOrNull() ?: 0
        }
    }

    fun initLoad(list: List<PriceTemp>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun lazyLoad(list: List<PriceTemp>) {
        val count = list.size
        val startIndex = dataList.size
        dataList.addAll(list)
        notifyItemRangeInserted(startIndex,count)
    }

    fun addItem(data: PriceTemp) {
        dataList.add(data)
        notifyDataSetChanged()
    }

    fun getList(): List<PriceTemp> {
        return dataList
    }

    fun clearAndAddTemp(data: PriceTemp) {
        dataList.clear()
        dataList.add(data)
        notifyDataSetChanged()
    }


}