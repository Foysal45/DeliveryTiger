package com.bd.deliverytiger.app.ui.add_order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.accounts.AdvanceBalanceData
import com.bd.deliverytiger.app.api.model.accounts.BalanceInfo
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.courier_info.CourierInfoModel
import com.bd.deliverytiger.app.api.model.district.AllDistrictListsModel
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.generic_limit.GenericLimitData
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.referral.OfferData
import com.bd.deliverytiger.app.api.model.time_slot.TimeSlotData
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.exhaustive
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class AddOrderViewModel(private val repository: AppRepository) : ViewModel() {

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
                    responseBody.value = response.body()!!.model!!
                } else {
                    viewState.value = ViewState.ShowMessage(message)
                }
            }
        })
        return responseBody
    }

    fun fetchMerchantCurrentAdvanceBalance(courierUserId: Int): LiveData<AdvanceBalanceData> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<AdvanceBalanceData>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchMerchantCurrentAdvanceBalance(courierUserId)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body != null) {
                            responseBody.value = response.body
                        } else {

                        }
                    }
                    is NetworkResponse.ServerError -> {
                        //val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        //viewState.value = ViewState.ShowMessage(message)
                        responseBody.value = AdvanceBalanceData()
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
        return responseBody
    }

    fun fetchMerchantBalanceInfo(courierUserId: Int, amount: Int): LiveData<BalanceInfo> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<BalanceInfo>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchMerchantBalanceInfo(courierUserId, amount)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.model != null) {
                            responseBody.value = response.body.model!!
                        } else {

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
        return responseBody
    }

    fun loadAllDistricts(): LiveData<List<AllDistrictListsModel>> {
        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<List<AllDistrictListsModel>>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.loadAllDistricts()
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        responseData.value = response.body.model!!
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
                if (response.isSuccessful && response.body() != null) {
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
        val responseData = MutableLiveData<List<PickupLocation>>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getPickupLocations(courierUserId)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.model != null) {
                            responseData.value = response.body.model!!
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
                        responseBody.value = response.body()!!.model!!
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
                        responseBody.value = response.body()!!.model!!
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
                viewState.value = ViewState.ProgressState(false)
                if (response.isSuccessful && response.body() != null) {
                    responseBody.value = response.body()!!.model!!
                } else {
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
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.model != null) {
                        responseBody.value = response.body()!!.model!!
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


    /*fun fetchCollectionTimeSlot(): LiveData<Int> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Int>()

        repository.fetchCollectionTimeSlot().enqueue(object : Callback<GenericResponse<List<TimeSlotData>> {
            override fun onFailure(call: Call<GenericResponse<List<TimeSlotData>>, t: Throwable) {
                viewState.value = ViewState.ProgressState(false)
                viewState.value = ViewState.ShowMessage(message)
            }

            override fun onResponse(call: Call<GenericResponse<List<TimeSlotData>>, response: Response<GenericResponse<List<TimeSlotData>>) {
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
    }*/

    fun fetchCollectionTimeSlot(): LiveData<List<TimeSlotData>> {

        val responseData: MutableLiveData<List<TimeSlotData>> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchCollectionTimeSlot()
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

    fun fetchDTOrderGenericLimit(): LiveData<GenericLimitData> {

        val responseData: MutableLiveData<GenericLimitData> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchDTOrderGenericLimit()
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

    fun fetchOfferCharge(courierUserId: Int): LiveData<OfferData> {

        val responseData: MutableLiveData<OfferData> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchOfferCharge(courierUserId)
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

    fun getCourierUsersInformation(courierUserId: Int): LiveData<CourierInfoModel> {

        val responseData: MutableLiveData<CourierInfoModel> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getCourierUsersInformation(courierUserId)
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