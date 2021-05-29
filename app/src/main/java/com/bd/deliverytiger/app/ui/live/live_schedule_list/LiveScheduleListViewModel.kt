package com.bd.deliverytiger.app.ui.live.live_schedule_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.PagingModel
import com.bd.deliverytiger.app.api.model.live.live_schedule_list.MyLiveSchedule
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LiveScheduleListViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    //val myLiveSchedule: MutableLiveData<List<MyLiveSchedule>> = MutableLiveData()
    val pagingState = MutableLiveData<PagingModel<List<MyLiveSchedule>>>()

    fun fetchUserSchedule(customerId: Int, type: String, index: Int, count: Int): LiveData<List<MyLiveSchedule>> {

        val responseData: MutableLiveData<List<MyLiveSchedule>> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchUserSchedule(customerId, type, index, count)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val list = response.body.data
                        if (list != null) {
                            responseData.value = list!!
                            if (index == 0) {
                                Timber.d("PagingDebug pagingState set value with true $index")
                                pagingState.value = PagingModel(true, 0,0,0.0,0.0, list!!)
                            } else {
                                Timber.d("PagingDebug pagingState set value with false $index")
                                pagingState.value = PagingModel(false, 0, 0,0.0,0.0,list!!)
                            }
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        //val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        //viewState.value = ViewState.ShowMessage(message)
                        responseData.value = listOf()
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

    fun fetchUserScheduleReplay(customerId: Int, type: String, index: Int, count: Int): LiveData<List<MyLiveSchedule>> {

        val responseData: MutableLiveData<List<MyLiveSchedule>> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchUserScheduleReplay(customerId, type, index, count)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val list = response.body.data
                        if (list != null) {
                            responseData.value = list!!
                            if (index == 0) {
                                Timber.d("PagingDebug pagingState set value with true $index")
                                pagingState.value = PagingModel(true, 0,0,0.0,0.0, list!!)
                            } else {
                                Timber.d("PagingDebug pagingState set value with false $index")
                                pagingState.value = PagingModel(false, 0,0,0.0,0.0, list!!)
                            }
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        //val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        //viewState.value = ViewState.ShowMessage(message)
                        responseData.value = listOf()
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