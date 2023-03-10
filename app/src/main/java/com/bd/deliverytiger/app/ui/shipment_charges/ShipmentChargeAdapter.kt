package com.bd.deliverytiger.app.ui.shipment_charges

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.databinding.ItemViewShipmentChargeBinding

class ShipmentChargeAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<DeliveryChargeResponse> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding: ItemViewShipmentChargeBinding = ItemViewShipmentChargeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding
            binding.weightTV.text = model.weight

            val dataAdapter = ShipmentChargeDeliveyTypeAdapter()
            dataAdapter.initLoad(model.deliveryTypeModel)
            with(binding.recyclerView) {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(binding.recyclerView.context)
                adapter = dataAdapter
            }

        }
    }


    internal inner class ViewHolder(val binding: ItemViewShipmentChargeBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    fun initLoad(list: List<DeliveryChargeResponse>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<DeliveryChargeResponse>) {
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