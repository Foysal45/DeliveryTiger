package com.bd.deliverytiger.app.ui.payment_statement.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsRequest
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsResponse
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.exhaustive
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PaymentStatementDetailViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    fun getPaymentHistoryDetails(courierUserId: Int, transactionId: String): LiveData<PaymentDetailsResponse> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<PaymentDetailsResponse>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getPaymentHistoryDetails(PaymentDetailsRequest(courierUserId, transactionId))
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.isNotEmpty()) {
                            responseBody.value = response.body[0]
                        } else {
                            responseBody.value = PaymentDetailsResponse()
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

}