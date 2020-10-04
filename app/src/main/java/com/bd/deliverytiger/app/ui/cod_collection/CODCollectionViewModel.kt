package com.bd.deliverytiger.app.ui.cod_collection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.PagingModel
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.exhaustive
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class CODCollectionViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val pagingState = MutableLiveData<PagingModel<List<CourierOrderViewModel>>>()

    fun fetchCODCollectionDetails(requestBody: CODReqBody, index: Int = 0) {

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchCODCollectionDetails(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val model = response.body.model
                        if (index == 0) {
                            pagingState.value = PagingModel(
                                true,
                                model.totalCount.toInt(),
                                model.adTotalCollectionAmount.toInt(),
                                model.adTotalCollectionAmount,
                                model.adTotalCollectionAmount,
                                model.courierOrderViewModel
                            )
                        } else {
                            pagingState.value = PagingModel(
                                false,
                                model.totalCount.toInt(),
                                model.adTotalCollectionAmount.toInt(),
                                model.adTotalCollectionAmount,
                                model.adTotalCollectionAmount,
                                model.courierOrderViewModel
                            )
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
    }

}