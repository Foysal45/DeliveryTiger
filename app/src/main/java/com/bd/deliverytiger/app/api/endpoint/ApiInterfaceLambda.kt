package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.ResponseHeader
import com.bd.deliverytiger.app.api.model.product_upload.ProductUploadResponse
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.http.*

interface ApiInterfaceLambda {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceLambda {
            return retrofit.create(ApiInterfaceLambda::class.java)
        }
    }

    @Headers(
        "API_KEY: ",
    )
    @Multipart
    @POST("v1/upload_and_resize_image")
    suspend fun uploadProductImage(
        @Query("bucket") bucket: String = "ajkerdeal-images-ohio-1",
        @Query("location") location: String = "images/deals/folderName",
        @Query("bigImageSize") bigImageSize: String = "1024,910",
        @Query("smallImageSize") smallImageSize: String = "250,222",
        @Query("miniImageSize") miniImageSize: String = "90,80",
        @Part file: MultipartBody.Part): NetworkResponse<ResponseHeader<ProductUploadResponse>, ErrorResponse>

}