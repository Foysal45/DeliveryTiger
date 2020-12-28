package com.bd.deliverytiger.app.ui.complain

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.complain.ComplainData
import com.bd.deliverytiger.app.databinding.ItemViewComplainBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class ComplainAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<ComplainData> = mutableListOf()
    var onItemClicked: ((model: ComplainData) -> Unit)? = null
    private val charLimit = 85
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewComplainBinding = ItemViewComplainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            //model.complain = "The quick brown fox jumps over the lazy dog is an English-language pangram—a sentence that contains all of the letters of the English alphabet."
            if (!model.complain.isNullOrEmpty()) {
                if (!model.isExpand && model.complain!!.length > charLimit) {
                    val subString = model.complain!!.substring(0, charLimit-1) + "...<font color='#00844A'>বিস্তারিত</font>"
                    binding.complainType.text = HtmlCompat.fromHtml(subString, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    binding.complainType.setOnClickListener {
                        if (!model.isExpand) {
                            model.isExpand = true
                            notifyItemChanged(position)
                        }
                    }
                } else {
                    binding.complainType.text = model.complain
                }
            } else {
                binding.complainType.text = ""
            }

            binding.orderCode.text = "DT-${model.orderId}"
            if (model.complaintDate != null) {
                val formattedDate = DigitConverter.toBanglaDate(model.complaintDate!!,"yyyy-MM-dd", false)
                binding.date.text = formattedDate
            } else {
                binding.date.text = ""
            }

            binding.status.text = "বর্তমান স্ট্যাটাস: ${model.complainType}"
            if (model.solvedDate != null && model.solvedDate != "0001-01-01T00:00:00Z") {
                val formattedDate = DigitConverter.toBanglaDate(model.solvedDate!!,"yyyy-MM-dd", true)
                binding.status.append(" ($formattedDate)")
            }
        }
    }

    inner class ViewModel(val binding: ItemViewComplainBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition in 0..dataList.lastIndex)
                    onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
    }

    fun initLoad(list: List<ComplainData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<ComplainData>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }
}