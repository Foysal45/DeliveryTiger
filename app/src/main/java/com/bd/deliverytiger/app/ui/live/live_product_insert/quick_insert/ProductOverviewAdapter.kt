package com.bd.deliverytiger.app.ui.live.live_product_insert.quick_insert

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductData
import com.bd.deliverytiger.app.databinding.ItemViewProductOverviewBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class ProductOverviewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LiveProductData> = mutableListOf()
    var onItemClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var onDeleteItemClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var selectedIndex: Int = -1

    private val options = RequestOptions().placeholder(R.drawable.ic_logo_ad1).error(R.drawable.ic_logo_ad1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewProductOverviewBinding = ItemViewProductOverviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            Glide.with(binding.preview)
                .load(model.coverPhoto)
                .apply(options)
                .into(binding.preview)

            binding.price.text = "৳ ${DigitConverter.toBanglaDigit(model.productPrice)}"

        }
    }

    inner class ViewModel(val binding: ItemViewProductOverviewBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.preview.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
            binding.deleteBtn.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
        }
    }

    fun initList(list: List<LiveProductData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun itemCount(): Int = dataList.size

    fun removeAt(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearSelection() {
        selectedIndex = -1
        notifyDataSetChanged()
    }
}