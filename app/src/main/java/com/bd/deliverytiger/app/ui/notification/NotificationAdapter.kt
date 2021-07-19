package com.bd.deliverytiger.app.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ItemViewNotificationBinding
import com.bd.deliverytiger.app.fcm.FCMData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<FCMData> = mutableListOf()
    var onItemClicked: ((model: FCMData, position: Int) -> Unit)? = null
    var onItemDelete: ((model: FCMData, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemViewNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder){
            val model = dataList[position]
            val binding = holder.binding

            binding.title.text = model.title
            binding.body.text = model.body
            binding.date.text = model.createdAt

            if (model.bigText.isNullOrEmpty()) {
                binding.bigText.text = ""
                binding.bigText.isVisible = false
            } else {
                binding.bigText.text = model.bigText
                binding.bigText.isVisible = true
            }

            if (model.imageUrl.isNullOrEmpty()) {
                binding.image.isVisible = false
            } else {
                Glide.with(binding.image)
                    .load(model.imageUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_banner_place).error(R.drawable.ic_banner_place))
                    .into(binding.image)
                binding.image.isVisible = true
            }


        }
    }

    internal inner class ViewHolder(val binding: ItemViewNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked?.invoke(dataList[position], position)
                }
            }
            binding.deleteBtn.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemDelete?.invoke(dataList[position], position)
                }
            }
        }
    }

    fun initLoad(list: List<FCMData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun removeByIndex(position: Int) {
        if (position in 0..dataList.lastIndex) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }
}