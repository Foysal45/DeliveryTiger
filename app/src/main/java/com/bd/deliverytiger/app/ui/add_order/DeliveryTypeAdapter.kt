package com.bd.deliverytiger.app.ui.add_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.charge.WeightRangeWiseData
import com.bd.deliverytiger.app.databinding.ItemViewDeliveryTypeBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import java.util.*

class DeliveryTypeAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<WeightRangeWiseData> = mutableListOf()
    private val specialList: MutableList<WeightRangeWiseData> = mutableListOf()
    var onItemClick: ((position: Int, model: WeightRangeWiseData) -> Unit)? = null
    private var selectedItem: Int = -1
    private var selectedItemModel: WeightRangeWiseData? = null

    private val options = RequestOptions()
        .signature(ObjectKey(Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toString()))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewDeliveryTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            if (position == selectedItem) {
                Glide.with(binding.deliveryTypeImage)
                    .load(model.onImageLink)
                    .apply(options)
                    .dontAnimate()
                    .into(binding.deliveryTypeImage)
            } else {
                Glide.with(binding.deliveryTypeImage)
                    .load(model.offImageLink)
                    .apply(options)
                    .dontAnimate()
                    .into(binding.deliveryTypeImage)
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewDeliveryTypeBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(position, dataList[position])
                    selectedItem = position
                    selectedItemModel = dataList[selectedItem]
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun initLoad(list: List<WeightRangeWiseData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun addRemoveSpecialService(isSpecialServiceAvailable: Boolean, list:List<WeightRangeWiseData>){

        if (!list.isNullOrEmpty()){
            dataList.addAll(list)
        }
        if (!isSpecialServiceAvailable){
            var data = dataList.filter { it.deliveryType == "Special Service" }
            dataList.removeAll(data)
        }
        selectPreSelection()
        //selectPreSelectionV2()
        notifyDataSetChanged()

    }

    fun selectPreSelection() {
        if (selectedItem != -1) {
            if (selectedItem in 0..dataList.lastIndex) {
                onItemClick?.invoke(selectedItem, dataList[selectedItem])
            }
        }
    }

    fun selectPreSelectionV2(){
        if(selectedItem != -1){
            if (selectedItemModel != null){
                if (selectedItemModel in dataList){
                    onItemClick?.invoke(selectedItem, selectedItemModel!!)
                }
            }
        }
    }

    fun selectByDeliveryRangeId(rangeId: Int) {
        val index = dataList.indexOfFirst { it.deliveryRangeId == rangeId }
        if (index != -1) {
            selectedItem = index
            onItemClick?.invoke(selectedItem, dataList[selectedItem])
        }
    }

    fun clearList() {
        selectedItem = -1
        dataList.clear()
        notifyDataSetChanged()
    }

    fun clearSelection(){
        selectedItem = -1
    }

}