package com.bd.deliverytiger.app.ui.loan_survey

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.complain.ComplainRequest
import com.bd.deliverytiger.app.api.model.loan_survey.LoanSurveyRequestBody
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File

class LoanSurveryViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"

    private val mediaTypeMultipart = "multipart/form-data".toMediaTypeOrNull()
    private val mediaTypeText = "text/plain".toMediaTypeOrNull()


    fun imageUploadForFile(context: Context, fileName: String, imagePath: String, fileUrl: String): LiveData<Boolean> {

        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {

            val fileNameR = fileName.toRequestBody(mediaTypeText)
            val imagePathR = imagePath.toRequestBody(mediaTypeText)
            //val compressedFile = File(fileUrl)
            val compressedFile = Compressor.compress(context, File(fileUrl)) {
                resolution(1280, 720)
                size(512_152)
            }
            val requestFile = compressedFile.asRequestBody(mediaTypeMultipart)
            val part = MultipartBody.Part.createFormData("img", fileName, requestFile)

            val response = repository.imageUploadForFile(fileNameR, imagePathR, part)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        responseData.value = response.body
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "Server error"
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

    fun submitLoanSurvey(requestBody: LoanSurveyRequestBody): LiveData<Int> {

        val responseData: MutableLiveData<Int> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.submitLoanSurvey(requestBody)
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

}