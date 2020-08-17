package com.bd.deliverytiger.app.ui.add_order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddOrderViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    private val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    fun getMerchantCredit(): LiveData<Boolean> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>()
        repository.getMerchantCredit(SessionManager.courierUserId).enqueue(object : Callback<GenericResponse<Boolean>> {
            override fun onFailure(call: Call<GenericResponse<Boolean>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<Boolean>>, response: Response<GenericResponse<Boolean>>) {
                viewState.value = ViewState.ProgressState(false)
                if (response.isSuccessful && response.body() != null) {
                    responseBody.value = response.body()!!.model
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
        repository.getAllDistrictFromApi(districtId).enqueue(object : Callback<DeliveryChargePayLoad>{
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

    fun getBreakableCharge(): LiveData<BreakableChargeData> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<BreakableChargeData>()

        repository.getBreakableCharge().enqueue(object : Callback<GenericResponse<BreakableChargeData>> {
            override fun onFailure(call: Call<GenericResponse<BreakableChargeData>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<BreakableChargeData>>, response: Response<GenericResponse<BreakableChargeData>>) {
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

    fun getPackagingCharge(): LiveData<List<PackagingData>> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<PackagingData>>()

        repository.getPackagingCharge().enqueue(object : Callback<GenericResponse<List<PackagingData>>> {
            override fun onFailure(call: Call<GenericResponse<List<PackagingData>>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<List<PackagingData>>>, response: Response<GenericResponse<List<PackagingData>>>) {
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

    fun getDeliveryCharge(requestBody: DeliveryChargeRequest): LiveData<List<DeliveryChargeResponse>> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<DeliveryChargeResponse>>()

        repository.getDeliveryCharge(requestBody).enqueue(object : Callback<GenericResponse<List<DeliveryChargeResponse>>> {
            override fun onFailure(call: Call<GenericResponse<List<DeliveryChargeResponse>>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<List<DeliveryChargeResponse>>>, response: Response<GenericResponse<List<DeliveryChargeResponse>>>) {
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

    fun placeOrder(requestBody: OrderRequest): LiveData<OrderResponse> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<OrderResponse>()

        repository.placeOrder(requestBody).enqueue(object : Callback<GenericResponse<OrderResponse>> {
            override fun onFailure(call: Call<GenericResponse<OrderResponse>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<OrderResponse>>, response: Response<GenericResponse<OrderResponse>>) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null){
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

    fun getCollectionCharge(courierUserId: Int): LiveData<Int> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Int>()

        repository.getCollectionCharge(courierUserId).enqueue(object : Callback<GenericResponse<Int>> {
            override fun onFailure(call: Call<GenericResponse<Int>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<Int>>, response: Response<GenericResponse<Int>>) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null){
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