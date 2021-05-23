package com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoDataModel
import com.bd.deliverytiger.app.databinding.ItemViewServiceListsBinding

class ServiceSelectionBottomSheetAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ServiceInfoDataModel> = mutableListOf()
    var onItemClick: ((position: Int, model: ServiceInfoDataModel) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewServiceListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            binding.serviceTypeTitle.text =  Html.fromHtml(model.serviceTypeName,  Html.FROM_HTML_MODE_COMPACT)
            binding.serviceInfo.text = Html.fromHtml( model.serviceInfo,  Html.FROM_HTML_MODE_COMPACT)

        }
    }

    inner class ViewHolder(val binding: ItemViewServiceListsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(position, dataList[position])
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun initLoad(list: List<ServiceInfoDataModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}