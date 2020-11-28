package com.bd.deliverytiger.app.ui.collection_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.collection_history.CollectionData
import com.bd.deliverytiger.app.databinding.ItemViewCollectionHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class CollectionHistoryAdapter:  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<CollectionData> = mutableListOf()
    var onItemClicked: ((model: CollectionData) -> Unit)? = null

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US) // 2020-11-28T11:34:18.363
    private val sdf1 = SimpleDateFormat("dd-MM-yyyy", Locale.US) // 2020-11-28T11:34:18.363

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewCollectionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            var formattedDate = ""
            try {
                val date = sdf.parse(model.postedOn)
                formattedDate = sdf1.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.date.text = formattedDate
            binding.orderId.text = model.courierOrderId
        }
    }

    inner class ViewModel(val binding: ItemViewCollectionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
    }

    fun initLoad(list: List<CollectionData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<CollectionData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

}