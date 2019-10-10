package com.bd.deliverytiger.app.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponseModel
import com.bd.deliverytiger.app.utils.DigitConverter


class DashboardAdapter(private val mContext: Context?, private var dataList: MutableList<DashboardResponseModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClick: ((position: Int, responseModel: DashboardResponseModel?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_dashboard,
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

            holder.titleTV.text = model.name
            var countMsg = "0"
            when {
                model.dashboardCountSumView == "countsum" -> {
                    countMsg = "৳ ${DigitConverter.toBanglaDigit(model.totalAmount.toInt())} (${DigitConverter.toBanglaDigit(model.count)})"
                }

                model.dashboardCountSumView == "count" -> {
                    countMsg = DigitConverter.toBanglaDigit(model.count)
                }

                model.dashboardCountSumView == "sum" -> {
                    countMsg = "৳ ${DigitConverter.toBanglaDigit(model.totalAmount.toInt())}"
                }
            }
            holder.countTV.text = countMsg

            when(model.dashboardViewColorType) {
                "positive" -> {
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#EEF8EF"))
                    holder.designIV.setImageResource(R.drawable.ic_dashboard_design_3)
                    holder.countTV.setTextColor(Color.parseColor("#66BB6A"))
                }
                "neutral" -> {
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#E1F3F3"))
                    holder.designIV.setImageResource(R.drawable.ic_dashboard_design_1)
                    holder.countTV.setTextColor(Color.parseColor("#73B4C8"))
                }
                "negative" -> {
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#FFEBDB"))
                    holder.designIV.setImageResource(R.drawable.ic_dashboard_design_4)
                    holder.countTV.setTextColor(Color.parseColor("#FF8E34"))
                }
                "waiting" -> {
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#FFF4DB"))
                    holder.designIV.setImageResource(R.drawable.ic_dashboard_design_2)
                    holder.countTV.setTextColor(Color.parseColor("#FFB300"))
                }
            }


        }
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val parentLayout: ConstraintLayout = view.findViewById(R.id.item_view_dashboard_parent)
        internal val designIV: ImageView = view.findViewById(R.id.item_view_dashboard_design)
        internal val countTV: TextView = view.findViewById(R.id.item_view_dashboard_count_tv)
        internal val titleTV: TextView = view.findViewById(R.id.item_view_dashboard_msg_tv)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(adapterPosition, dataList[adapterPosition])
            }
        }
    }

}