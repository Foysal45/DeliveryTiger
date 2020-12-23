package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.log_sms.SMSLogRequest
import com.bd.deliverytiger.app.api.model.log_sms.SMSLogResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterfaceANA {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceANA {
            return retrofit.create(ApiInterfaceANA::class.java)
        }
    }

    // https://analytics.ajkerdeal.com

    @POST("api/bkashsmslog")
    suspend fun logSMS(@Body requestBody: SMSLogRequest): NetworkResponse<SMSLogResponse, ErrorResponse>

}