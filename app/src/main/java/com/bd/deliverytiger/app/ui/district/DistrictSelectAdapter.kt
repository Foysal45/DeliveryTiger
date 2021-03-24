package com.bd.deliverytiger.app.ui.district

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad

class DistrictSelectAdapter(var mContext: Context, var districtList: ArrayList<DistrictDeliveryChargePayLoad>) :
    RecyclerView.Adapter<DistrictSelectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.district_select_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return districtList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.spinnerItemId.text = districtList[position].districtBng
        holder.spinnerItemIdEng.text = districtList[position].district
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val spinnerItemId: TextView = itemView.findViewById(R.id.locationName)
        val spinnerItemIdEng: TextView = itemView.findViewById(R.id.locationNameEng)

        init {
            itemView.setOnClickListener{
                districtClick?.onClick(adapterPosition, districtList[adapterPosition].districtBng!!, districtList[adapterPosition].districtId)
            }
        }
    }

    interface DistrictClick {
        fun onClick(position: Int, name: String, clickedID: Int)
    }

    private var districtClick: DistrictClick? = null
     fun setOnClick(districtClick: DistrictClick?) {
        this.districtClick = districtClick
    }
}