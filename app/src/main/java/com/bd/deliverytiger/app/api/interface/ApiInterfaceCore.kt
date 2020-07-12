package com.bd.deliverytiger.app.api.`interface`

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponseModel
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterfaceCore {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceCore {
            return retrofit.create(ApiInterfaceCore::class.java)
        }
    }
    //"https://adcore.ajkerdeal.com/"

    @POST("api/Dashboard/GetOrderCountByStatusGroup")
    fun getDashboardStatusGroup(@Body requestBody: DashBoardReqBody): Call<GenericResponse<List<DashboardResponseModel>>>

    @GET("api/Other/GetAllDistrictFromApi/{id}")
    fun getAllDistrictFromApi(@Path("id") id: Int): Call<DeliveryChargePayLoad>

    @GET("api/Fetch/GetMerchantCredit/{courierUserId}")
    fun getMerchantCredit(@Path("courierUserId") courierUserId: Int): Call<GenericResponse<Boolean>>

}