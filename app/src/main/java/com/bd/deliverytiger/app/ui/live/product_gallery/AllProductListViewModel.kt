package com.bd.deliverytiger.app.ui.live.product_gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.PagingModel
import com.bd.deliverytiger.app.api.model.live.live_product_insert.ProductGalleryData
import com.bd.deliverytiger.app.api.model.live.my_products_lists.MyProductsRequest
import com.bd.deliverytiger.app.api.model.live.my_products_lists.MyProductsResponse
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class AllProductListViewModel (private val repository: AppRepository): ViewModel(){

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val pagingState: MutableLiveData<PagingModel<List<MyProductsResponse>>> = MutableLiveData()

    fun fetchLiveProducts(requestBody: MyProductsRequest): LiveData<List<MyProductsResponse>> {

        val myProducts: MutableLiveData<List<MyProductsResponse>> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchProducts((requestBody))
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val productList = response.body.data
                        if (productList != null) {
                            if (requestBody.index == 0) {
                                Timber.d("PagingDebug pagingState set value with true ${requestBody.index}")
                                pagingState.value = PagingModel(true, 0, 0, 0.0, 0.0,productList)
                            } else {
                                Timber.d("PagingDebug pagingState set value with false ${requestBody.index}")
                                pagingState.value = PagingModel(false, 0, 0, 0.0, 0.0, productList)
                            }
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }
        return myProducts
    }

    fun insertProduct(requestBody: List<ProductGalleryData>): LiveData<Boolean> {

        val responseData: MutableLiveData<Boolean> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.insertProductByID(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val data = response.body.data ?: false
                        responseData.value = data
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }
        return responseData
    }
}