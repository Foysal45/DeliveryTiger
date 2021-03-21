package com.bd.deliverytiger.app.ui.district.v2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R

class ListAdapter(var mContext: Context, var list: ArrayList<CustomModel>) :
    RecyclerView.Adapter<ListAdapter.DistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistViewHolder {
        return DistViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.district_select_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DistViewHolder, position: Int) {
        holder.spinnerItemId.text = list[position].bangName
        holder.spinnerItemIdEng.text = list[position].engName
    }

    inner class DistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val spinnerItemId: TextView = itemView.findViewById(R.id.district_spinner_item_id)
        val spinnerItemIdEng: TextView = itemView.findViewById(R.id.district_spinner_item_id_english)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(adapterPosition, list[adapterPosition].bangName, list[adapterPosition].id, list[adapterPosition].listPosition)
            }
        }
    }

    var onItemClick: ((adapterPosition: Int, name: String, id: Int, listPosition: Int) -> Unit)? = null

}