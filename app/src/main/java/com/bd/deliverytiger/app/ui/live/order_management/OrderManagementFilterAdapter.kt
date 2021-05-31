package com.bd.deliverytiger.app.ui.live.order_management

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R

class OrderManagementFilterAdapter(private val mContext: Context, mDataList: List<FilterDataModel>) : RecyclerView.Adapter<OrderManagementFilterAdapter.ViewHolder>() {
    private val mDataList: List<FilterDataModel>
    private var mOnItemClickLister: OnItemClickLister? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_view_order_management_filter, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val model: FilterDataModel = mDataList[i]
        viewHolder.filterText.setText(model.filterTag)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var filterText: TextView
        var filterClose: ImageView

        init {
            filterText = itemView.findViewById(R.id.om_filter_text)
            filterClose = itemView.findViewById(R.id.om_filter_close)
            itemView.setOnClickListener { view ->
                if (mOnItemClickLister != null) {
                    mOnItemClickLister!!.onItemClicked(view, adapterPosition)
                }
            }
        }
    }

    interface OnItemClickLister {
        fun onItemClicked(view: View?, position: Int)
    }

    fun setOnItemClickLister(lister: OnItemClickLister?) {
        mOnItemClickLister = lister
    }

    init {
        this.mDataList = mDataList
    }
}