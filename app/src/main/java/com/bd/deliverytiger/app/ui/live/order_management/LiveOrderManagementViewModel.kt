package com.bd.deliverytiger.app.ui.live.order_management

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.PagingModel
import com.bd.deliverytiger.app.api.model.live.order_management.OrderManagementPendingRequestBody
import com.bd.deliverytiger.app.api.model.live.order_management.OrderManagementPendingResponseBody
import com.bd.deliverytiger.app.api.model.live.order_management.OrderManagementResponseModel
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LiveOrderManagementViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    val pagingState: MutableLiveData<PagingModel<List<OrderManagementResponseModel>>> = MutableLiveData()
    val pendingPagingState: MutableLiveData<PagingModel<List<OrderManagementPendingResponseBody>>> = MutableLiveData()

    fun fetchOrderManagementPendingList(requestBody: OrderManagementPendingRequestBody) {

        viewState.value = ViewState.ProgressState(true)
        //val responseBody = MutableLiveData<List<OrderManagementResponseModel>>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchOrderManagementPendingList(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val productList = response.body.data
                        if (productList != null) {
                            if (requestBody.index == 0) {
                                Timber.d("PagingDebug pagingState set value with true $requestBody.index")
                                pendingPagingState.value = PagingModel(true, /*response.body.data*/0,0, 0.0, 0.0, productList)
                            } else {
                                Timber.d("PagingDebug pagingState set value with false $requestBody.index")
                                pendingPagingState.value = PagingModel(false, /*response.body.totalCount*/ 0, 0, 0.0, 0.0,productList)
                            }
                            Timber.d("TestingModel $productList")
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
        // return responseBody
    }

}