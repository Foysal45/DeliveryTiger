package com.bd.deliverytiger.app.ui.order_tracking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.databinding.ItemViewOrderTrackBinding
import com.bd.deliverytiger.app.utils.DigitConverter

class OrderTrackingNewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<OrderTrackMainResponse> = mutableListOf()
    var onItemClick: ((model:OrderTrackMainResponse, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewOrderTrackBinding = ItemViewOrderTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            val formattedDate = DigitConverter.toBanglaDate(model.dateTime,"yyyy-MM-dd'T'HH:mm:ss", true)
            binding.date.text = formattedDate
            binding.time.text = DigitConverter.toBanglaDigit("05:28 PM") //"05:28 PM"

            binding.statusName.text = model.orderTrackStatusGroup

            val greenTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.ic_done_green)
            val redTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.ic_done_red)
            val gryTic = ContextCompat.getDrawable(binding.statusImage.context, R.drawable.bg_circle_gray)

            binding.statusImage.setImageDrawable(greenTic)

            if (position == dataList.lastIndex) {
                binding.verticalView.visibility = View.GONE
            } else {
                binding.verticalView.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewOrderTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<OrderTrackMainResponse>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

}