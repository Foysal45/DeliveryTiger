package com.bd.deliverytiger.app.ui.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R

class NotificationAdapter(private val mContent: Context, val datalist: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_notification,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder){
            val model = datalist[position]
            holder.titleTV.text = model
            holder.dateTV.text = "7:58 PM 10.06.19"
        }
    }


    internal inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        internal val titleTV: TextView = view.findViewById(R.id.notification_title)
        internal val dateTV: TextView = view.findViewById(R.id.notification_date)

    }
}