package com.bd.deliverytiger.app.ui.dana.user_details_info

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.util.contains
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.loan_survey.CourierModel
import com.bd.deliverytiger.app.api.model.loan_survey.SelectedCourierModel
import com.bd.deliverytiger.app.databinding.ItemViewCourierOptionsBinding

class LoanSurveyAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<CourierModel> = mutableListOf()

    var onItemClicked: ((model: CourierModel, position: Int) -> Unit)? = null

    private val selectedItems: SparseBooleanArray = SparseBooleanArray()
    // array used to perform multiple animation at once
    private var currentSelectedIndex = -1

    var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemViewCourierOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            binding.title.text = model.courierName
            /*val background = if (selectedPosition == position) {
                ContextCompat.getDrawable(binding.title.context, R.drawable.bg_item_selected_border)
            } else {
                ContextCompat.getDrawable(binding.title.context, R.drawable.bg_item_unselected_border)
            }
            binding.title.background = background*/

            val color = if (selectedPosition == position) {
                ContextCompat.getColor(binding.title.context, R.color.white)
            } else {
                ContextCompat.getColor(binding.title.context, R.color.black_80)
            }
            binding.title.setTextColor(color)

            var background = if (selectedItems[position, false]) {
                ContextCompat.getDrawable(binding.title.context, R.drawable.bg_live_schedule_date_selected)
            } else {
                ContextCompat.getDrawable(binding.title.context, R.drawable.bg_live_schedule_date_unselected)
            }
            binding.title.background = background
        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val binding: ItemViewCourierOptionsBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                /*val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked?.invoke(dataList[position], position)
                    selectedPosition = position
                    notifyDataSetChanged()
                }*/
            }
        }
    }

    fun initLoad(list: List<CourierModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun multipleSelection(model : CourierModel, pos: Int) {
        this.currentSelectedIndex = pos
        if (selectedItems.contains(pos)){
            selectedItems.delete(pos)
        }else{
            selectedItems.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun getSelectedItemModelList(): List<SelectedCourierModel> {
        val items: MutableList<SelectedCourierModel> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(SelectedCourierModel(
                dataList[selectedItems.keyAt(i)].courierId,
                dataList[selectedItems.keyAt(i)].courierName
            ))
        }
        return items
    }

}