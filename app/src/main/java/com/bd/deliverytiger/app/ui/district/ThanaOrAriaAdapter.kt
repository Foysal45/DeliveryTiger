package com.bd.deliverytiger.app.ui.district

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.district.ThanaPayLoad

class ThanaOrAriaAdapter(private var context: Context,private var thanaOrAriaList: ArrayList<ThanaPayLoad>): RecyclerView.Adapter<ThanaOrAriaAdapter.mViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
       return mViewHolder(LayoutInflater.from(context).inflate(R.layout.district_select_layout,parent,false))
    }

    override fun getItemCount(): Int {
       return thanaOrAriaList.size
    }

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {
        holder.district_spinner_item_id.text = thanaOrAriaList[position].thanaBng
    }


    inner  class mViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val district_spinner_item_id: TextView = itemView.findViewById(R.id.locationName)

        init {
            itemView.setOnClickListener {
                onClickedListener?.onClick(adapterPosition)
            }
        }
    }

    interface OnClickedListener{
        fun onClick(pos: Int);
    }
    fun setOnClick(onClickedListener: OnClickedListener?){
        this.onClickedListener = onClickedListener
    }
    var onClickedListener: OnClickedListener? = null
}