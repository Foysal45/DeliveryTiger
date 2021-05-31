package com.bd.deliverytiger.app.ui.live.deal_management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.PagingModel
import com.bd.deliverytiger.app.api.model.deal_management.DealManagementData
import com.bd.deliverytiger.app.api.model.deal_management.DealManagementRequest
import com.bd.deliverytiger.app.api.model.live.live_deal_management.InsertDealManagementRequestBody
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.exhaustive
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class LiveDealManagementViewModel (private val repository: AppRepository): ViewModel(){

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val pagingState: MutableLiveData<PagingModel<List<DealManagementData>>> = MutableLiveData()

    fun fetchDealManagementData(requestBody: DealManagementRequest) {

        viewState.value = ViewState.ProgressState(true)

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchDealManagementData(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val model = response.body
                        if (requestBody.pagingLowerVal == 0) {
                            pagingState.value = PagingModel(true, model.totalCount, 0, 0.0, 0.0, model.data)
                        } else {
                            pagingState.value = PagingModel(false, 0,0, 0.0, 0.0,  model.data)
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
                }.exhaustive
            }
        }

    }

    fun insertFromOrderManagement(requestBody: InsertDealManagementRequestBody): LiveData<Int> {
            viewState.value = ViewState.ProgressState(true)
            val responseBody = MutableLiveData<Int>()
            viewModelScope.launch(Dispatchers.IO) {
                val response = repository.insertFromOrderManagement(requestBody)
                withContext(Dispatchers.Main) {
                    viewState.value = ViewState.ProgressState(false)
                    when (response) {
                        is NetworkResponse.Success -> {
                            responseBody.value = response.body.data!!
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
                    }.exhaustive
                }
            }
            return responseBody
        }

}