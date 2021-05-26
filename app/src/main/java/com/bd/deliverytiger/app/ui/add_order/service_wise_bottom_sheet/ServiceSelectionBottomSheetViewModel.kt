package com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.district.AllDistrictListsModel
import com.bd.deliverytiger.app.api.model.service_selection.ServiceDistrictsRequest
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoData
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ServiceSelectionBottomSheetViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    val serverErrorMessage = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
    val networkErrorMessage = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
    val unknownErrorMessage = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    fun fetchServiceWiseDistrict(serviceList: List<ServiceInfoData>): LiveData<ServiceInfoData> {
        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<ServiceInfoData>()

        viewModelScope.launch(Dispatchers.IO) {

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
                                    this.locationList = locationList
                                    this.districtList = districtList
                                    this.index = index
                                }
                                withContext(Dispatchers.Main) {
                                    responseData.value = model
                                }
                            }
                        }
                    }
                } else {
                    if (model.districtList.isEmpty()) {
                        val allDistrictResponse = repository.loadAllDistricts()
                        if (allDistrictResponse is NetworkResponse.Success) {
                            val locationList = allDistrictResponse.body.model
                            if (!locationList.isNullOrEmpty()) {
                                val districtList = locationList.filter { it.parentId == 0 }
                                model.apply {
                                    this.locationList = locationList
                                    this.districtList = districtList
                                    this.index = index
                                }
                                withContext(Dispatchers.Main) {
                                    responseData.value = model
                                }
                            }
                        }
                    }
                }
            }
        }
        return responseData
    }

    fun fetchServiceDistricts(requestBody: ServiceDistrictsRequest): LiveData<List<AllDistrictListsModel>> {
        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<List<AllDistrictListsModel>>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchServiceDistricts(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        responseData.value = response.body.model!!
                    }
                    is NetworkResponse.ServerError -> {
                        viewState.value = ViewState.ShowMessage(serverErrorMessage)
                    }
                    is NetworkResponse.NetworkError -> {
                        viewState.value = ViewState.ShowMessage(networkErrorMessage)
                    }
                    is NetworkResponse.UnknownError -> {
                        viewState.value = ViewState.ShowMessage(unknownErrorMessage)
                        Timber.d(response.error)
                    }
                }
            }
        }
        return responseData
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
                        viewState.value = ViewState.ShowMessage(serverErrorMessage)
                    }
                    is NetworkResponse.NetworkError -> {
                        viewState.value = ViewState.ShowMessage(networkErrorMessage)
                    }
                    is NetworkResponse.UnknownError -> {
                        viewState.value = ViewState.ShowMessage(unknownErrorMessage)
                        Timber.d(response.error)
                    }
                }
            }
        }
        return responseData
    }

}