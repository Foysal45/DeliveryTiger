package com.bd.deliverytiger.app.ui.lead_management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.PagingModel
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInfoRequest
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInformation
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.exhaustive
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LeadManagementViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val pagingState: MutableLiveData<PagingModel<List<CustomerInformation>>> = MutableLiveData()

    fun fetchCustomerList(requestBody: CustomerInfoRequest, index: Int) {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<CustomerInformation>>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchCustomerList(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (index == 0){
                            if (response.body.model.isNotEmpty()){
                                pagingState.value = PagingModel(true, response.body.model.count(), 0, 0.0, 0.0, response.body.model)
                            }else{
                                pagingState.value = PagingModel(true, 0, 0, 0.0, 0.0, response.body.model)

                            }
                        }else{
                            pagingState.value = PagingModel(false, 0, 0, 0.0, 0.0, response.body.model)
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

}