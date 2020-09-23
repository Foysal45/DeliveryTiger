package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CODResponse
import com.bd.deliverytiger.app.api.model.cod_collection.HubInfo
import com.bd.deliverytiger.app.api.model.config.BannerResponse
import com.bd.deliverytiger.app.api.model.courier_info.CourierInfoModel
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponse
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.offer.OfferUpdateRequest
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.order.UpdateOrderResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackReqBody
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.haroldadmin.cnradapter.NetworkResponse
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

    @GET("api/Dashboard/GetAllBanners")
    suspend fun getBannerInfo(): NetworkResponse<GenericResponse<BannerResponse>, ErrorResponse>

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
    suspend fun getPickupLocations(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<List<PickupLocation>>, ErrorResponse>

    @PUT("api/Update/UpdateMerchantInformation/{courierOrdersId}")
    fun updateMerchantInformation(@Path("courierOrdersId") courierOrdersId: Int, @Body requestBody: ProfileUpdateReqBody): Call<GenericResponse<LoginResponse>>

    @POST("api/Entry/AddPickupLocations")
    suspend fun addPickupLocations(@Body requestBody: PickupLocation): NetworkResponse<GenericResponse<PickupLocation>, ErrorResponse>

    @PUT("api/Update/UpdatePickupLocations/{id}")
    fun updatePickupLocations(@Path("id") id: Int, @Body requestBody: PickupLocation): Call<GenericResponse<PickupLocation>>

    @GET("api/Fetch/GetMerchantCollectionCharge/{courierUserId}")
    fun getCollectionCharge(@Path("courierUserId") courierUserId: Int): Call<GenericResponse<Int>>

    @GET("api/Fetch/GetCourierUsersInformation/{courierUserId}")
    suspend fun getCourierUsersInformation(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<CourierInfoModel>, ErrorResponse>

    @PUT("api/Offer/UpdateOffer/{orderId}")
    suspend fun updateOffer(@Path("orderId") orderId: Int, @Body requestBody: OfferUpdateRequest): NetworkResponse<GenericResponse<OrderResponse>, ErrorResponse>

    @POST("api/Fetch/GetOrderTracking/{flag}")
    suspend fun getOrderTrackingList(@Path("flag") flag: String, @Body requestBody: OrderTrackReqBody): NetworkResponse<GenericResponse<List<OrderTrackMainResponse>>, ErrorResponse>

    @GET("api/Fetch/GetCustomerOrders/{mobileNumber}")
    suspend fun fetchCustomerOrder(@Path("mobileNumber") mobileNumber: String): NetworkResponse<GenericResponse<CODResponse>, ErrorResponse>

    @GET("api/Fetch/GetAllHubs")
    suspend fun fetchAllHubInfo(): NetworkResponse<GenericResponse<List<HubInfo>>, ErrorResponse>

    @POST("api/Fetch/GetHubsByPickupLocation")
    suspend fun fetchHubByPickupLocation(@Body requestBody: PickupLocation): NetworkResponse<GenericResponse<HubInfo>, ErrorResponse>

}