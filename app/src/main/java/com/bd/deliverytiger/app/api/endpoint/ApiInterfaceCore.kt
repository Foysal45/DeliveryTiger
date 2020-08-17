package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.config.BannerConfig
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponse
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.order.UpdateOrderResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*

interface ApiInterfaceCore {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceCore {
            return retrofit.create(ApiInterfaceCore::class.java)
        }
    }
    //"https://adcore.ajkerdeal.com/"

    @GET("api/Dashboard/GetBanner")
    fun getBannerInfo(): Call<GenericResponse<BannerConfig>>

    @POST("api/Dashboard/GetOrderCountByStatusGroupv2")
    fun getDashboardStatusGroup(@Body requestBody: DashBoardReqBody): Call<GenericResponse<DashboardResponse>>

    @GET("api/Other/GetAllDistrictFromApi/{id}")
    fun getAllDistrictFromApi(@Path("id") id: Int): Call<DeliveryChargePayLoad>

    @GET("api/Fetch/GetMerchantCredit/{courierUserId}")
    fun getMerchantCredit(@Path("courierUserId") courierUserId: Int): Call<GenericResponse<Boolean>>

    @GET("api/Fetch/GetBreakableCharge")
    fun getBreakableCharge(): Call<GenericResponse<BreakableChargeData>>

    @GET("api/Fetch/GetPackagingChargeRange/{onlyActive}")
    fun getPackagingCharge(@Path("onlyActive") onlyActive: Boolean = true): Call<GenericResponse<List<PackagingData>>>

    @POST("api/Fetch/DeliveryChargeDetailsAreaWise")
    fun getDeliveryCharge(@Body requestBody: DeliveryChargeRequest): Call<GenericResponse<List<DeliveryChargeResponse>>>

    @POST("api/Order/AddOrder")
    fun placeOrder(@Body requestBody: OrderRequest): Call<GenericResponse<OrderResponse>>

    @PUT("api/Update/UpdateCourierOrders/{courierOrdersId}")
    fun placeOrderUpdate(@Path("courierOrdersId") courierOrdersId: String, @Body requestBody: UpdateOrderReqBody): Call<GenericResponse<UpdateOrderResponse>>

    @GET("api/Fetch/GetPickupLocations/{courierUserId}")
    fun getPickupLocations(@Path("courierUserId") courierUserId: Int): Call<GenericResponse<List<PickupLocation>>>

    @PUT("api/Update/UpdateMerchantInformation/{courierOrdersId}")
    fun updateMerchantInformation(@Path("courierOrdersId") courierOrdersId: Int, @Body requestBody: ProfileUpdateReqBody): Call<GenericResponse<LoginResponse>>

    @PUT("api/Update/UpdatePickupLocations/{id}")
    fun updatePickupLocations(@Path("id") id: Int, @Body requestBody: PickupLocation): Call<GenericResponse<PickupLocation>>

    @GET("api/Fetch/GetMerchantCollectionCharge/{courierUserId}")
    fun getCollectionCharge(@Path("courierUserId") courierUserId: Int): Call<GenericResponse<Int>>
}