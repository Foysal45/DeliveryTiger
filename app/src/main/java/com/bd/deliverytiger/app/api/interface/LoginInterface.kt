package com.bd.deliverytiger.app.api.`interface`

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.LoginBody
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.login.SignUpReqBody
import com.bd.deliverytiger.app.api.model.login.SignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginInterface {

    @POST("api/Account/UserLogin")
    fun userLogin(@Body body: LoginBody): Call<GenericResponse<LoginResponse>>

    @POST("api/Account/{refreshToken}/RefreshToken")
    fun refreshToken(@Path("refreshToken") refreshToken: String): Call<GenericResponse<LoginResponse>>

    @POST("api/Account/UserRegister")
    fun userUserRegister(@Body body: SignUpReqBody): Call<GenericResponse<SignUpResponse>>

    @POST("api/Account/ResetPassword")
    fun userResetPassword(@Body body: SignUpReqBody): Call<GenericResponse<SignUpResponse>>

}