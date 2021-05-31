package com.bd.deliverytiger.app.ui.live.deal_management

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.deal_management.DealManagementData
import com.bd.deliverytiger.app.databinding.ItemViewLiveDealManagementBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class LiveDealManagementAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<DealManagementData> = mutableListOf()
    var onItemClicked: ((model: DealManagementData) -> Unit)? = null
    var onAddClicked: ((model: DealManagementData) -> Unit)? = null

    private val options = RequestOptions().placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewLiveDealManagementBinding = ItemViewLiveDealManagementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            Glide.with(binding.productImage).load(model.imageLink).apply(options).into(binding.productImage)

            binding.title.text = model.dealTitle
            val price = model.dealPrice?.toDoubleOrNull()?.toInt() ?: 0
            binding.price.text = "দামঃ ${DigitConverter.toBanglaDigit(price, true)}৳"
            binding.dealCode.text = "ডিল কোড: "+model.dealId.toString()


        }
    }

    inner class ViewModel(val binding: ItemViewLiveDealManagementBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.productAddBtn.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onAddClicked?.invoke(dataList[absoluteAdapterPosition])
                }
            }
        }
    }

    fun initLoad(list: List<DealManagementData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<DealManagementData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun dealCount(): Int = dataList.size

}