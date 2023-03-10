package com.bd.deliverytiger.app.ui.lead_management

import android.content.res.ColorStateList
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.util.contains
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInformation
import com.bd.deliverytiger.app.databinding.ItemViewLeadManagementCustomerInfoBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class LeadManagementAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<CustomerInformation> = mutableListOf()
    private val filterDataList: MutableList<CustomerInformation> = mutableListOf()
    var onItemClicked: ((model: CustomerInformation, position: Int) -> Unit)? = null
    var onOrderDetailsClicked: ((model: CustomerInformation, position: Int) -> Unit)? = null
    var onSaveToPhoneBookClicked: ((model: CustomerInformation, position: Int) -> Unit)? = null

    private val selectedItems: SparseBooleanArray = SparseBooleanArray()
    // array used to perform multiple animation at once
    private var currentSelectedIndex = -1
    private var reverseAllAnimations = false
    var enableSelection: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewLeadManagementCustomerInfoBinding = ItemViewLeadManagementCustomerInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = filterDataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = filterDataList[position]
            val binding = holder.binding

            binding.name.text = model.customerName
            binding.district.text = model.districtsViewModel?.district ?: ""
            binding.mobileNumber.text = model.mobile
            binding.totalOrder.text = "${DigitConverter.toBanglaDigit(model.totalOrder)}?????? ?????????????????? "

            if (selectedItems[position, false]) {
                binding.parent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.parent.context, R.color.selection_color))
            } else {
                binding.parent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.parent.context, R.color.white))
            }
        }
    }

    inner class ViewModel(val binding: ItemViewLeadManagementCustomerInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
            }

            binding.orderLayout.setOnClickListener {
                onOrderDetailsClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
            }

        }
    }

    fun initLoad(list: List<CustomerInformation>) {
        dataList.clear()
        dataList.addAll(list)
        filterDataList.clear()
        filterDataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<CustomerInformation>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun lazyLoadWithFilter(list: List<CustomerInformation>, selectedTab: Int) {
        val start = filterDataList.size
        dataList.addAll(list)
        when (selectedTab) {
            1 -> {
                filterDataList.addAll(list)
                notifyItemRangeInserted(start, list.size)
            }
            2 -> {
                val filter = list.filter { it.totalOrder > 0 }
                filterDataList.addAll(filter)
                notifyItemRangeInserted(start, filter.size)
            }
            3 -> {
                val filter = list.filter { it.totalOrder == 0 }
                filterDataList.addAll(filter)
                notifyItemRangeInserted(start, filter.size)
            }
        }
    }


    fun allList(): List<CustomerInformation> = dataList

    fun multipleSelection(model : CustomerInformation, pos: Int) {
        this.currentSelectedIndex = pos
        if (selectedItems.contains(pos)){
            selectedItems.delete(pos)
        }else{
            selectedItems.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun clearList() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    fun getSelectedItemModelList(): List<CustomerInformation> {
        val items: MutableList<CustomerInformation> = ArrayList<CustomerInformation>(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(filterDataList[selectedItems.keyAt(i)])
        }
        return items
    }

    fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = java.util.ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun filter(selectedTab: Int): Boolean {
        when (selectedTab) {
            1 -> {
                filterDataList.clear()
                filterDataList.addAll(dataList)
                notifyDataSetChanged()
            }
            2 -> {
                filterDataList.clear()
                filterDataList.addAll(dataList.filter { it.totalOrder > 0 })
                notifyDataSetChanged()
            }
            3 -> {
                filterDataList.clear()
                filterDataList.addAll(dataList.filter { it.totalOrder == 0 })
                notifyDataSetChanged()
            }
        }
        return filterDataList.isEmpty()
    }


}