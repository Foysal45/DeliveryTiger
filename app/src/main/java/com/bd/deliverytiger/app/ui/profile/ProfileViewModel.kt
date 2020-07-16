package com.bd.deliverytiger.app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    private val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    fun getAllDistrictFromApi(districtId: Int): LiveData<List<DistrictDeliveryChargePayLoad>> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<DistrictDeliveryChargePayLoad>>()
        repository.getAllDistrictFromApi(districtId).enqueue(object : Callback<DeliveryChargePayLoad> {
            override fun onFailure(call: Call<DeliveryChargePayLoad>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }
            override fun onResponse(call: Call<DeliveryChargePayLoad>, response: Response<DeliveryChargePayLoad>) {
                viewState.value = ViewState.ProgressState(false)
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.data != null) {
                        if (!response.body()!!.data!!.districtInfo.isNullOrEmpty()) {
                            responseBody.value = response.body()!!.data!!.districtInfo
                        } else {
                            viewState.value = ViewState.ShowMessage(message)
                        }
                    } else {
                        viewState.value = ViewState.ShowMessage(message)
                    }
                } else {
                    viewState.value = ViewState.ShowMessage(message)
                }
            }
        })
        return responseBody
    }

    fun updateMerchantInformation(courierUserId: Int, requestBody: ProfileUpdateReqBody) {
        viewState.value = ViewState.ProgressState(true)
        repository.updateMerchantInformation(courierUserId, requestBody).enqueue(object : Callback<GenericResponse<LoginResponse>> {
            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<LoginResponse>>, response: Response<GenericResponse<LoginResponse>>) {
                if (response.isSuccessful && response.body() != null && response.body()?.model != null) {
                    SessionManager.updateSession(requestBody)
                    viewState.value = ViewState.ShowMessage("সফলভাবে আপডেট হয়েছে")
                    viewState.value = ViewState.ProgressState(false)
                } else {
                    viewState.value = ViewState.ProgressState(false)
                    viewState.value = ViewState.ShowMessage(message)
                }
            }
        })
    }
}