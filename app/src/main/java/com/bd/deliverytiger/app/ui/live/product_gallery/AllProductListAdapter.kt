package com.bd.deliverytiger.app.ui.live.product_gallery

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.my_products_lists.MyProductsResponse
import com.bd.deliverytiger.app.databinding.ItemViewProductsListBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class AllProductListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<MyProductsResponse> = mutableListOf()
    var onItemClicked: ((model: MyProductsResponse, position: Int) -> Unit)? = null

    private val selectedItems: SparseBooleanArray = SparseBooleanArray()
    // array used to perform multiple animation at once
    private val animationItemsIndex: SparseBooleanArray = SparseBooleanArray()
    private var currentSelectedIndex = -1
    private var reverseAllAnimations = false
    var enableSelection: Boolean = false

    private val options = RequestOptions().placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewProductsListBinding = ItemViewProductsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            Glide.with(binding.productImage).load(model.coverPhoto).apply(options).into(binding.productImage)
            binding.title.text = "${DigitConverter.toBanglaDigit(model.ProductPrice)} ৳"

            binding.soldOutIcon.isVisible = model.isSoldOut
            if (selectedItems[position, false]) {
                binding.parent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.parent.context, R.color.product_selected_color))
                binding.selectedIcon.isVisible = true
            } else {
                binding.parent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.parent.context, R.color.white))
                binding.selectedIcon.isVisible = false
            }

        }
    }

    inner class ViewModel(val binding: ItemViewProductsListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked?.invoke(dataList[absoluteAdapterPosition], absoluteAdapterPosition)
                }
            }
        }
    }

    fun initLoad(list: List<MyProductsResponse>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun pagingLoad(list: List<MyProductsResponse>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun allList(): List<MyProductsResponse> = dataList

    fun toggleSelection(model : MyProductsResponse, pos: Int) {
        this.currentSelectedIndex = pos
        if (selectedItems[pos, false]) {
            selectedItems.delete(pos)
            //animationItemsIndex.delete(pos);
        } else {
            if (!model.isSoldOut) selectedItems.put(pos, true)
            //animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos)
    }

    fun allSelection() {
        val selectionLimit = 40
        reverseAllAnimations = true
        selectedItems.clear()
        if (dataList.size >= selectionLimit) {
            for (i in 0 until selectionLimit) {
                currentSelectedIndex = i
                selectedItems.put(i, true)
                animationItemsIndex.put(i, true)
            }
        } else {
            for (i in dataList.indices) {
                currentSelectedIndex = i
                selectedItems.put(i, true)
                animationItemsIndex.put(i, true)
            }
        }
        notifyDataSetChanged()
    }

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    fun getSeletedItemModelList(): List<MyProductsResponse> {
        val items: MutableList<MyProductsResponse> = ArrayList<MyProductsResponse>(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(dataList.get(selectedItems.keyAt(i)))
        }
        return items
    }

    fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }



}