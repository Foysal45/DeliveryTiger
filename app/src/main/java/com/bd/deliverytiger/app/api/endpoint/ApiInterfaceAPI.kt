package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.ResponseHeader
import com.bd.deliverytiger.app.api.model.image_upload.ClassifiedImageUploadResponse
import com.bd.deliverytiger.app.api.model.live.auth.AuthRequestBody
import com.bd.deliverytiger.app.api.model.live.auth.AuthResponseBody
import com.bd.deliverytiger.app.api.model.live.auth.SignUpNew
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductInsertData
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductInsertResponse
import com.bd.deliverytiger.app.api.model.live.live_product_insert.ProductGalleryData
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductData
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleData
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule_insert.LiveScheduleInsertRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule_list.MyLiveSchedule
import com.bd.deliverytiger.app.api.model.live.live_status.LiveStatusUpdateRequest
import com.bd.deliverytiger.app.api.model.live.products.ProductResponse
import com.bd.deliverytiger.app.api.model.live.share_sms.FreeSMSServiceModel
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSRequest
import com.bd.deliverytiger.app.api.model.location.LocationResponse
import com.bd.deliverytiger.app.api.model.login.OTPCheckResponse
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.api.model.login.OTPResponse
import com.bd.deliverytiger.app.api.model.product_upload.ProductUploadResponse
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.*

