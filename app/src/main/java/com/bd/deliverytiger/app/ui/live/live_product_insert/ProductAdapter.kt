package com.bd.deliverytiger.app.ui.live.live_product_insert

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductTemp
import com.bd.deliverytiger.app.databinding.ItemViewCatalogItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ProductAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LiveProductTemp> = mutableListOf()
    private val options = RequestOptions().placeholder(R.drawable.ic_live_placeholder)
    var onItemClick: ((model: LiveProductTemp) -> Unit)? = null
    var onItemRemove: ((model: LiveProductTemp, position: Int) -> Unit)? = null
    var onImagePick: ((model: LiveProductTemp, position: Int) -> Unit)? = null
    var onMsg: ((model: LiveProductTemp, position: Int) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewCatalogItemBinding = ItemViewCatalogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding, CustomTextWatcher())
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            Glide.with(binding.coverPhoto)
                .load(model.imageUrl)
                .apply(options)
                .into(binding.coverPhoto)

            holder.customTextWatcher.updatePosition(holder.absoluteAdapterPosition)
            val price = if (model.price > 0) model.price.toString() else ""
            binding.priceET.setText(price)
            binding.priceET.clearFocus()
            binding.parent.requestFocus()
            /*binding.priceET.doAfterTextChanged {
                model.price = it.toString().toIntOrNull() ?: 0
            }*/


            if (model.imageUrl.isNotEmpty()) {
                binding.coverUploadBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.coverUploadBtn.context, R.color.gray_200))
                binding.coverUploadBtn.setTextColor(ContextCompat.getColor(binding.coverUploadBtn.context, R.color.black_50))
                binding.coverUploadBtn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(binding.coverUploadBtn.context, R.color.black_30))
                binding.coverUploadBtn.text = "আপলোড হয়েছে"

                binding.removeBtn.isVisible = true

            } else {
                binding.coverUploadBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.coverUploadBtn.context, R.color.blue))
                binding.coverUploadBtn.setTextColor(ContextCompat.getColor(binding.coverUploadBtn.context, R.color.white))
                binding.coverUploadBtn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(binding.coverUploadBtn.context, R.color.white))
                binding.coverUploadBtn.text = "প্রোডাক্টের ছবি"

                binding.removeBtn.isVisible = false
            }
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

    inner class ViewModel(val binding: ItemViewCatalogItemBinding, val customTextWatcher: CustomTextWatcher): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[absoluteAdapterPosition])
                }
            }
            binding.coverUploadBtn.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onImagePick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
            binding.removeBtn.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    if (dataList.size > 1) {
                        onItemRemove?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                        dataList.removeAt(absoluteAdapterPosition)
                        notifyDataSetChanged()
                    } else {
                        onMsg?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                    }
                }
            }
        }

        fun enableTextWatcher() {
            binding.priceET.addTextChangedListener(customTextWatcher)
        }

        fun disableTextWatcher() {
            binding.priceET.removeTextChangedListener(customTextWatcher)
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

    fun initLoad(list: List<LiveProductTemp>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun lazyLoad(list: List<LiveProductTemp>) {
        val count = list.size
        val startIndex = dataList.size
        dataList.addAll(list)
        notifyItemRangeInserted(startIndex,count)
    }

    fun addItem(data: LiveProductTemp) {
        dataList.add(data)
        notifyDataSetChanged()
    }

    fun getList(): List<LiveProductTemp> {
        return dataList
    }

    fun updateImage(url: String, position: Int) {
        if (position in 0..dataList.lastIndex) {
            dataList[position].imageUrl = url
            notifyItemChanged(position)
        }
    }

    fun clearAndAddTemp(data: LiveProductTemp) {
        dataList.clear()
        dataList.add(data)
        notifyDataSetChanged()
    }


}