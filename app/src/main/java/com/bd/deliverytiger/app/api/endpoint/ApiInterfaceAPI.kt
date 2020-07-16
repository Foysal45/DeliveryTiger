package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ResponseHeader
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterfaceAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceAPI {
            return retrofit.create(ApiInterfaceAPI::class.java)
        }
    }

    //"https://api.ajkerdeal.com/"

    @POST("api/SelfDelivery/AddLatLag")
    fun updateCourierStatus(@Body requestBody: StatusLocationRequest): Call<ResponseHeader<Int>>

}