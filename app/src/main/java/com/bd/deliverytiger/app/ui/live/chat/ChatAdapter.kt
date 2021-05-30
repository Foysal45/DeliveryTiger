package com.bd.deliverytiger.app.ui.live.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.databinding.ItemViewChatLiveBinding
import com.bd.deliverytiger.app.ui.live.chat.model.ChatData

class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ChatData> = mutableListOf()

    var onItemClicked: ((model: ChatData) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewChatLiveBinding = ItemViewChatLiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModelMine(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewModelMine) {
            val model = dataList[position]
            val binding = holder.binding

            val userName = model.name
            val message = model.message
            val html = if (model.isHost){
                "<font color='#e74c3c'>$userName:</font> <font color='#FFFFFF'>$message</font>"
            } else {
                "<font color='#FFFF33'>$userName:</font> <font color='#FFFFFF'>$message</font>"
            }
            binding.message.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    inner class ViewModelMine(val binding: ItemViewChatLiveBinding) : RecyclerView.ViewHolder(binding.root) {

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