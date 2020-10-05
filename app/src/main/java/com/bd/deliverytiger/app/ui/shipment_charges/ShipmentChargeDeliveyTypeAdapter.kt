package com.bd.deliverytiger.app.ui.shipment_charges

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.charge.DeliveryTypeModel
import com.bd.deliverytiger.app.databinding.ItemViewShipmentChargeDeliveryTypeBinding

class ShipmentChargeDeliveyTypeAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<DeliveryTypeModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding: ItemViewShipmentChargeDeliveryTypeBinding = ItemViewShipmentChargeDeliveryTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            binding.deliveryType.text = model.deliveryType
            val dataAdapter = ShipmentDeliveryAdapter(binding.recyclerView.context, model.deliveryDayChargeModel)
            with(binding.recyclerView) {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(binding.recyclerView.context)
                adapter = dataAdapter
            }

        }
    }


    internal inner class ViewHolder(val binding: ItemViewShipmentChargeDeliveryTypeBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    fun initLoad(list: List<DeliveryTypeModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<DeliveryTypeModel>) {
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