interface ApiInterfaceAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceAPI {
            return retrofit.create(ApiInterfaceAPI::class.java)
        }
    }

    //"https://api.ajkerdeal.com/"

    @POST("Recover/RetrivePassword/deliverytiger")
    suspend fun sendOTP(@Body requestBody: OTPRequestModel): NetworkResponse<OTPResponse, ErrorResponse>

    @GET("Recover/CheckOTP/{mobileNo}/{OTP}")
    suspend fun checkOTP(@Path("mobileNo") mobileNo: String, @Path("OTP") OPTCode: String): NetworkResponse<OTPCheckResponse, ErrorResponse>

    @Multipart
    @POST("api/Classified/V1/ProductUpload")
    suspend fun uploadProductInfo(@Part("Data") ProductUploadReqBody: RequestBody): NetworkResponse<ResponseHeader<ProductUploadResponse>, ErrorResponse>

    @GET("District/v3/LoadAllDistrictFromJson")
    suspend fun fetchAllDistricts(): NetworkResponse<ResponseHeader<LocationResponse>, ErrorResponse>

    @Multipart
    @POST("api/Classified/ClassifiedImageUpload")
    suspend fun uploadProductImage(
            @Part("data") data: RequestBody,
            @Part file: List<MultipartBody.Part>? = null
            ): NetworkResponse<ResponseHeader<ClassifiedImageUploadResponse>,ErrorResponse>

    // Live Stream

    @POST("api/videoshopping/LoadLiveVideoSchedule")
    suspend fun fetchLiveSchedule(@Body requestBody: ScheduleRequest): NetworkResponse<ResponseHeader<List<ScheduleData>>, ErrorResponse>

    @POST("api/videoshopping/LiveVideoInsert")
    suspend fun insertLiveSchedule(@Body requestBody: LiveScheduleInsertRequest): NetworkResponse<ResponseHeader<Int>, ErrorResponse>

    @Multipart
    @POST("api/videoshopping/LiveVideoCoverPhoto")
    suspend fun uploadLiveCoverPhoto(
        @Part("liveId") requestBody: RequestBody,
        @Part file1: MultipartBody.Part? = null) : NetworkResponse<ResponseHeader<Boolean>, ErrorResponse>

    //Live

    @GET("/GroupBuy/GetFreeProductLoad/{CategoryId}/{SubCategoryId}/{SubSUbCategoryId}/{routingName}/{Index}/{Count}")
    suspend fun getProductList(
        @Path("CategoryId") CategoryId: Int,
        @Path("SubCategoryId") SubCategoryId: Int,
        @Path("SubSUbCategoryId") SubSUbCategoryId: Int,
        @Path("routingName") routingName: String,
        @Path("Index") Index: Int,
        @Path("Count") Count: Int
    ): NetworkResponse<ResponseHeader<ProductResponse>, ErrorResponse>

    @GET("api/videoshopping/LoadUserSchedule/{customerId}/{type}/{Index}/{Count}")
    suspend fun fetchUserSchedule(
        @Path("customerId") customerId: Int,
        @Path("type") type: String,
        @Path("Index") Index: Int,
        @Path("Count") Count: Int
    ): NetworkResponse<ResponseHeader<List<MyLiveSchedule>>, ErrorResponse>

    @GET("api/videoshopping/LoadReplayLive/{customerId}/{type}/{Index}/{Count}")
    suspend fun fetchUserScheduleReplay(
        @Path("customerId") customerId: Int,
        @Path("type") type: String,
        @Path("Index") Index: Int,
        @Path("Count") Count: Int
    ): NetworkResponse<ResponseHeader<List<MyLiveSchedule>>, ErrorResponse>

    @POST("api/videoshopping/LoadAllLiveVideoProducts")
    suspend fun fetchLiveProducts(@Body requestBody: LiveProductRequest): NetworkResponse<ResponseHeader<List<LiveProductData>>, ErrorResponse>

    @GET("api/videoshopping/UpdateProductSoldOutStatus/{productId}/{flag}")
    suspend fun updateProductSoldOut(@Path("productId") productId: Int, @Path("flag") flag: Boolean): NetworkResponse<ResponseHeader<Int>, ErrorResponse>

    // Live Order Details

    @POST("api/videoshopping/InsertLiveVideoProducts")
    suspend fun insertLiveProducts(@Body requestBody: List<LiveProductInsertData>): NetworkResponse<ResponseHeader<LiveProductInsertResponse>, ErrorResponse>

    @Multipart
    @POST("api/videoshopping/v1/LiveVideoProductImage")
    suspend fun uploadProductPhoto(
        @Part("ProductId") productId: RequestBody,
        @Part("FolderName") folderName: RequestBody,
        @Part files: List<MultipartBody.Part>? = null) : NetworkResponse<ResponseHeader<Boolean>, ErrorResponse>

    @GET("api/videoshopping/LiveProductDelete/{productId}")
    suspend fun deleteProductFromLive(@Path("productId") productId: Int): NetworkResponse<ResponseHeader<Int>, ErrorResponse>

    @POST("api/videoshopping/UpdateLiveStatus")
    suspend fun updateLiveStatus(@Body requestBody: LiveStatusUpdateRequest): NetworkResponse<ResponseHeader<Int>, ErrorResponse>


    //Free SMS Services
    @GET("live/PlazaConfig")
    suspend fun fetchFreeSMSCondition(): NetworkResponse<ResponseHeader<FreeSMSServiceModel>, ErrorResponse>

    @POST("api/videoshopping/LiveShoppingShareSMS")
    suspend fun shareSMS(@Body requestBody: SMSRequest): NetworkResponse<ResponseHeader<Boolean>, ErrorResponse>

    @GET("api/videoshopping/TotalSMSCount/{liveId}")
    suspend fun fetchTotalSMSCount(@Path("liveId") liveId: Int): NetworkResponse<ResponseHeader<Int>, ErrorResponse>

    @POST("api/videoshopping/InsertMultiProducts")
    suspend fun insertProductByID(@Body responseBody: List<ProductGalleryData>): NetworkResponse<ResponseHeader<Boolean>, ErrorResponse>


    //Customer Exists check
    @POST("CustomerAccess/CustomerAuthenticationCheck")
    suspend fun customerAuthenticationCheck(@Body requestBody: AuthRequestBody): NetworkResponse<ResponseHeader<AuthResponseBody>, ErrorResponse>

    @POST("CustomerAccess/SignUpNew")
    suspend fun signUpForLivePlaza(@Body requestBody: SignUpNew): NetworkResponse<AuthResponseBody, ErrorResponse>
}