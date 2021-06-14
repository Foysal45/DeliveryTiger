package com.bd.deliverytiger.app.ui.add_order.district_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.location.LocationData
import com.bd.deliverytiger.app.databinding.ItemViewLocationBinding


class LocationDistrictAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LocationData> = mutableListOf()
    var onItemClicked: ((position: Int, value: LocationData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemViewLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            binding.locationName.text = model.displayNameBangla
            binding.locationNameEng.text = model.displayNameEng

            val backgroundColor = if (model.isDeactivate)
                ContextCompat.getColor(binding.parent.context, R.color.black_15)
            else ContextCompat.getColor(binding.parent.context, R.color.white)
            binding.parent.setBackgroundColor(backgroundColor)
        }
    }

    inner class ViewHolder(val binding: ItemViewLocationBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked?.invoke(position, dataList[position])
                }
            }
        }
    }

    fun setDataList(list: List<LocationData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}