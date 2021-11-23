package com.bd.deliverytiger.app.ui.loan_survey.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ItemViewLoanSurveyRecyclerItemBinding

class LocalUniversalAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<String> = mutableListOf()

    var onItemClicked: ((model: String, position: Int) -> Unit)? = null

    var hasPrevSelection = false

    var selectedPosition: Int = -1

    var selectedItem: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemViewLoanSurveyRecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            binding.title.text = model

            if(hasPrevSelection){
                selectedItem = dataList[selectedPosition]
            }
            val color = if (selectedPosition == position) {
                ContextCompat.getColor(binding.title.context, R.color.black_100)
            } else {
                ContextCompat.getColor(binding.title.context, R.color.black_80)
            }
            binding.title.setTextColor(color)

            val background = if (selectedPosition == position) {
                ContextCompat.getDrawable(
                    binding.title.context,
                    R.drawable.bg_live_schedule_date_selected
                )
            } else {
                ContextCompat.getDrawable(
                    binding.title.context,
                    R.drawable.bg_live_schedule_date_unselected
                )
            }
            binding.title.background = background

        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val binding: ItemViewLoanSurveyRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked?.invoke(dataList[position], position)
                    selectedPosition = position
                    selectedItem = dataList[position]
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun initLoad(list: List<String>, selectedItemFromAPi: Int = 0, hasPreviousSelection: Boolean = false) {
        selectedPosition = selectedItemFromAPi
        hasPrevSelection = hasPreviousSelection
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }


}

