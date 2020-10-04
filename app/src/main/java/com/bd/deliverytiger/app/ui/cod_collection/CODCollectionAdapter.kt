package com.bd.deliverytiger.app.ui.cod_collection

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.databinding.ItemViewCodCollectionNewBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class CODCollectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<CourierOrderViewModel> = mutableListOf()
    var onTrackClicked: ((model: CourierOrderViewModel, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewCodCollectionNewBinding = ItemViewCodCollectionNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            val formattedDate = DigitConverter.toBanglaDate(model.courierOrderDateDetails?.confirmationDate.toString(), "MM-dd-yyyy HH:mm:ss")
            binding.key0.text = model.courierOrdersId.toString()
            binding.date.text = formattedDate

            binding.codCollection.text = "${DigitConverter.toBanglaDigit(model.courierPrice?.collectionAmount, true)} ৳"
            binding.statusName.text = model.statusTypeName

            if (model.statusType == "Paid" || model.statusType == "পেইড") {
                binding.statusName.setTextColor(ContextCompat.getColor(binding.statusName.context, R.color.colorPrimary))
            } else {
                binding.statusName.setTextColor(ContextCompat.getColor(binding.statusName.context, R.color.black_90))
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewCodCollectionNewBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.track.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onTrackClicked?.invoke(dataList[adapterPosition], adapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<CourierOrderViewModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<CourierOrderViewModel>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }


}