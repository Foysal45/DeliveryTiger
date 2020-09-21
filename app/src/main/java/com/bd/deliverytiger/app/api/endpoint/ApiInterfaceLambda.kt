package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.image_upload.ImageUploadResponse
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterfaceLambda {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceLambda {
            return retrofit.create(ApiInterfaceLambda::class.java)
        }
    }

    @Headers(
        "Content-Type: multipart/form-data",
    )
    @POST("v1/upload-and-resize-image")
    suspend fun uploadProductImage(
        @Query("bucket") bucket: String = "ajkerdeal-images-ohio-1",
        @Query("location") location: String = "images/deals/folderName",
        @Query("bigImageSize") bigImageSize: String = "1024,910",
        @Query("smallImageSize") smallImageSize: String = "250,222",
        @Query("miniImageSize") miniImageSize: String = "90,80",
        @Body file: RequestBody): NetworkResponse<ImageUploadResponse, ErrorResponse>

}