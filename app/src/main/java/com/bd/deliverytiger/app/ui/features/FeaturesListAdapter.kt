package com.bd.deliverytiger.app.ui.features

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R

class FeaturesListAdapter(private val context: Context, private val list: Array<String>) : RecyclerView.Adapter<FeaturesListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_feathuers, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.tvFeatures.text = list[position]
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvFeatures: TextView = itemView.findViewById(R.id.tvFeatures)
    }
}