package com.bd.deliverytiger.app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ProfileViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    private val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    fun getPickupLocations(courierUserId: Int): LiveData<List<PickupLocation>> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<PickupLocation>>()

        repository.getPickupLocations(courierUserId).enqueue(object : Callback<GenericResponse<List<PickupLocation>>> {
            override fun onFailure(call: Call<GenericResponse<List<PickupLocation>>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }
            override fun onResponse(call: Call<GenericResponse<List<PickupLocation>>>, response: Response<GenericResponse<List<PickupLocation>>>) {
                viewState.value = ViewState.ProgressState(false)
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.model != null) {
                        responseBody.value = response.body()!!.model
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

    fun updateMerchantInformation(courierUserId: Int, requestBody: ProfileUpdateReqBody): LiveData<LoginResponse> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<LoginResponse>()
        repository.updateMerchantInformation(courierUserId, requestBody).enqueue(object : Callback<GenericResponse<LoginResponse>> {
            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<LoginResponse>>, response: Response<GenericResponse<LoginResponse>>) {
                viewState.value = ViewState.ProgressState(false)
                if (response.isSuccessful && response.body() != null && response.body()?.model != null) {
                    responseBody.value = response.body()?.model
                    SessionManager.updateSession(requestBody)
                    viewState.value = ViewState.ShowMessage("সফলভাবে আপডেট হয়েছে")
                    //viewState.value = ViewState.NextState()
                } else {
                    viewState.value = ViewState.ShowMessage(message)
                }
            }
        })
        return responseBody
    }

    fun updatePickupLocations(requestBody: PickupLocation) {

        repository.updatePickupLocations(requestBody.id, requestBody).enqueue(object : Callback<GenericResponse<PickupLocation>> {
            override fun onFailure(call: Call<GenericResponse<PickupLocation>>, t: Throwable) {}

            override fun onResponse(call: Call<GenericResponse<PickupLocation>>, response: Response<GenericResponse<PickupLocation>>) {
                if (response.isSuccessful && response.body() != null && response.body()?.model != null) {

                }
            }
        })
    }

    fun addPickupLocations(requestBody: PickupLocation): LiveData<PickupLocation> {

        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<PickupLocation>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.addPickupLocations(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.model != null) {
                            responseData.value = response.body.model
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


}