package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.deal_management.DealManagementRequest
import com.bd.deliverytiger.app.api.model.deal_management.DealManagementResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterfaceMerchant {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceMerchant {
            return retrofit.create(ApiInterfaceMerchant::class.java)
        }
    }

    @POST("/DealManagement/LoadAllDealsOfMerchantWithSizeNew")
    suspend fun fetchDealManagementData(@Body requestBody: DealManagementRequest): NetworkResponse<DealManagementResponse, ErrorResponse>

}