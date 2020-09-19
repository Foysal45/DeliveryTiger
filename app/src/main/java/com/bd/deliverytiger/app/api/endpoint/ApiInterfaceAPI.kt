package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.ResponseHeader
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import com.bd.deliverytiger.app.api.model.product_upload.ProductUploadResponse
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterfaceAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceAPI {
            return retrofit.create(ApiInterfaceAPI::class.java)
        }
    }

    //"https://api.ajkerdeal.com/"

    @POST("api/SelfDelivery/AddLatLag")
    fun updateCourierStatus(@Body requestBody: StatusLocationRequest): Call<ResponseHeader<Int>>

    @Multipart
    @POST("api/Classified/V1/ProductUpload")
    suspend fun uploadProductInfo(@Part("Data") ProductUploadReqBody: RequestBody): NetworkResponse<ResponseHeader<ProductUploadResponse>, ErrorResponse>

}