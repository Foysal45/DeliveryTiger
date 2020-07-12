package com.bd.deliverytiger.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponseModel
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    fun getDashboardStatusGroup(requestBody: DashBoardReqBody): LiveData<List<DashboardResponseModel>> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<DashboardResponseModel>>()

        repository.getDashboardStatusGroup(requestBody).enqueue(object : Callback<GenericResponse<List<DashboardResponseModel>>> {
            override fun onFailure(call: Call<GenericResponse<List<DashboardResponseModel>>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
            }

            override fun onResponse(call: Call<GenericResponse<List<DashboardResponseModel>>>, response: Response<GenericResponse<List<DashboardResponseModel>>>) {
                viewState.value = ViewState.ProgressState(false)
                if (response.isSuccessful && response.body() != null) {
                    if (!response.body()!!.model.isNullOrEmpty()) {
                        responseBody.value = response.body()!!.model
                    }
                }
            }
        })

        return responseBody
    }

}