package com.bd.deliverytiger.app.ui.charges

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse

class ShipmentChargeAdapter(private val mContext: Context?, private var dataList: List<DeliveryChargeResponse>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_shipment_charge,
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

            holder.weightTV.text = model.weight
            val deliveryTypeModelList = model.deliveryTypeModel
            if (deliveryTypeModelList != null) {

                if (deliveryTypeModelList.size == 1){
                    holder.regularLayout.visibility = View.VISIBLE
                    holder.expressLayout.visibility = View.GONE
                    holder.regularTV.text = deliveryTypeModelList[0].deliveryType

                    val deliveryAdapter = ShipmentDeliveryAdapter(holder.regualrRV.context, deliveryTypeModelList[0].deliveryDayChargeModel)
                    with(holder.regualrRV){
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(holder.regualrRV.context, RecyclerView.VERTICAL, false)
                        adapter = deliveryAdapter
                    }

                } else if (deliveryTypeModelList.size >= 2) {
                    holder.regularLayout.visibility = View.VISIBLE
                    holder.expressLayout.visibility = View.VISIBLE

                    holder.regularTV.text = deliveryTypeModelList[0].deliveryType
                    val deliveryAdapter1 = ShipmentDeliveryAdapter(holder.regualrRV.context, deliveryTypeModelList[0].deliveryDayChargeModel)
                    with(holder.regualrRV){
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(holder.regualrRV.context, RecyclerView.VERTICAL, false)
                        adapter = deliveryAdapter1
                    }

                    holder.expressTV.text = deliveryTypeModelList[1].deliveryType
                    val deliveryAdapter2 = ShipmentDeliveryAdapter(holder.expressRV.context, deliveryTypeModelList[1].deliveryDayChargeModel)
                    with(holder.expressRV){
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(holder.expressRV.context, RecyclerView.VERTICAL, false)
                        adapter = deliveryAdapter2
                    }
                }
            }



        }
    }


    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val weightTV: TextView = view.findViewById(R.id.item_view_shipment_weight)
        internal val regularLayout: LinearLayout = view.findViewById(R.id.item_view_shipment_regular_layout)
        internal val regularTV: TextView = view.findViewById(R.id.item_view_shipment_regular_tv)
        internal val regualrRV: RecyclerView = view.findViewById(R.id.item_view_shipment_regular_rv)
        internal val expressLayout: LinearLayout = view.findViewById(R.id.item_view_shipment_express_layout)
        internal val expressTV: TextView = view.findViewById(R.id.item_view_shipment_express_tv)
        internal val expressRV: RecyclerView = view.findViewById(R.id.item_view_shipment_express_rv)

        init {
            view.setOnClickListener {

            }
        }
    }

}