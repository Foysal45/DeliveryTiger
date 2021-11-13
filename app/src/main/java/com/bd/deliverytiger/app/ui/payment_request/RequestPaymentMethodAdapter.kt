package com.bd.deliverytiger.app.ui.payment_request

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_receieve.OptionImageUrl
import com.bd.deliverytiger.app.databinding.ItemViewRegularPaymentMethodBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import java.util.*

class RequestPaymentMethodAdapter:  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var paymentMethodLists: MutableList<OptionImageUrl> = mutableListOf()

    var onItemClick: ((model: OptionImageUrl, position: Int) -> Unit)? = null

    var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewRegularPaymentMethodBinding = ItemViewRegularPaymentMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RequestPaymentMethodAdapter.ViewModel) {
            val model = paymentMethodLists[position]
            val binding = holder.binding

            val options = RequestOptions()
                .placeholder(R.drawable.ic_banner_place)
                .signature(ObjectKey(Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toString()))

            Glide.with(binding?.deliveryTypeImage)
                .load(model.imageUrl)
                .apply(options)
                .dontAnimate()
                .into(binding?.deliveryTypeImage)

            if (selectedPosition == position) {
                binding.deliveryTypeImage.setBackgroundResource(R.drawable.bg_stroke_payment_method_selected)
            } else {
                binding.deliveryTypeImage.setBackgroundResource(R.drawable.bg_stroke_payment_method_unselected)
            }
        }
    }

    override fun getItemCount(): Int {
        return paymentMethodLists.size
    }

    internal inner class ViewModel(val binding: ItemViewRegularPaymentMethodBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    selectedPosition = absoluteAdapterPosition
                    onItemClick?.invoke(paymentMethodLists[absoluteAdapterPosition], absoluteAdapterPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun initData(list: List<OptionImageUrl>) {
        paymentMethodLists.clear()
        paymentMethodLists.addAll(list)
        notifyDataSetChanged()
    }

}