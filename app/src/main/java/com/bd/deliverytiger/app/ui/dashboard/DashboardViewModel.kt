package com.bd.deliverytiger.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponse
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
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

}