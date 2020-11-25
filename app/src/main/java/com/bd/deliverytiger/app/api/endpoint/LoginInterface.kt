package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.*
import retrofit2.Call
import retrofit2.http.*

interface LoginInterface {

    @POST("api/Account/UserLogin")
    fun userLogin(@Body body: LoginBody): Call<GenericResponse<LoginResponse>>

    @POST("api/Account/{refreshToken}/RefreshToken")
    fun refreshToken(@Path("refreshToken") refreshToken: String): Call<GenericResponse<LoginResponse>>

    @POST("api/Account/UserRegister")
    fun userUserRegister(@Body body: SignUpReqBody): Call<GenericResponse<SignUpResponse>>

    @PUT("api/Account/ResetPassword")
    fun userResetPassword(@Body body: SignUpReqBody): Call<GenericResponse<SignUpResponse>>

    @POST("api/Account/GetCourierUsers")
    fun getUserInfo(@Body requestBody: UserInfoRequest): Call<GenericResponse<LoginResponse>>

    @POST("Recover/RetrivePassword/deliverytiger")
    fun sendOTP(@Body requestBody: OTPRequestModel): Call<OTPResponse>

    @GET("Recover/CheckOTP/{mobileNo}/{OTP}")
    fun checkOTP(@Path("mobileNo") mobileNo: String, @Path("OTP") OPTCode: String): Call<OTPCheckResponse>

    @GET("api/Account/CheckReferrerMobile/{referrerMobile}")
    fun checkReferrerMobile(@Path("referrerMobile") referrerMobile: String): Call<GenericResponse<SignUpResponse>>
}