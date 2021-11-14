package com.bd.deliverytiger.app.ui.payment_request

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ItemViewInstantPaymentRateBinding

class InstantPaymentRateAdapter:  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var chargeList: MutableList<String> = mutableListOf()
    private var discountList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewInstantPaymentRateBinding = ItemViewInstantPaymentRateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is InstantPaymentRateAdapter.ViewModel) {
            if (position % 2 == 0) {
                holder.binding.itemRoot.background = ContextCompat.getDrawable(
                    holder.binding.itemRoot.context,
                    R.drawable.bg_instant_payment_white
                )
            } else {
                holder.binding.itemRoot.background = ContextCompat.getDrawable(
                    holder.binding.itemRoot.context,
                    R.drawable.bg_instant_payment_gray
                )
            }
            val modelCharge = chargeList[position]
            val modelDiscount = discountList[position]
            val binding = holder.binding

            binding.chargeTv.text = modelCharge
            binding.discountAmountTv.text = modelDiscount

        }
    }

    override fun getItemCount(): Int {
        return chargeList.size
    }

    internal inner class ViewModel(val binding: ItemViewInstantPaymentRateBinding): RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }

    fun initData(ChargeList: MutableList<String>, DiscountList:MutableList<String>) {
        chargeList.clear()
        discountList.clear()
        chargeList.addAll(ChargeList)
        discountList.addAll(DiscountList)
    }

}