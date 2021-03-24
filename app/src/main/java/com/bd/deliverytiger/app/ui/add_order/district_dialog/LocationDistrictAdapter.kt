package com.bd.deliverytiger.app.ui.add_order.district_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.location.LocationData


class LocationDistrictAdapter(private var dataList: MutableList<LocationData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClicked: ((position: Int, value: LocationData) -> Unit)? = null

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.district_select_layout,
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
            holder.displayNameBangla.text = model.displayNameBangla
            holder.displayNameEnglish.text = model.displayNameEng
        }
    }

    /**
     * ViewHolder
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val displayNameBangla: TextView = view.findViewById(R.id.locationName)
        internal val displayNameEnglish: TextView = view.findViewById(R.id.locationNameEng)

        init {
            view.setOnClickListener {
                val index = adapterPosition
                if (index >= 0 && index < dataList.size) {
                    onItemClicked?.invoke(index, dataList[index])
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