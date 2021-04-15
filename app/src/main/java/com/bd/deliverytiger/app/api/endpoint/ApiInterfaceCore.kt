package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.ResponseHeader
import com.bd.deliverytiger.app.api.model.accounts.BalanceInfo
import com.bd.deliverytiger.app.api.model.balance_load.BalanceLimitResponse
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceMainResponse
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceReqBody
import com.bd.deliverytiger.app.api.model.calculator.DeliveryChargeInfo
import com.bd.deliverytiger.app.api.model.calculator.WeightPrice
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.cod_collection.CODResponse
import com.bd.deliverytiger.app.api.model.cod_collection.HubInfo
import com.bd.deliverytiger.app.api.model.collection_history.CollectionData
import com.bd.deliverytiger.app.api.model.config.BannerResponse
import com.bd.deliverytiger.app.api.model.courier_info.CourierInfoModel
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnCountResponseItem
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnedCountRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsResponse
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.AllDistrictListsModel
import com.bd.deliverytiger.app.api.model.generic_limit.GenericLimitData
import com.bd.deliverytiger.app.api.model.helpline_number.HelpLineNumberModel
import com.bd.deliverytiger.app.api.model.instant_payment_update.InstantPaymentUpdateResponse
import com.bd.deliverytiger.app.api.model.instant_payment_update.UpdatePaymentCycleRequest
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.offer.OfferUpdateRequest
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.order.UpdateOrderResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackReqBody
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.api.model.referral.OfferData
import com.bd.deliverytiger.app.api.model.referral.RefereeInfo
import com.bd.deliverytiger.app.api.model.referral.ReferrerInfo
import com.bd.deliverytiger.app.api.model.return_statement.ReturnStatementData
import com.bd.deliverytiger.app.api.model.rider.RiderInfo
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionAnswer
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionAnswerResponse
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.api.model.time_slot.TimeSlotData
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

    @POST("api/Dashboard/GetOrderCountByStatusGroupV3")
    suspend fun getDashboardStatusGroup(@Body requestBody: DashBoardReqBody): NetworkResponse<GenericResponse<List<DashboardData>>, ErrorResponse>

    @GET("api/Dashboard/GetCollection/{courierUserId}")
    suspend fun fetchCollection(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<DashboardData>, ErrorResponse>

    @GET("api/Other/GetAllDistrictFromApi/{id}")
    fun getAllDistrictFromApi(@Path("id") id: Int): Call<DeliveryChargePayLoad>

    @GET("api/Fetch/LoadAllDistricts")
    suspend fun loadAllDistricts(): NetworkResponse<GenericResponse<List<AllDistrictListsModel>>, ErrorResponse>

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

    @GET("api/Fetch/GetCollectionTimeSlot")
    suspend fun fetchCollectionTimeSlot(): NetworkResponse<GenericResponse<List<TimeSlotData>>, ErrorResponse>
    //fun fetchCollectionTimeSlot(): Call<GenericResponse<List<TimeSlotData>>>

    @GET("api/Fetch/GetDTOrderGenericLimit")
    suspend fun fetchDTOrderGenericLimit(): NetworkResponse<GenericResponse<GenericLimitData>, ErrorResponse>

    @GET("api/Settings/GetOfferCharge/{courierUserId}")
    suspend fun fetchOfferCharge(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<OfferData>, ErrorResponse>

    @GET("api/Fetch/GetPickupLocations/{courierUserId}")
    suspend fun getPickupLocations(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<List<PickupLocation>>, ErrorResponse>

    @GET("api/Fetch/GetPickupLocationsWithAcceptedOrderCount/{courierUserId}")
    suspend fun fetchPickupLocationsWithAcceptedOrderCount(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<List<PickupLocation>>, ErrorResponse>

    @PUT("api/Update/UpdateMerchantInformation/{courierOrdersId}")
    fun updateMerchantInformation(@Path("courierOrdersId") courierOrdersId: Int, @Body requestBody: ProfileUpdateReqBody): Call<GenericResponse<LoginResponse>>

    @POST("api/Entry/AddPickupLocations")
    suspend fun addPickupLocations(@Body requestBody: PickupLocation): NetworkResponse<GenericResponse<PickupLocation>, ErrorResponse>

    @PUT("api/Update/UpdatePickupLocations/{id}")
    suspend fun  updatePickupLocations(@Path("id") id: Int, @Body requestBody: PickupLocation): NetworkResponse<GenericResponse<PickupLocation>, ErrorResponse>

    @DELETE("api/Delete/DeletePickupLocations/{id}")
    suspend fun  deletePickupLocations(@Path("id") id: Int): NetworkResponse<GenericResponse<Int>, ErrorResponse>

    @GET("api/Fetch/GetMerchantCollectionCharge/{courierUserId}")
    fun getCollectionCharge(@Path("courierUserId") courierUserId: Int): Call<GenericResponse<Int>>

    @GET("api/Fetch/GetCourierUsersInformation/{courierUserId}")
    suspend fun getCourierUsersInformation(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<CourierInfoModel>, ErrorResponse>

    @PUT("api/Update/UpdatePaymentCycle")
    suspend fun updatePaymentCycle(@Body requestBody: UpdatePaymentCycleRequest): NetworkResponse<GenericResponse<InstantPaymentUpdateResponse>, ErrorResponse>

    @PUT("api/Offer/UpdateOffer/{orderId}")
    suspend fun updateOffer(@Path("orderId") orderId: Int, @Body requestBody: OfferUpdateRequest): NetworkResponse<GenericResponse<OrderResponse>, ErrorResponse>

    @POST("api/Fetch/GetOrderTracking/{flag}")
    suspend fun getOrderTrackingList(@Path("flag") flag: String, @Body requestBody: OrderTrackReqBody): NetworkResponse<GenericResponse<List<OrderTrackMainResponse>>, ErrorResponse>

    @POST("api/Fetch/GetOrderTrackingNew/{flag}")
    suspend fun getOrderTrackingNewList(@Path("flag") flag: String, @Body requestBody: OrderTrackReqBody): NetworkResponse<GenericResponse<OrderTrackResponse>, ErrorResponse>

    @GET("api/Fetch/GetCustomerOrders/{mobileNumber}")
    suspend fun fetchCustomerOrder(@Path("mobileNumber") mobileNumber: String): NetworkResponse<GenericResponse<CODResponse>, ErrorResponse>

    @GET("api/Fetch/GetAllHubs")
    suspend fun fetchAllHubInfo(): NetworkResponse<GenericResponse<List<HubInfo>>, ErrorResponse>

    @POST("api/Fetch/GetHubsByPickupLocation")
    suspend fun fetchHubByPickupLocation(@Body requestBody: PickupLocation): NetworkResponse<GenericResponse<HubInfo>, ErrorResponse>

    @POST("api/Fetch/GetAcceptedRiders")
    suspend fun fetchRiderByPickupLocation(@Body requestBody: PickupLocation): NetworkResponse<GenericResponse<List<RiderInfo>>, ErrorResponse>

    @GET("api/Dashboard/GetDeliveryChargeCalInfo")
    suspend fun fetchDeliveryChargeCalculationInfo(): NetworkResponse<GenericResponse<DeliveryChargeInfo>, ErrorResponse>

    @GET("api/Fetch/GetPriceList/{districtId}/{deliveryRangeId}")
    suspend fun fetchPriceList(@Path("districtId") districtId: Int, @Path("deliveryRangeId") deliveryRangeId: Int): NetworkResponse<GenericResponse<List<WeightPrice>>, ErrorResponse>

    @POST("api/Fetch/LoadCourierOrderAmountDetailsV2")
     suspend fun fetchServiceBillDetails(@Body requestBody: BillingServiceReqBody): NetworkResponse<GenericResponse<BillingServiceMainResponse>, ErrorResponse>

    @POST("api/Fetch/GetCodCollections")
    suspend fun fetchCODCollectionDetails(@Body requestBody: CODReqBody): NetworkResponse<GenericResponse<CODResponse>, ErrorResponse>

    @GET("api/Fetch/GetBalanceLoadLimit/{merchantId}")
    suspend fun fetchBalanceLimit(@Path("merchantId") merchantId: Int): NetworkResponse<GenericResponse<BalanceLimitResponse>, ErrorResponse>

    @GET("api/Dashboard/GetMerchantBalanceInfo/{courierUserId}/{amount}")
    suspend fun fetchMerchantBalanceInfo(@Path("courierUserId") courierUserId: Int, @Path("amount") amount: Int): NetworkResponse<GenericResponse<BalanceInfo>, ErrorResponse>

    @GET("api/Fetch/GetReferee")
    suspend fun fetchRefereeInfo(): NetworkResponse<GenericResponse<RefereeInfo>, ErrorResponse>

    @GET("api/Fetch/GetReferrer")
    suspend fun fetchReferrerInfo(): NetworkResponse<GenericResponse<ReferrerInfo>, ErrorResponse>

    @GET("api/Dashboard/GetCollectionHistory/{courierUserId}")
    suspend fun fetchCollectionHistory(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<List<CollectionData>>, ErrorResponse>

    @GET("api/Fetch/GetReturnOrders/{courierUserId}/{index}/{count}")
    suspend fun fetchReturnStatement(
        @Path("courierUserId") courierUserId: Int,
        @Path("index") index: Int,
        @Path("count") count: Int): NetworkResponse<GenericResponse<List<ReturnStatementData>> ,ErrorResponse>

    @GET("api/Fetch/GetSurveyQuestion")
    suspend fun fetchSurveyQuestion(): NetworkResponse<GenericResponse<List<SurveyQuestionModel>>, ErrorResponse>

    @POST("api/Entry/AddSurveyQuestionAnswerLog")
    suspend fun fetchSubmitSurvey(@Body requestBody: List<SurveyQuestionAnswer>): NetworkResponse<GenericResponse<List<SurveyQuestionAnswerResponse>>, ErrorResponse>

    @GET("api/Offer/GetOfferByMerchant/{courierUserId}")
    suspend fun isGetOfferByMerchant(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<Boolean>, ErrorResponse>

    @POST("api/Fetch/GetDeliveredReturnedCount")
    suspend fun fetchDeliveredReturnedCount(@Body requestBody: DeliveredReturnedCountRequest): NetworkResponse<GenericResponse<List<DeliveredReturnCountResponseItem>>, ErrorResponse>

    @POST("api/Fetch/GetDeliveredReturnedCountWiseDetails")
    suspend fun fetchDeliveredReturnedCountWiseDetails(@Body requestBody: DeliveryDetailsRequest): NetworkResponse<GenericResponse<List<DeliveryDetailsResponse>>, ErrorResponse>

    @GET("api/Dashboard/GetHelpLineNumbers")
    suspend fun fetchHelpLineNumbers(): NetworkResponse<GenericResponse<HelpLineNumberModel>, ErrorResponse>
}