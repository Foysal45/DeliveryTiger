package com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.district.DistrictData
import com.bd.deliverytiger.app.api.model.service_selection.ServiceDistrictsRequest
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoData
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceSelectionBottomSheetViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    val serverErrorMessage = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
    val networkErrorMessage = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
    val unknownErrorMessage = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

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
}