package com.bd.deliverytiger.app.ui.add_order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.product_upload.ProductUploadResponse
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber

class AddProductViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    fun uploadProductInfo(productDataRequestBody: RequestBody): LiveData<ProductUploadResponse> {

        val responseData: MutableLiveData<ProductUploadResponse> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.uploadProductInfo(productDataRequestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.data != null) {
                            responseData.value = response.body.data
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

    suspend fun uploadProductImage(location: String, file: MultipartBody.Part): LiveData<ProductUploadResponse> {
        val responseData: MutableLiveData<ProductUploadResponse> = MutableLiveData()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.uploadProductImage(location, file)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.data != null) {
                        responseData.value = response.body.data
                    }
                }
                is NetworkResponse.ServerError -> {
                    val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                    viewState.postValue(ViewState.ShowMessage(message))
                }
                is NetworkResponse.NetworkError -> {
                    val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                    //viewState.value = ViewState.ShowMessage(message)
                    viewState.postValue(ViewState.ShowMessage(message))
                }
                is NetworkResponse.UnknownError -> {
                    val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                    viewState.postValue(ViewState.ShowMessage(message))
                    //viewState.value = ViewState.ShowMessage(message)
                    //Timber.d(response.error)
                }
            }
        }
        return responseData
    }

}