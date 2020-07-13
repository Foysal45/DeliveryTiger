package com.bd.deliverytiger.app.ui.add_order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddOrderViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    fun getMerchantCredit(): LiveData<Boolean> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>()
        repository.getMerchantCredit(SessionManager.courierUserId).enqueue(object : Callback<GenericResponse<Boolean>> {
            override fun onFailure(call: Call<GenericResponse<Boolean>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
            }

            override fun onResponse(call: Call<GenericResponse<Boolean>>, response: Response<GenericResponse<Boolean>>) {
                viewState.value = ViewState.ProgressState(false)
                if (response.isSuccessful && response.body() != null) {
                    responseBody.value = response.body()!!.model
                }
            }
        })
        return responseBody
    }

}