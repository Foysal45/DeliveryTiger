package com.bd.deliverytiger.app.ui.live.stream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_channel.ChannelActionRequest
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_channel.ChannelActionResponse
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_channel_medialive.ChannelUpdateRequest
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_channel_medialive.ChannelUpdateResponse
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_product_list.LiveProductData
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_product_list.LiveProductRequest
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_started_notify.LiveStartedNotifyRequest
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_started_notify.LiveStartedNotifyResponse
import com.ajkerdeal.app.ajkerdealseller.api.model.live.live_status.LiveStatusUpdateRequest
import com.ajkerdeal.app.ajkerdealseller.repository.AppRepository
import com.ajkerdeal.app.ajkerdealseller.utilities.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LiveActivityViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    fun updateLiveStatus(requestBody: LiveStatusUpdateRequest): LiveData<Boolean> {

        val responseData: MutableLiveData<Boolean> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.updateLiveStatus(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.data != null) {
                            responseData.value = response.body.data!! == 1
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
        return responseData
    }

    fun channelAction(requestBody: ChannelActionRequest): LiveData<ChannelActionResponse> {

        val responseData: MutableLiveData<ChannelActionResponse> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true, 1)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.channelAction(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false, 1)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body != null) {
                            responseData.value = response.body
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
        return responseData
    }

    fun channelUpdateAction(requestBody: ChannelUpdateRequest): LiveData<ChannelUpdateResponse> {

        val responseData: MutableLiveData<ChannelUpdateResponse> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true, 1)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.channelUpdate(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false, 1)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body != null) {
                            responseData.value = response.body
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        responseData.value = ChannelUpdateResponse(false, "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন")
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

    fun fetchLiveProducts(liveId: Int): LiveData<List<LiveProductData>> {

        val catalogList: MutableLiveData<List<LiveProductData>> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchLiveProducts(LiveProductRequest(liveId))
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val dataList = response.body.data
                        if (!dataList.isNullOrEmpty()) {
                            catalogList.value = dataList!!
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
        return catalogList
    }

    fun liveStartedNotify(requestBody: LiveStartedNotifyRequest){

        val responseData: MutableLiveData<LiveStartedNotifyResponse> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true, 1)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.liveStartedNotify(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false, 1)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body != null) {
                            responseData.value = response.body
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
    }

}