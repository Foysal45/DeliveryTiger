package com.bd.deliverytiger.app.ui.lead_management.customer_details_bottomsheet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInformation
import com.bd.deliverytiger.app.api.model.lead_management.customer_details.CustomerInfoDetails
import com.bd.deliverytiger.app.databinding.ItemViewCustomerDetailsBinding
import com.bd.deliverytiger.app.databinding.ItemViewLeadManagementCustomerInfoBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CustomerDetailsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<CustomerInfoDetails> = mutableListOf()
    var onItemClicked: ((model: CustomerInfoDetails) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewCustomerDetailsBinding = ItemViewCustomerDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.orderId.text = model.courierOrdersId
            if (model.podNumber.isNullOrEmpty()){
                binding.podNumber.text = "-------"
            }else{
                binding.podNumber.text = model.podNumber
            }
            binding.orderDate.text = convertISOTimeToDate(model.orderDate ?: "")

            binding.address.text = model.address
        }
    }

    inner class ViewModel(val binding: ItemViewCustomerDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertISOTimeToDate(isoTime: String): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        var convertedDate: Date? = null
        var formattedDate: String? = ""
        try {
            convertedDate = sdf.parse(isoTime)
            formattedDate = SimpleDateFormat("dd MMM, yyyy").format(convertedDate ?: "")
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formattedDate
    }

    fun initLoad(list: List<CustomerInfoDetails>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<CustomerInfoDetails>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}