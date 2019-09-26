package com.bd.deliverytiger.app.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bd.deliverytiger.app.R

class CustomSpinnerAdapter(context: Context, private val resource: Int, private val dataList: MutableList<String>) :
    ArrayAdapter<String>(context, resource, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return dataList.size
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View{

        val view = LayoutInflater.from(parent.context).inflate(resource, parent, false)
        val textView: TextView = view.findViewById(R.id.item_spinner_item_tv)
        textView.text = dataList[position]

        return view
    }


}