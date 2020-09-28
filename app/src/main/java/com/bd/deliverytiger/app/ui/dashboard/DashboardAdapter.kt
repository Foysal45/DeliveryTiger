package com.bd.deliverytiger.app.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.utils.DigitConverter


class DashboardAdapter(private val mContext: Context?, private var dataList: MutableList<DashboardData>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClick: ((position: Int, data: DashboardData?) -> Unit)? = null

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

            var countMsg = "0"
            var titleMsg = ""
            var countText = ""
            when (model.dashboardCountSumView) {
                "countsum" -> {
                    countMsg = DigitConverter.toBanglaDigit(model.count)
                    titleMsg = "<font color='#CC000000'><b>৳${DigitConverter.toBanglaDigit(model.totalAmount.toInt(), true)}</b></font> ${model.name}"
                    if (model.statusGroupId == 6) {
                        countText = "${model.name}"
                        titleMsg = "<font color='#CC000000'><b>৳ ${DigitConverter.toBanglaDigit(model.totalAmount.toInt(), true)}</b></font> কালেক্ট হয়েছে"
                    }
                }
                "count" -> {
                    countMsg = DigitConverter.toBanglaDigit(model.count)
                    titleMsg = "${model.name}"
                }
                "sum" -> {
                    countMsg = "৳ ${DigitConverter.toBanglaDigit(model.totalAmount.toInt(), true)}"
                    titleMsg = "${model.name}"
                }
            }
            holder.countTV.text = countMsg
            holder.titleTV.text = HtmlCompat.fromHtml(titleMsg, HtmlCompat.FROM_HTML_MODE_LEGACY)
            holder.countText.text = countText


            when(model.dashboardViewColorType) {
                "positive" -> {
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#EEF8EF"))
                    //holder.designIV.setImageResource(R.drawable.ic_dashboard_design_3)
                    holder.countTV.setTextColor(Color.parseColor("#66BB6A"))
                }
                "neutral" -> {
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#E1F3F3"))
                    //holder.designIV.setImageResource(R.drawable.ic_dashboard_design_1)
                    holder.countTV.setTextColor(Color.parseColor("#73B4C8"))
                }
                "negative" -> {
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#FFEBDB"))
                    //holder.designIV.setImageResource(R.drawable.ic_dashboard_design_4)
                    holder.countTV.setTextColor(Color.parseColor("#FF8E34"))
                }
                "waiting" -> {
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#FFF4DB"))
                    //holder.designIV.setImageResource(R.drawable.ic_dashboard_design_2)
                    holder.countTV.setTextColor(Color.parseColor("#FFB300"))
                }
            }

            /*Glide.with(holder.iconIV.context)
                .load(model.dashboardImageUrl)
                .into(holder.iconIV)*/

        }
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val parentLayout: ConstraintLayout = view.findViewById(R.id.item_view_dashboard_parent)
        //internal val designIV: ImageView = view.findViewById(R.id.item_view_dashboard_design)
        internal val countTV: TextView = view.findViewById(R.id.item_view_dashboard_count_tv)
        internal val titleTV: TextView = view.findViewById(R.id.item_view_dashboard_msg_tv)
        //internal val iconIV: ImageView = view.findViewById(R.id.item_view_dashboard_icon)
        internal val countText: TextView = view.findViewById(R.id.countText)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(adapterPosition, dataList[adapterPosition])
            }
        }
    }

}