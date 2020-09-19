package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.sms.SMSModel
import com.bd.deliverytiger.app.api.model.sms.SMSResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterfaceBRIDGE {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceBRIDGE {
            return retrofit.create(ApiInterfaceBRIDGE::class.java)
        }
    }

    @Headers(
        "API_KEY: Ajkerdeal_~La?Rj73FcLm",
    )
    @POST("SmsComunication/SendSms")
    suspend fun sendSMS(@Body requestBody: List<SMSModel>): NetworkResponse<List<SMSResponse>, ErrorResponse>


}