package com.bd.deliverytiger.app.ui.cod_collection

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.utils.DigitConverter

class CODCollectionAdapter(
    var context: Context,
    var courierOrderViewModelList: ArrayList<CourierOrderViewModel?>?
) :
    RecyclerView.Adapter<CODCollectionAdapter.myViewHolder>() {
    private var formattedDate = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_view_cod_collection,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return courierOrderViewModelList!!.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        formattedDate =
            DigitConverter.toBanglaDate(courierOrderViewModelList?.get(position)?.courierOrderDateDetails?.confirmationDate.toString())

        holder.tvCodItemCount.text = DigitConverter.toBanglaDigit(position + 1)
        holder.tvCodOrderId.text =
            courierOrderViewModelList?.get(position)?.courierOrdersId.toString()
        holder.tvCodOrderDate.text = formattedDate

        holder.tvCodCollectionAmount.text =
            "৳ " + DigitConverter.toBanglaDigit(courierOrderViewModelList?.get(position)?.courierPrice?.collectionAmount)
        holder.tvCodPaymentStatus.text = courierOrderViewModelList?.get(position)?.statusType

        if (courierOrderViewModelList?.get(position)?.statusType.equals("Paid")) {
            holder.itemMainLay.setBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            holder.itemMainLay.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }


    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCodItemCount: TextView = itemView.findViewById(R.id.tvCodItemCount)
        val tvCodOrderId: TextView = itemView.findViewById(R.id.tvCodOrderId)
        val tvCodOrderDate: TextView = itemView.findViewById(R.id.tvCodOrderDate)
        val tvCodCollectionAmount: TextView = itemView.findViewById(R.id.tvCodCollectionAmount)
        val tvCodPaymentStatus: TextView = itemView.findViewById(R.id.tvCodPaymentStatus)
        val codOrderInfoLay: LinearLayout = itemView.findViewById(R.id.codOrderInfoLay)
        val itemMainLay: LinearLayout = itemView.findViewById(R.id.itemMainLay)

        init {
            codOrderInfoLay.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }

    var onItemClick: ((position: Int) -> Unit)? = null
}