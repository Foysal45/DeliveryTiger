package com.bd.deliverytiger.app.ui.profile.pickup_address

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.databinding.ItemViewPickupAddressBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class PickUpLocationAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<PickupLocation> = mutableListOf()
    var onItemClicked: ((model: PickupLocation) -> Unit)? = null
    var onEditClicked: ((model: PickupLocation) -> Unit)? = null
    var onDeleteClicked: ((model: PickupLocation) -> Unit)? = null
    var showCount: Boolean = false
    var showEdit: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewPickupAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            if (model.id == 0) {
                binding.district.text = "${model.districtName}"
                binding.thana.text = "${model.thanaName}"
                binding.address.text = ""
            } else {
                binding.district.text = "জেলা: ${model.districtName}"
                binding.thana.text = "থানা: ${model.thanaName}"
                binding.address.text = "পিকআপ ঠিকানা: ${model.pickupAddress}"
            }


            if (showCount) {
                binding.orderCount.visibility = View.VISIBLE
                binding.orderCount.text = "অর্ডার সংখ্যা: ${DigitConverter.toBanglaDigit(model.acceptedOrderCount)}"
            } else {
                binding.orderCount.visibility = View.GONE
            }

            if (showEdit) {
                binding.editBtn.visibility = View.VISIBLE
                binding.deleteBtn.visibility = View.VISIBLE
            } else {
                binding.editBtn.visibility = View.GONE
                binding.deleteBtn.visibility = View.GONE
            }

        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewModel(val binding: ItemViewPickupAddressBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked?.invoke(dataList[adapterPosition])
                }
            }
            binding.editBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onEditClicked?.invoke(dataList[adapterPosition])
                }
            }
            binding.deleteBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClicked?.invoke(dataList[adapterPosition])
                }
            }
        }
    }

    fun initList(list: List<PickupLocation>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(model: PickupLocation) {
        dataList.add(model)
        notifyItemInserted(dataList.lastIndex)
    }

}