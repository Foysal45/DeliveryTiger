package com.bd.deliverytiger.app.ui.live.live_product_insert.quick_insert

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductData
import com.bd.deliverytiger.app.databinding.ItemViewProductHighlightBinding
import com.bd.deliverytiger.app.utils.dpToPx
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class ProductHighlightAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LiveProductData> = mutableListOf()
    var onItemClick: ((model: LiveProductData, position: Int, isSelected: Boolean) -> Unit)? = null
    var onDeleteItemClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var selectedIndex: Int = -1

    private val options = RequestOptions().placeholder(R.drawable.ic_logo_ad1).error(R.drawable.ic_logo_ad1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewProductHighlightBinding = ItemViewProductHighlightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            if (position == selectedIndex) {
                binding.preview.layoutParams = holder.selectedParams
                binding.preview.setBackgroundResource(R.drawable.bg_live_product_preview_border_yellow)
            } else {
                binding.preview.layoutParams = holder.unselectedParams
                binding.preview.setBackgroundResource(R.drawable.bg_live_product_preview_border)
            }
            Glide.with(binding.preview)
                .load(model.coverPhoto)
                .apply(options)
                .dontAnimate()
                .into(binding.preview)
        }
    }

    inner class ViewModel(val binding: ItemViewProductHighlightBinding): RecyclerView.ViewHolder(binding.root) {

        private val size = binding.preview.context.dpToPx(50f)
        private val size1 = binding.preview.context.dpToPx(60f)
        val unselectedParams = LinearLayout.LayoutParams(size, size)
        val selectedParams = LinearLayout.LayoutParams(size1, size1)

        init {
            binding.preview.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    if (selectedIndex == absoluteAdapterPosition) {
                        selectedIndex = -1
                    } else {
                        selectedIndex = absoluteAdapterPosition
                    }
                    onItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition, selectedIndex != -1)
                    notifyDataSetChanged()
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