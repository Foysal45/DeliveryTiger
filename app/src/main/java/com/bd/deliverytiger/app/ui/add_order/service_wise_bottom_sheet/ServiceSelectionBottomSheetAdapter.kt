package com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoData
import com.bd.deliverytiger.app.databinding.ItemViewServiceListsBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.sdk24orAbove

class ServiceSelectionBottomSheetAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ServiceInfoData> = mutableListOf()
    var onDistrictSelectionClick: ((position: Int, model: ServiceInfoData) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewServiceListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n", "NewApi")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            //binding.serviceTypeTitle.text =  Html.fromHtml(model.serviceTypeName,  Html.FROM_HTML_MODE_LEGACY)
            //binding.serviceInfo.text = Html.fromHtml( model.serviceInfo,  Html.FROM_HTML_MODE_LEGACY)

            sdk24orAbove { flag ->
                if (flag) {
                    binding.serviceTypeTitle.text =  Html.fromHtml(model.serviceTypeName,  Html.FROM_HTML_MODE_LEGACY)
                    binding.serviceInfo.text = Html.fromHtml( model.serviceInfo,  Html.FROM_HTML_MODE_LEGACY)
                } else {
                    binding.serviceTypeTitle.text =  Html.fromHtml( model.serviceTypeName)
                    binding.serviceInfo.text = Html.fromHtml( model.serviceInfo)
                }
            }

            if (model.deliveryRangeId.isNotEmpty()) {
                val firstRange = model.deliveryRangeId.first()
                if (firstRange == 14 || firstRange == 18) {
                    binding.serviceTypeSubTitle.text = "(শুধু জেলা সদর)"
                } else {
                    binding.serviceTypeSubTitle.text = ""
                }
            } else {
                binding.serviceTypeSubTitle.text = ""
            }

            /*if (model.deliveryRangeId.isEmpty()){
                binding.serviceRangeArea.text = "সারাদেশে"
            }else{
                binding.serviceRangeArea.text = "${DigitConverter.toBanglaDigit(model.districtList.count())} টি জেলা সদরে"
            }*/
            if (model.deliveryRangeId.isNotEmpty()) {
                binding.progressBar.isVisible = model.districtList.isEmpty()
            } else {
                binding.progressBar.isVisible = false
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewServiceListsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.etDistrict.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDistrictSelectionClick?.invoke(position, dataList[position])
                }
            }
        }
    }

    fun initLoad(list: List<ServiceInfoData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateData(model: ServiceInfoData) {
        dataList[model.index].districtList = model.districtList
        notifyDataSetChanged()
    }

}