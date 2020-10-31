package com.bd.deliverytiger.app.ui.chat.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ItemViewChatHistoryBinding
import com.bd.deliverytiger.app.ui.chat.model.ChatHistoryData
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.generateNameInitial
import com.bumptech.glide.Glide

class ChatHistoryAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val dataList: MutableList<ChatHistoryData> = mutableListOf()
    var onItemClicked: ((model: ChatHistoryData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewChatHistoryBinding = ItemViewChatHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.title.text = model.customerName
            binding.body.text = model.lastMsg
            binding.date.text = DigitConverter.formatDate(model.time ?: "", "HH:mm:ss '-' dd/MMM/yyyy", "dd/MM/yy")

            when (model.seenStatus) {
                "wow" -> {
                    binding.status.visibility = View.VISIBLE
                    Glide.with(binding.status).load(R.drawable.ic_done_all).into(binding.status)
                    binding.body.setTextColor(ContextCompat.getColor(binding.body.context, R.color.black_70))
                }
                "notDone" -> {
                    binding.status.visibility = View.VISIBLE
                    Glide.with(binding.status).load(R.drawable.ic_done).into(binding.status)
                    binding.body.setTextColor(ContextCompat.getColor(binding.body.context, R.color.black_90))
                }
                else -> {
                    binding.status.visibility = View.GONE
                    binding.body.setTextColor(ContextCompat.getColor(binding.body.context, R.color.black_70))
                }
            }


            /*if (!model.customerImgUrl.isNullOrEmpty()) {
                Glide.with(binding.logo).load(model.customerImgUrl).into(binding.logo)
                binding.nameInitial.text = ""
            } else {
                Glide.with(binding.logo).load(R.drawable.bg_chat_user).into(binding.logo)
                binding.nameInitial.text = generateNameInitial(model.customerName)
            }*/

            Glide.with(binding.logo).load(R.drawable.bg_chat_user).into(binding.logo)
            binding.nameInitial.text = generateNameInitial(model.customerName)

        }
    }

    inner class ViewModel(val binding: ItemViewChatHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
    }

    fun initLoad(list: List<ChatHistoryData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<ChatHistoryData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun addNewData(list: List<ChatHistoryData>) {
        val newDataCount = list.size
        dataList.addAll(0, list)
        notifyItemRangeInserted(0, newDataCount)
    }

    fun lastItem(): ChatHistoryData {
        return dataList.last()
    }

}