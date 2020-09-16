package com.bd.deliverytiger.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.ResponseHeader
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponse
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    private val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"


    fun getDashboardStatusGroup(requestBody: DashBoardReqBody): LiveData<DashboardResponse> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<DashboardResponse>()

        repository.getDashboardStatusGroup(requestBody).enqueue(object : Callback<GenericResponse<DashboardResponse>> {
            override fun onFailure(call: Call<GenericResponse<DashboardResponse>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<DashboardResponse>>, response: Response<GenericResponse<DashboardResponse>>) {
                viewState.value = ViewState.ProgressState(false)
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.model != null) {
                        responseBody.value = response.body()!!.model
                    } else {
                        viewState.value = ViewState.ProgressState(false)
                        viewState.value = ViewState.ShowMessage(message)
                    }
                } else {
                    viewState.value = ViewState.ProgressState(false)
                    viewState.value = ViewState.ShowMessage(message)
                }
            }
        })

        return responseBody
    }

    fun updateStatusLocation(courierUserId: Int): LiveData<Boolean> {

        val responseData: MutableLiveData<Boolean> = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO){

            repository.updateCourierStatus(StatusLocationRequest(courierUserId)).enqueue(object : Callback<ResponseHeader<Int>> {
                override fun onFailure(call: Call<ResponseHeader<Int>>, t: Throwable) {
                    viewState.value = ViewState.ShowMessage(message)
                }
                override fun onResponse(call: Call<ResponseHeader<Int>>, response: Response<ResponseHeader<Int>>) {
                    if (response.isSuccessful && response.body() != null) {
                        responseData.value = response.body()!!.data == 1
                    } else {
                        viewState.value = ViewState.ShowMessage(message)
                    }
                }
            })
        }
        return responseData
    }

    /*fun fetchVideoShoppingBanner(): LiveData<Boolean> {

        val responseData: MutableLiveData<Boolean> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.data != null) {
                            responseData.value = response.body.data!!
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
    }*/

}