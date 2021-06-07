package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.accounts.AccountDetailsResponse
import com.bd.deliverytiger.app.api.model.accounts.AccountsData
import com.bd.deliverytiger.app.api.model.accounts.AdvanceBalanceData
import com.bd.deliverytiger.app.api.model.bill_pay_history.BillPayHistoryResponse
import com.bd.deliverytiger.app.api.model.complain.ComplainData
import com.bd.deliverytiger.app.api.model.complain.ComplainHistoryData
import com.bd.deliverytiger.app.api.model.complain.ComplainListRequest
import com.bd.deliverytiger.app.api.model.complain.ComplainRequest
import com.bd.deliverytiger.app.api.model.instant_payment_status.InstantPaymentStatusData
import com.bd.deliverytiger.app.api.model.instant_payment_status.InstantPaymentActivationStatusResponse
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentData
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsRequest
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsResponse
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableResponse
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableUpdateRequest
import com.bd.deliverytiger.app.api.model.unpaid_cod.UnpaidCODResponse
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

    @POST("api/account/reports/GetMerchantMonthlyReceivableList_V2")
    suspend fun getMerchantMonthlyReceivable(@Body requestBody: MonthlyReceivableRequest): NetworkResponse<MonthlyReceivableResponse, ErrorResponse>

    @GET("api/account/reports/GetMerchantReceivableList/{courierUserId}")
    suspend fun fetchMerchantReceivableList(@Path("courierUserId") courierUserId: Int): NetworkResponse<MonthlyReceivableResponse, ErrorResponse>

    @POST("api/account/reports/BulkMerchantCashCollection")
    suspend fun bulkMerchantCashCollection(@Body requestBody: MonthlyReceivableUpdateRequest): NetworkResponse<String, ErrorResponse>

    @GET("api/account/reports/GetDTMerchantPaidChequeList/{courierUserId}")
    suspend fun getPaymentHistory(@Path("courierUserId") courierUserId: Int): NetworkResponse<List<PaymentData>, ErrorResponse>

    @POST("api/account/reports/GetDTMerchantPaidChequeDetails_V2")
    suspend fun getPaymentHistoryDetails(@Body requestBody: PaymentDetailsRequest): NetworkResponse<List<PaymentDetailsResponse>, ErrorResponse>

    @GET("api/account/reports/GetDTMerchantFreezeAmountInfo/{courierUserId}")
    suspend fun fetchFreezeAmountData(@Path("courierUserId") courierUserId: Int): NetworkResponse<AccountsData, ErrorResponse>

    @GET("api/account/reports/GetDTMerchantFreezeAmountDetails/{courierUserId}")
    suspend fun fetchFreezeAmountDetails(@Path("courierUserId") courierUserId: Int): NetworkResponse<AccountDetailsResponse, ErrorResponse>

    @POST("api/ComplainInsert/InsertDTComplain")
    suspend fun submitComplain(@Body requestBody: ComplainRequest): NetworkResponse<Int, ErrorResponse>

    @POST("api/Complain/ComplainList")
    suspend fun fetchComplainList(@Body requestBody: ComplainListRequest):NetworkResponse<List<ComplainData>, ErrorResponse>

    @GET("api/account/reports/GetDTCollectedFromMerchantData/{courierUserId}")
    suspend fun fetchBillPayHistory(@Path("courierUserId") courierUserId: Int): NetworkResponse<List<BillPayHistoryResponse>, ErrorResponse>

    @GET("api/account/reports/GetDtAllDeliveredOrdersAccounting/{courierUserId}")
    suspend fun fetchUnpaidCOD(@Path("courierUserId") courierUserId: Int): NetworkResponse<UnpaidCODResponse, ErrorResponse>

    @GET("api/account/reports/UpdateInstantPaymentRequestFlag/{courierUserId}")
    suspend fun updateInstantPaymentRequest(@Path("courierUserId") courierUserId: Int): NetworkResponse<Boolean, ErrorResponse>

    @GET("api/account/reports/GetMerchantCurrentAdvanceBalance/{courierUserId}")
    suspend fun fetchMerchantCurrentAdvanceBalance(@Path("courierUserId") courierUserId: Int): NetworkResponse<AdvanceBalanceData, ErrorResponse>

    @GET("api/account/reports/GetDTMerchantInstantPaymentStatus/{courierUserId}")
    suspend fun fetchDTMerchantInstantPaymentStatus(@Path("courierUserId") courierUserId: Int): NetworkResponse<InstantPaymentStatusData, ErrorResponse>

    @GET("api/account/reports/GetInstantPaymentActivationStatus/{courierUserId}")
    suspend fun getInstantPaymentActivationStatus(@Path("courierUserId") courierUserId: Int): NetworkResponse<InstantPaymentActivationStatusResponse, ErrorResponse>

    @GET("api/Complain/GetAllCommentsForOnlyDTComplain/{bookingCode}")
    suspend fun getComplainHistory(@Path("bookingCode") bookingCode: Int): NetworkResponse<List<ComplainHistoryData>, ErrorResponse>

}