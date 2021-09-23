package com.bd.deliverytiger.app.ui.chat.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.chat.HistoryData
import com.bd.deliverytiger.app.databinding.ItemViewChatHistoryBinding
import com.bd.deliverytiger.app.utils.generateNameInitial
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*

class ChatHistoryAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val dataList: MutableList<HistoryData> = mutableListOf()
    var onItemClicked: ((model: HistoryData) -> Unit)? = null

    private val sdf = SimpleDateFormat("dd MMM, yy", Locale.US)
    private var option = RequestOptions()
        .placeholder(R.drawable.ic_person_circle)
        .error(R.drawable.ic_person_circle)
        .circleCrop()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewChatHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.title.text = model.receiverName
            binding.body.text = model.message
            val color = if (model.seenStatus == 0) {
                ContextCompat.getColor(binding.parent.context, R.color.unread_background)
            } else {
                ContextCompat.getColor(binding.parent.context, R.color.read_background)
            }
            binding.parent.setBackgroundColor(color)
            try {
                binding.date.text = sdf.format(model.date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Glide.with(binding.logo)
                .load(model.receiverProfile)
                .apply(option)
                .into(binding.logo)

            /*if (model.receiverProfile.isNotEmpty()) {
                binding.nameInitial.isVisible = false
            } else {
                binding.nameInitial.isVisible = true
                binding.nameInitial.text = generateNameInitial(model.receiverName)
            }*/


            /*when (model.seenStatus) {
                "wow" -> {
                    binding.status.visibility = View.VISIBLE
                    Glide.with(binding.status).load(R.drawable.ic_done_all).into(binding.status)
                    binding.body.setTextColor(ContextCompat.getColor(binding.body.context, R.color.black_70))
                    binding.parent.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }
                "notDone" -> {
                    binding.status.visibility = View.VISIBLE
                    Glide.with(binding.status).load(R.drawable.ic_done).into(binding.status)
                    binding.body.setTextColor(ContextCompat.getColor(binding.body.context, R.color.black_90))
                    binding.parent.setBackgroundColor(Color.parseColor("#D9F3FF"))
                }
                else -> {
                    binding.status.visibility = View.GONE
                    binding.body.setTextColor(ContextCompat.getColor(binding.body.context, R.color.black_70))
                    binding.parent.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }
            }*/


            /*if (!model.customerImgUrl.isNullOrEmpty()) {
                Glide.with(binding.logo).load(model.customerImgUrl).into(binding.logo)
                binding.nameInitial.text = ""
            } else {
                Glide.with(binding.logo).load(R.drawable.bg_chat_user).into(binding.logo)
                binding.nameInitial.text = generateNameInitial(model.customerName)
            }*/


            


        }
    }

    inner class ViewModel(val binding: ItemViewChatHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    fun initLoad(list: List<HistoryData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<HistoryData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun addNewData(list: List<HistoryData>) {
        val newDataCount = list.size
        dataList.addAll(0, list)
        notifyItemRangeInserted(0, newDataCount)
    }

    fun addNewData(model: HistoryData) {
        dataList.firstOrNull()?.let {
            if (it == model) return
        }
        val currentIndex = dataList.size
        dataList.add(0, model)
        notifyItemInserted(0)
    }

    fun addUniqueHistory(model: HistoryData) {
        val oldModelIndex = dataList.indexOfFirst { it.receiverId == model.receiverId }
        if (oldModelIndex != -1) {
            dataList.removeAt(oldModelIndex)
        }
        dataList.add(0, model)
        notifyDataSetChanged()
    }

    fun lastItem(): HistoryData {
        return dataList.last()
    }

    fun firstItem(): HistoryData {
        return dataList.first()
    }

}