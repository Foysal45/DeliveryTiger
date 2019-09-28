package com.bd.deliverytiger.app.ui.add_order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.charge.WeightRangeWiseData

class DeliveryTypeAdapter(private val mContext: Context?, private var dataList: MutableList<WeightRangeWiseData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: ((position: Int, model: WeightRangeWiseData) -> Unit)? = null
    private var selectedItem: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_delivery_type,
                parent,
                false
            )
        )
    }

    /**
     * getItemCount
     */
    override fun getItemCount(): Int {
        return dataList.size
    }

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {

            val model = dataList[position]

            holder.dateRange.text = model.days
            holder.dateRangeUnit.text = "Days"
            holder.deliveryType.text = model.deliveryType

            val resId = if (position == selectedItem) {
                R.drawable.ic_delivery_regular_selected
            } else {
                R.drawable.ic_delivery_regular_unselected
            }
            holder.backgroundImage.setImageResource(resId)
            /*Glide.with(holder.backgroundImage.context)
                .load()
                .into(holder.backgroundImage)*/

        }
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val backgroundImage: ImageView = view.findViewById(R.id.item_view_delivery_type_image)
        internal val dateRange: TextView = view.findViewById(R.id.item_view_delivery_type_range)
        internal val dateRangeUnit: TextView = view.findViewById(R.id.item_view_delivery_type_range_unit)
        internal val deliveryType: TextView = view.findViewById(R.id.item_view_delivery_type_tv)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position >= 0 && position < dataList.size) {
                    onItemClick?.invoke(position, dataList[position])
                    selectedItem = position
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun clearSelectedItemPosition(){
        selectedItem = -1
    }
}