package com.bd.deliverytiger.app.ui.payment_details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.accounts.AccountsDetailsData
import com.bd.deliverytiger.app.databinding.ItemViewPaymentHistoryDetailsBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class PaymentDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<AccountsDetailsData> = mutableListOf()
    var onItemClicked: ((model: AccountsDetailsData, isOnlyDelivery: Boolean) -> Unit)? = null
    var isOnlyDelivery: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewPaymentHistoryDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_payment_history_details, parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding
            binding.orderCode.text = model.orderCode
            binding.totalCharge.text = "- ${DigitConverter.toBanglaDigit(model.totalCharge, true)} ৳"

            if (isOnlyDelivery) {
                binding.collectedAmount.visibility = View.GONE
                binding.netAmount.visibility = View.GONE
                binding.key2.visibility = View.GONE
                binding.key4.visibility = View.GONE
            } else {
                binding.collectedAmount.visibility = View.VISIBLE
                binding.netAmount.visibility = View.VISIBLE
                binding.key2.visibility = View.VISIBLE
                binding.key4.visibility = View.VISIBLE

                binding.collectedAmount.text = "${DigitConverter.toBanglaDigit(model.collectedAmount, true)} ৳"
                binding.netAmount.text = "${DigitConverter.toBanglaDigit(model.merchantPayable, true)} ৳"
            }

        }
    }

    inner class ViewModel(val binding: ItemViewPaymentHistoryDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked?.invoke(dataList[adapterPosition], isOnlyDelivery)
            }
        }
    }

    fun initLoad(list: List<AccountsDetailsData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<AccountsDetailsData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }
}