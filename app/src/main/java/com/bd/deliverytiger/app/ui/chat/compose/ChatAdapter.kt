package com.bd.deliverytiger.app.ui.chat.compose

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ItemViewChatMineMsgBinding
import com.bd.deliverytiger.app.databinding.ItemViewChatOtherMsgBinding
import com.bd.deliverytiger.app.ui.chat.model.ChatData
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.generateNameInitial
import com.bumptech.glide.Glide

class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ChatData> = mutableListOf()
    private val userId = SessionManager.courierUserId.toString()
    var onItemClicked: ((model: ChatData) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position].id == userId) {
            1
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val binding: ItemViewChatMineMsgBinding = ItemViewChatMineMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewModelMine(binding)
        } else {
            val binding: ItemViewChatOtherMsgBinding = ItemViewChatOtherMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewModelOther(binding)
        }

    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewModelMine) {
            val model = dataList[position]
            val binding = holder.binding

            binding.message.text = model.message
            /*if (!model.customerImgUrl.isNullOrEmpty()) {
                Glide.with(binding.logo).load(model.customerImgUrl).into(binding.logo)
                binding.nameInitial.text = ""
            } else {
                Glide.with(binding.logo).load(R.drawable.bg_chat_user).into(binding.logo)
                binding.nameInitial.text = generateNameInitial(model.customerName)
            }*/

            Glide.with(binding.profileImage).load(R.drawable.bg_chat_user).into(binding.profileImage)
            binding.nameInitial.text = generateNameInitial(model.name)

        } else if (holder is ViewModelOther) {
            val model = dataList[position]
            val binding = holder.binding

            binding.message.text = model.message
            /*if (!model.customerImgUrl.isNullOrEmpty()) {
                            Glide.with(binding.logo).load(model.customerImgUrl).into(binding.logo)
                            binding.nameInitial.text = ""
                        } else {
                            Glide.with(binding.logo).load(R.drawable.bg_chat_user).into(binding.logo)
                            binding.nameInitial.text = generateNameInitial(model.customerName)
                        }*/

            Glide.with(binding.profileImage).load(R.drawable.bg_chat_user).into(binding.profileImage)
            binding.nameInitial.text = generateNameInitial(model.name)
        }
    }

    inner class ViewModelMine(val binding: ItemViewChatMineMsgBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
    }

    inner class ViewModelOther(val binding: ItemViewChatOtherMsgBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[adapterPosition])
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

    fun addNewData(list: List<ChatData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun lastItem(): ChatData {
        return dataList.last()
    }

}