package com.bd.deliverytiger.app.ui.lead_management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInformation
import com.bd.deliverytiger.app.databinding.ItemViewLeadManagementCustomerInfoBinding
class LeadManagementAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<CustomerInformation> = mutableListOf()
    var onItemClicked: ((model: CustomerInformation) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewLeadManagementCustomerInfoBinding = ItemViewLeadManagementCustomerInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.name.text = model.customerName
            binding.mobileNumber.text = model.mobile
            binding.district.text = model.districtsViewModel?.district ?: ""
        }
    }

    inner class ViewModel(val binding: ItemViewLeadManagementCustomerInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    fun initLoad(list: List<CustomerInformation>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<CustomerInformation>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}