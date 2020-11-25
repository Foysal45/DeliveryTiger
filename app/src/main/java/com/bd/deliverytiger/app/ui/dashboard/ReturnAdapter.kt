package com.bd.deliverytiger.app.ui.dashboard

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.databinding.ItemViewReturnTypeBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.dpToPx

class ReturnAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataList: MutableList<DashboardData> = mutableListOf()
    var onItemClick: ((position: Int, model: DashboardData) -> Unit)? = null
    var onMapClick: ((position: Int, model: DashboardData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewReturnTypeBinding = ItemViewReturnTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding
            binding.title.text = model.name
            binding.countTV.text = DigitConverter.toBanglaDigit(model.count)

            if (position == 0) {
                binding.title.setBackgroundColor(ContextCompat.getColor(binding.title.context, R.color.return_state_1))
                (binding.countTV.background as GradientDrawable).setStroke(binding.countTV.context.dpToPx(1f),  ContextCompat.getColor(binding.countTV.context, R.color.return_state_1))
                binding.countTV.setTextColor(ContextCompat.getColor(binding.title.context, R.color.black_90))
            } else if (position == 1) {
                binding.title.setBackgroundColor(ContextCompat.getColor(binding.title.context, R.color.return_state_2))
                (binding.countTV.background as GradientDrawable).setStroke(binding.countTV.context.dpToPx(1f),  ContextCompat.getColor(binding.countTV.context, R.color.return_state_2))
                binding.countTV.setTextColor(ContextCompat.getColor(binding.title.context, R.color.black_90))
            } else {
                binding.title.setBackgroundColor(ContextCompat.getColor(binding.title.context, R.color.return_state_3))
                (binding.countTV.background as GradientDrawable).setStroke(binding.countTV.context.dpToPx(1f),  ContextCompat.getColor(binding.countTV.context, R.color.return_state_3))
                binding.countTV.setTextColor(ContextCompat.getColor(binding.title.context, R.color.orange))
            }

            if (model.statusGroupId == 11 || model.statusGroupId == 10) {
                binding.mapBtn.setImageResource(R.drawable.ic_location1)
                binding.countTV.setPadding(0,0,binding.countTV.context.dpToPx(16f),0)
            } else if (model.statusGroupId == 9) {
                binding.mapBtn.visibility = View.GONE
                binding.countTV.setPadding(0,0,0,0)
            }
        }
    }

    internal inner class ViewModel(val binding: ItemViewReturnTypeBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(adapterPosition, dataList[adapterPosition])
                }
            }
            binding.mapBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onMapClick?.invoke(adapterPosition, dataList[adapterPosition])
                }
            }
        }
    }

    fun initData(list: MutableList<DashboardData>) {
        dataList.clear()
        dataList.addAll(list)
    }




}