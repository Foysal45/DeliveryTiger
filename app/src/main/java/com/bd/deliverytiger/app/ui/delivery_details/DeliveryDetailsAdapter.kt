package com.bd.deliverytiger.app.ui.delivery_details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.complain.ComplainData
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsResponse
import com.bd.deliverytiger.app.databinding.ItemViewDeliveryDetailsBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class DeliveryDetailsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<DeliveryDetailsResponse> = mutableListOf()
    var onItemClick: ((position: Int, model: DeliveryDetailsResponse) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val binding: ItemViewDeliveryDetailsBinding = ItemViewDeliveryDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding
            binding.orderId.text = "DT-${model.courierOrdersId}"
            binding.date.text = "${model.orderDate}"
            binding.customerName.text = "${model.collectionName}"
            binding.customerPhone.text = "${model.mobile}"
            binding.status.text = "${model.statusNameEng}"
        }
    }

    internal inner class ViewModel(val binding: ItemViewDeliveryDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(adapterPosition, dataList[adapterPosition])
                }
            }
        }
    }

    fun initLoad(list: List<DeliveryDetailsResponse>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<DeliveryDetailsResponse>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}