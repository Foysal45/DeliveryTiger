package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.ResponseHeader
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import com.bd.deliverytiger.app.api.model.helpline_number.HelpLineNumberModel
import com.bd.deliverytiger.app.api.model.image_upload.ClassifiedImageUploadResponse
import com.bd.deliverytiger.app.api.model.location.LocationResponse
import com.bd.deliverytiger.app.api.model.login.OTPCheckResponse
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.api.model.login.OTPResponse
import com.bd.deliverytiger.app.api.model.product_upload.ProductUploadResponse
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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

}