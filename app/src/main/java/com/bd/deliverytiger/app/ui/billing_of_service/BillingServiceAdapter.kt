package com.bd.deliverytiger.app.ui.billing_of_service

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R

class BillingServiceAdapter(var context: Context) :
    RecyclerView.Adapter<BillingServiceAdapter.myViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_view_billing_collection,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return 15
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
    }


    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}