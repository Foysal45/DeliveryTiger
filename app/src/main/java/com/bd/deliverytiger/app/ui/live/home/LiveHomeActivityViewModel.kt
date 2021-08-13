package com.bd.deliverytiger.app.ui.live.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bd.deliverytiger.app.api.model.PagingModel
import com.bd.deliverytiger.app.api.model.live.products.ProductData
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState

class LiveHomeActivityViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    private var lastIndexCategory: Int = -1

    val createdProduct: MutableLiveData<ProductData?> = MutableLiveData()

    private var lastIndex1: Int = -1

    private var lastIndexBrand: Int = -1


    private val _pagingState: MutableLiveData<PagingModel<List<ProductData>>> = MutableLiveData()
    val pagingState: LiveData<PagingModel<List<ProductData>>>
        get() = _pagingState



}