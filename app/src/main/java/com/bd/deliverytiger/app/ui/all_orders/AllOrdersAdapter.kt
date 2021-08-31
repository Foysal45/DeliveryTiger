package com.bd.deliverytiger.app.ui.all_orders

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.databinding.ItemViewAllOrderBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class AllOrdersAdapter(var context: Context, var dataList: MutableList<CourierOrderViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: ((model: CourierOrderViewModel, position: Int) -> Unit)? = null
    var onOrderItemClick: ((position: Int) -> Unit)? = null
    var onEditItemClick: ((position: Int) -> Unit)? = null
    var onLocationBtnClick: ((model: CourierOrderViewModel, position: Int) -> Unit)? = null
    var onActionClicked: ((model: CourierOrderViewModel, position: Int) -> Unit)? = null
    var isFromDashBoard: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewAllOrderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_all_order, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            holder.binding.orderId.text = model?.courierOrdersId.toString()

            val formattedDate = DigitConverter.toBanglaDate(model?.courierOrderDateDetails?.orderDate, "MM-dd-yyyy HH:mm:ss")
            holder.binding.date.text = formattedDate

            holder.binding.reference.text = model?.courierOrderInfo?.collectionName
            holder.binding.customerName.text = model?.customerName
            var mobile = "${model?.courierAddressContactInfo?.mobile}"
            if (model?.courierAddressContactInfo?.otherMobile?.isEmpty() == false) {
                mobile += ",${model?.courierAddressContactInfo?.otherMobile}"
            }
            holder.binding.customerPhone.text = mobile
            //holder.binding.status.text = model?.status
            val status = if (isFromDashBoard) {
                model.dashboardStatusGroup
            } else {
                model.orderTrackStatusGroup
            }
            holder.binding.status.text = status


            /*
            holder.binding.address.text = model?.courierAddressContactInfo?.address
            val district = "${model?.courierAddressContactInfo?.thanaName},${model?.courierAddressContactInfo?.districtName}"
            holder.binding.district.text = district
            holder.binding.serviceCharge.text = "${DigitConverter.toBanglaDigit(model?.courierPrice?.totalServiceCharge)} ৳"
            holder.binding.collectionAmount.text = "${DigitConverter.toBanglaDigit(model?.courierPrice?.collectionAmount)} ৳"
            */

            if (model.buttonFlag) {
                holder.binding.editBtn.visibility = View.VISIBLE
            } else {
                holder.binding.editBtn.visibility = View.GONE
            }
            /*if (BuildConfig.DEBUG) {
                holder.binding.editBtn.visibility = View.VISIBLE
            }*/

            // 19 LP থেকে ফেরতকৃত প্রোডাক্টটি DT হেড অফিস গ্রহন করেছে
            // 60 রিটার্ন প্রোডাক্ট হাব থেকে সংগ্রহ করুন
            if (model.statusId == 60 || model.statusId == 19 || model.statusId == 15) {

                if (model.statusId == 60 || model.statusId == 19) {
                    holder.binding.hubLocationBtn.visibility = View.VISIBLE
                    holder.binding.hubName.text = "${model.hubViewModel?.name}-এ আছে"
                    holder.binding.trackBtn.visibility = View.GONE
                }
                if (model.statusId == 15) {
                    holder.binding.key1.text = "ডেলিভারি তারিখ"
                    val formattedDate1 = DigitConverter.toBanglaDate(model?.courierOrderDateDetails?.updatedOnDate, "MM-dd-yyyy HH:mm:ss")
                    holder.binding.date.text = formattedDate1
                }

                /*holder.binding.address.visibility = View.GONE
                holder.binding.district.visibility = View.GONE
                holder.binding.serviceCharge.visibility = View.GONE
                holder.binding.collectionAmount.visibility = View.GONE
                holder.binding.key8.visibility = View.GONE
                holder.binding.key9.visibility = View.GONE
                holder.binding.key4.visibility = View.GONE
                holder.binding.key5.visibility = View.GONE*/

            } else {
                holder.binding.hubLocationBtn.visibility = View.GONE
                holder.binding.trackBtn.visibility = View.VISIBLE
                holder.binding.key1.text = "তারিখ"


                /*holder.binding.address.visibility = View.VISIBLE
                holder.binding.district.visibility = View.VISIBLE
                holder.binding.serviceCharge.visibility = View.VISIBLE
                holder.binding.collectionAmount.visibility = View.VISIBLE
                holder.binding.key8.visibility = View.VISIBLE
                holder.binding.key9.visibility = View.VISIBLE
                holder.binding.key4.visibility = View.VISIBLE
                holder.binding.key5.visibility = View.VISIBLE*/
            }

            when (model.statusId) {
                // কাস্টমার প্রোডাক্ট নিতে চায়নি, ক্রেতা ফোনে পাওয়া যায়নি
                26, 33 -> {
                    binding.actionBtn.isVisible = true
                    binding.actionBtn.text = "আবার ডেলিভারি এটেম্পট নিন"
                }
                else -> {
                    binding.actionBtn.isVisible = false
                }
            }

        }

    }

    inner class ViewHolder(val binding: ItemViewAllOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
            binding.trackBtn.setOnClickListener {
                onOrderItemClick?.invoke(absoluteAdapterPosition)
            }
            binding.editBtn.setOnClickListener {
                onEditItemClick?.invoke(absoluteAdapterPosition)
            }
            binding.hubLocationBtn.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onLocationBtnClick?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
            binding.actionBtn.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onActionClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
        }
    }

}