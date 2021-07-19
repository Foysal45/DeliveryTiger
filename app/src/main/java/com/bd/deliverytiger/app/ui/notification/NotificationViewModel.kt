package com.bd.deliverytiger.app.ui.notification

import androidx.lifecycle.*
import com.bd.deliverytiger.app.fcm.FCMData
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    val notificationList = repository.getAllNotificationFlow().asLiveData()

    fun getAllNotification(): LiveData<List<FCMData>> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<List<FCMData>>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getAllNotification()
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = response
            }
        }
        return responseBody
    }

    fun deleteNotificationById(id: Int): LiveData<Boolean> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.deleteNotificationById(id)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = response > 0
            }
        }
        return responseBody
    }

    fun deleteAllNotification(): LiveData<Boolean> {

        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.deleteAllNotification()
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                responseBody.value = response > 0
            }
        }
        return responseBody
    }
}