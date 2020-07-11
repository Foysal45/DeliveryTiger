package com.bd.deliverytiger.app.api.`interface`

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.payment_history.PaymentData
import com.bd.deliverytiger.app.api.model.payment_history.PaymentDetailsResponse
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterfaceADM {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceADM {
            return retrofit.create(ApiInterfaceADM::class.java)
        }
    }

    //https://adm.ajkerdeal.com/

    @POST("api/account/reports/GetMerchantMonthlyReveivableList")
    suspend fun getMerchantMonthlyReceivable(@Body requestBody: MonthlyReceivableRequest): NetworkResponse<MonthlyReceivableResponse, ErrorResponse>

    @GET("api/account/reports/GetMerchantPaidChequeList/{courierUserId}")
    suspend fun getPaymentHistory(@Path("courierUserId") courierUserId: Int): NetworkResponse<List<PaymentData>, ErrorResponse>

    @GET("api/account/reports/GetMerchantPaidChequeDetails/{transactionId}")
    suspend fun getPaymentHistoryDetails(@Path("transactionId") transactionId: String): NetworkResponse<List<PaymentDetailsResponse>, ErrorResponse>

}