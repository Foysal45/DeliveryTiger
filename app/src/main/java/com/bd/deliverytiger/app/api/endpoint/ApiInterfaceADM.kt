package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.accounts.*
import com.bd.deliverytiger.app.api.model.balance_load.BalanceLoadHistoryData
import com.bd.deliverytiger.app.api.model.bill_pay_history.BillPayHistoryResponse
import com.bd.deliverytiger.app.api.model.complain.ComplainData
import com.bd.deliverytiger.app.api.model.complain.ComplainHistoryData
import com.bd.deliverytiger.app.api.model.complain.ComplainListRequest
import com.bd.deliverytiger.app.api.model.complain.ComplainRequest
import com.bd.deliverytiger.app.api.model.complain.general_complain.GeneralComplainListRequest
import com.bd.deliverytiger.app.api.model.complain.general_complain.GeneralComplainResponse
import com.bd.deliverytiger.app.api.model.instant_payment_rate.AllAlertMessage
import com.bd.deliverytiger.app.api.model.instant_payment_rate.InstantPaymentRateModel
import com.bd.deliverytiger.app.api.model.instant_payment_status.InstantPaymentStatusData
import com.bd.deliverytiger.app.api.model.instant_payment_status.InstantPaymentActivationStatusResponse
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantInstantPaymentRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceivableDetailResponse
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceiveableDetailRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceiveableDetailResponse
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentData
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsRequest
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsResponse
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableResponse
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableUpdateRequest
import com.bd.deliverytiger.app.api.model.unpaid_cod.UnpaidCODResponse
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.*

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

    @POST("api/Complain/GetAllWithoutOrderCodeComplains")
    suspend fun fetchWithoutOrderCodeComplains(@Body requestBody: GeneralComplainListRequest):NetworkResponse<List<GeneralComplainResponse>, ErrorResponse>

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

    @GET("api/Complain/GetAllCommentsDTComplainForApp/{bookingCode}/{isVisibleToMerchant}")
    suspend fun getComplainHistory(@Path("bookingCode") bookingCode: Int, @Path("isVisibleToMerchant") isVisibleToMerchant: Int): NetworkResponse<List<ComplainHistoryData>, ErrorResponse>

    @GET("api/account/reports/MerchantBalanceLoadHistory/{merchantID}")
    suspend fun merchantBalanceLoadHistory(@Path("merchantID") merchantID: Int): NetworkResponse<List<BalanceLoadHistoryData>, ErrorResponse>

    @GET("api/account/reports/GetInstantPaymentRate")
    suspend fun getInstantPaymentRate(): NetworkResponse<InstantPaymentRateModel, ErrorResponse>

    @GET("api/account/reports/GetEFTPaymentRate")
    suspend fun getEftPaymentRate(): NetworkResponse<InstantPaymentRateModel, ErrorResponse>

    @GET("api/account/reports/GetSuperExpressCharge")
    suspend fun getSuperEftPaymentRate(): NetworkResponse<InstantPaymentRateModel, ErrorResponse>

    @POST("api/account/reports/MerchantPayableReceiveableDetailForInstantPaymentFromAppV3")
    suspend fun merchantPayableReceiveableDetailForInstantPayment(@Body requestBody: MerchantPayableReceiveableDetailRequest): NetworkResponse<MerchantPayableReceivableDetailResponse, ErrorResponse>

    @POST("api/account/reports/InstantOr24hourPaymentV3")
    suspend fun instantOr24hourPayment(@Body requestBody: MerchantInstantPaymentRequest): NetworkResponse<MerchantPayableReceiveableDetailResponse, ErrorResponse>

    @POST("api/account/reports/CheckbankNameForEFT")
    suspend fun checkBankNameForEFT(@Body requestBody: BankCheckForEftRequest): NetworkResponse<BankCheckForEftResponse, ErrorResponse>

    @GET("api/account/reports/GetMessageAlertForIP")
    suspend fun getMessageAlertForIP(): NetworkResponse<AllAlertMessage, ErrorResponse>

    @Multipart
    @POST("Image/ImageUploadForFile")
    suspend fun imageUploadForFile(
        @Part("FileName") fileName: RequestBody,
        @Part("ImageUrl") imagePath: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): NetworkResponse<Boolean, ErrorResponse>
}