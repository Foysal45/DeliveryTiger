package com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.district.AllDistrictListsModel
import com.bd.deliverytiger.app.api.model.service_selection.GetServiceDistrictsRequest
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoDataModel
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

    fun loadAllDistricts(requestBody: GetServiceDistrictsRequest): LiveData<List<AllDistrictListsModel>> {
        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<List<AllDistrictListsModel>>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.loadAllServiceDistricts(requestBody)
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

    fun fetchServiceInfo(): LiveData<List<ServiceInfoDataModel>> {
        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<List<ServiceInfoDataModel>>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getDTService()
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