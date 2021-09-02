package com.bd.deliverytiger.app.ui.chat.compose

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.chat.ChatData
import com.bd.deliverytiger.app.databinding.ItemViewChatReceiverBinding
import com.bd.deliverytiger.app.databinding.ItemViewChatSenderBinding
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class ChatAdapter(private val userId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ChatData> = mutableListOf()
    var onItemClicked: ((model: ChatData) -> Unit)? = null
    private val sdf = SimpleDateFormat("hh:mm a", Locale.US)

    private val options = RequestOptions()
        .placeholder(R.drawable.ic_person_circle)
        .error(R.drawable.ic_person_circle)
        .circleCrop()
    private val optionsImage = RequestOptions()
        .placeholder(R.drawable.ic_placeholder_40)
        .error(R.drawable.ic_placeholder_40)
        .transform(RoundedCorners(40))
        //.transform(RoundedCorners(R.integer.msg_image_curve))

    override fun getItemViewType(position: Int): Int {
        val model = dataList[position]
        return if (model.id == userId) {
            1
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val binding = ItemViewChatSenderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewModelSender(binding)
        } else {
            val binding = ItemViewChatReceiverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewModelReceiver(binding)
        }
    }

    override fun getItemCount(): Int = dataList.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewModelSender) {
            val model = dataList[position]
            val binding = holder.binding

            binding.message.text = model.message
            try {
                val date = Date(model.date)
                val time = sdf.format(date)
                binding.timeStamp.text = time
            }catch (e: Exception) {
                e.printStackTrace()
            }

            when(model.type) {
                "msg" -> {
                    binding.message.text = model.message
                    binding.message.isVisible = true
                    binding.image.isVisible = false
                }
                "img" -> {
                    binding.message.isVisible = false
                    binding.image.isVisible = true
                    Glide.with(binding.image)
                        .load(model.url)
                        .apply(optionsImage)
                        .into(binding.image)
                }
                else -> {
                    binding.message.text = model.message
                    binding.message.isVisible = true
                    binding.image.isVisible = false
                }
            }

        } else if (holder is ViewModelReceiver) {
            val model = dataList[position]
            val binding = holder.binding

            binding.message.text = model.message
            try {
                val date = Date(model.date)
                val time = sdf.format(date)
                binding.timeStamp.text = time
            }catch (e: Exception) {
                e.printStackTrace()
            }

            when(model.type) {
                "msg" -> {
                    binding.message.text = model.message
                    binding.message.isVisible = true
                    binding.image.isVisible = false
                }
                "img" -> {
                    binding.message.isVisible = false
                    binding.image.isVisible = true
                    Glide.with(binding.image)
                        .load(model.url)
                        .apply(optionsImage)
                        .into(binding.image)
                }
                else -> {
                    binding.message.text = model.message
                    binding.message.isVisible = true
                    binding.image.isVisible = false
                }
            }

            Glide.with(binding.imageProfile)
                .load(model.profile)
                .apply(options)
                .into(binding.imageProfile)
        }
    }

    inner class ViewModelSender(val binding: ItemViewChatSenderBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    inner class ViewModelReceiver(val binding: ItemViewChatReceiverBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    fun initLoad(list: List<ChatData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<ChatData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun pagingLoadReverse(list: List<ChatData>) {
        val currentIndex = 0
        val newDataCount = list.size
        dataList.addAll(currentIndex, list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun addNewData(list: List<ChatData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun addNewData(model: ChatData) {
        dataList.lastOrNull()?.let {
            if (it == model) return
        }
        val currentIndex = dataList.size
        dataList.add(model)
        notifyItemInserted(currentIndex)
    }

    fun lastItem(): ChatData {
        return dataList.last()
    }

    fun firstItem(): ChatData {
        return dataList.first()
    }

    fun isDataExist(model: ChatData): Boolean {
        return dataList.contains(model)
    }

}