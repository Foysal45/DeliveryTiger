package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceMainResponse
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceReqBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BillingServiceInterface {

    @POST("api/Fetch/LoadCourierOrderAmountDetails")
    fun getAllBillingService(@Body body: BillingServiceReqBody): Call<GenericResponse<BillingServiceMainResponse>>

}