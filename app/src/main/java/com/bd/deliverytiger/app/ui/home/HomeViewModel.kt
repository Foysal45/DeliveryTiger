package com.bd.deliverytiger.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.config.BannerConfig
import com.bd.deliverytiger.app.repository.AppRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val repository: AppRepository): ViewModel() {


    fun getBannerInfo(): LiveData<BannerConfig> {

        val responseBody = MutableLiveData<BannerConfig>()
        repository.getBannerInfo().enqueue(object : Callback<GenericResponse<BannerConfig>> {
            override fun onFailure(call: Call<GenericResponse<BannerConfig>>, t: Throwable) {
            }

            override fun onResponse(call: Call<GenericResponse<BannerConfig>>, response: Response<GenericResponse<BannerConfig>>) {
                if (response.isSuccessful && response.body() != null) {
                    responseBody.value = response.body()!!.model
                }
            }
        })
        return responseBody
    }

}