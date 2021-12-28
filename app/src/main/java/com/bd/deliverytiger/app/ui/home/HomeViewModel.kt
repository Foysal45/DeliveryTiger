package com.bd.deliverytiger.app.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.accounts.AdvanceBalanceData
import com.bd.deliverytiger.app.api.model.config.BannerResponse
import com.bd.deliverytiger.app.api.model.courier_info.CourierInfoModel
import com.bd.deliverytiger.app.api.model.district.DistrictData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.service_selection.ServiceDistrictsRequest
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoData
import com.bd.deliverytiger.app.fcm.FCMData
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(private val repository: AppRepository): ViewModel() {

    val serverErrorMessage = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
    val networkErrorMessage = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
    val unknownErrorMessage = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    private val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US)

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val bannerInfo = MutableLiveData<BannerResponse>()

    val currentLocation = MutableLiveData<Location?>(null)

    val refreshEvent = MutableLiveData<String>("")

    val keyboardVisibility = MutableLiveData<Boolean>(false)

    val serviceInfoList = MutableLiveData<List<ServiceInfoData>>()

    val pickupLocationList = MutableLiveData<List<PickupLocation>>()

    //custom cod charge
    var codChargeTypeFlag: Int = -1
    var codChargeTypeOutsideFlag: Int = -1
    var codChargeDhaka: Double = -1.0
    var codChargeOutsideDhaka: Double = -1.0
    var codChargePercentageDhaka: Double = -1.0
    var codChargePercentageOutsideDhaka: Double = -1.0

    val netAmount = MutableLiveData<Int>(0)

    fun getBannerInfo(): LiveData<BannerResponse> {

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getBannerInfo()
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.model != null) {
                            bannerInfo.value = response.body.model!!
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
        return bannerInfo
    }

    fun fetchMerchantCurrentAdvanceBalance(courierUserId: Int): LiveData<AdvanceBalanceData> {

        viewState.value = ViewState.ProgressState(true)
        val responseData: MutableLiveData<AdvanceBalanceData> = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchMerchantCurrentAdvanceBalance(courierUserId)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
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

    fun fetchOrderServiceInfo() {
        //viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getDTService()
            if (response is NetworkResponse.Success) {
                val serviceList = response.body.model
                serviceList.forEachIndexed { index, model ->
                    if (model.deliveryRangeId.isNotEmpty()){
                        if (model.districtList.isEmpty()){
                            val districtRequest = ServiceDistrictsRequest(model.deliveryRangeId)
                            val serviceDistrictResponse = repository.fetchServiceDistricts(districtRequest)
                            if (serviceDistrictResponse is NetworkResponse.Success) {
                                val locationList = serviceDistrictResponse.body.model
                                if (!locationList.isNullOrEmpty()) {
                                    val districtList = locationList.filter { it.parentId == 0 }
                                    model.apply {
                                        this.districtList = districtList
                                        this.index = index
                                    }
                                    /*withContext(Dispatchers.Main) {
                                        serviceInfoList.value = model
                                    }*/
                                }
                            }
                        }
                    } else {
                        if (model.districtList.isEmpty()) {
                            val locationList = repository.getDistrictByParentId(0)
                            if (!locationList.isNullOrEmpty()) {
                                model.apply {
                                    this.districtList = locationList
                                    this.index = index
                                }
                            }
                            /*val allDistrictResponse = repository.loadAllDistrictsById(0)
                            if (allDistrictResponse is NetworkResponse.Success) {
                                val locationList = allDistrictResponse.body.model
                                if (!locationList.isNullOrEmpty()) {
                                    model.apply {
                                        this.districtList = locationList
                                        this.index = index
                                    }
                                    *//*withContext(Dispatchers.Main) {
                                        serviceInfoList.value = model
                                    }*//*
                                }
                            }*/
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    serviceInfoList.value = serviceList
                }
            }
        }
    }

    fun loadAllDistrictsById(parentId: Int): LiveData<List<DistrictData>> {
        val responseData: MutableLiveData<List<DistrictData>> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO){
            val dataList = repository.getDistrictByParentId(parentId)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseData.value = dataList
            }
        }
        return responseData
    }

    fun saveNotificationData(fcmModel: FCMData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(fcmModel.apply {
                createdAt = sdf.format(Date().time)
            })
        }
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

    fun getPickupLocations(courierUserId: Int): LiveData<List<PickupLocation>> {

        viewState.value = ViewState.ProgressState(true)
        //val responseData = MutableLiveData<List<PickupLocation>>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getPickupLocations(courierUserId)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.model != null) {
                            pickupLocationList.value = response.body.model!!
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
        return pickupLocationList
    }

}