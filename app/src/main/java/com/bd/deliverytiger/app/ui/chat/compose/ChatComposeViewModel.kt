package com.bd.deliverytiger.app.ui.chat.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.fcm.FCMRequest
import com.bd.deliverytiger.app.api.model.fcm.FCMResponse
import com.bd.deliverytiger.app.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatComposeViewModel(private val repository: AppRepository): ViewModel() {

    fun sendPushNotifications(authToken: String, requestBody: FCMRequest): LiveData<FCMResponse> {
        val responseData = MutableLiveData<FCMResponse>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.sendPushNotifications(authToken, requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        responseData.value = response.body()
                    }
                }
            }
        }
        return responseData
    }

}