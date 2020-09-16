package com.bd.deliverytiger.app.ui.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bd.deliverytiger.app.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter: SliderViewAdapter<SliderAdapter.ViewModel>() {

    private val dataList: MutableList<String> = mutableListOf()
    private val options = RequestOptions().placeholder(R.drawable.ic_banner_place).error(R.drawable.ic_banner_place)

    override fun getCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapter.ViewModel {
        //val binding = ItemViewBannerSliderBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_view_banner_slider, parent, false)
        return ViewModel(view)
    }

    override fun onBindViewHolder(holder: SliderAdapter.ViewModel, position: Int) {
        val model = dataList[position]

        Glide.with(holder.bannerIV)
            .load(model)
            .apply(options)
            .into(holder.bannerIV)

    }

    inner class ViewModel(val view: View): SliderViewAdapter.ViewHolder(view) {
         val bannerIV: ImageView = view.findViewById(R.id.banner)
        init {

        }
    }

    fun initList(list: List<String>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}