package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.accepted_orders.AcceptedOrder
import com.bd.deliverytiger.app.api.model.accounts.BalanceInfo
import com.bd.deliverytiger.app.api.model.balance_load.BalanceLimitResponse
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceMainResponse
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceReqBody
import com.bd.deliverytiger.app.api.model.bulk_status.StatusUpdateData
import com.bd.deliverytiger.app.api.model.calculator.DeliveryChargeInfo
import com.bd.deliverytiger.app.api.model.calculator.WeightPrice
import com.bd.deliverytiger.app.api.model.category.CategoryData
import com.bd.deliverytiger.app.api.model.category.SubCategoryData
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.cod_collection.CODResponse
import com.bd.deliverytiger.app.api.model.cod_collection.HubInfo
import com.bd.deliverytiger.app.api.model.collection_history.CollectionData
import com.bd.deliverytiger.app.api.model.collector_info.CollectorInfoRequest
import com.bd.deliverytiger.app.api.model.collector_info.CollectorInformation
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import com.bd.deliverytiger.app.api.model.config.BannerResponse
import com.bd.deliverytiger.app.api.model.courier_info.CourierInfoModel
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnCountResponseItem
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnedCountRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsResponse
import com.bd.deliverytiger.app.api.model.district.DistrictData
import com.bd.deliverytiger.app.api.model.generic_limit.GenericLimitData
import com.bd.deliverytiger.app.api.model.helpline_number.HelpLineNumberModel
import com.bd.deliverytiger.app.api.model.instant_payment_update.InstantPaymentUpdateResponse
import com.bd.deliverytiger.app.api.model.instant_payment_update.UpdatePaymentCycleRequest
import com.bd.deliverytiger.app.api.model.lead_management.*
import com.bd.deliverytiger.app.api.model.lead_management.customer_details.CustomerInfoDetails
import com.bd.deliverytiger.app.api.model.lead_management.customer_details.CustomerInfoDetailsRequest
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookData
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookGroupData
import com.bd.deliverytiger.app.api.model.loan_survey.CourierModel
import com.bd.deliverytiger.app.api.model.loan_survey.LoanSurveyRequestBody
import com.bd.deliverytiger.app.api.model.loan_survey.SelectedCourierModel
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.offer.OfferUpdateRequest
import com.bd.deliverytiger.app.api.model.order.*
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackReqBody
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.api.model.quick_order.*
import com.bd.deliverytiger.app.api.model.quick_order.quick_order_history.QuickOrderList
import com.bd.deliverytiger.app.api.model.quick_order.quick_order_history.QuickOrderListRequest
import com.bd.deliverytiger.app.api.model.referral.OfferData
import com.bd.deliverytiger.app.api.model.referral.RefereeInfo
import com.bd.deliverytiger.app.api.model.referral.ReferrerInfo
import com.bd.deliverytiger.app.api.model.return_statement.ReturnStatementData
import com.bd.deliverytiger.app.api.model.rider.RiderInfo
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionAnswer
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionAnswerResponse
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.api.model.service_selection.ServiceDistrictsRequest
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoData
import com.bd.deliverytiger.app.api.model.sms.SMSModel
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

    @GET("api/Fetch/LoadAllDistrictsById/{id}")
    suspend fun loadAllDistrictsById(@Path("id") id: Int): NetworkResponse<GenericResponse<List<DistrictData>>, ErrorResponse>

    @POST("api/Fetch/LoadAllDistrictsByIds")
    suspend fun loadAllDistrictsByIdList(@Body requestBody: List<GetLocationInfoRequest>): NetworkResponse<GenericResponse<List<DistrictData>>, ErrorResponse>

    @GET("api/Fetch/LoadAllDistricts")
    suspend fun loadAllDistricts(): NetworkResponse<GenericResponse<List<DistrictData>>, ErrorResponse>

    @POST("api/Fetch/GetServiceDistricts")
    suspend fun fetchServiceDistricts(@Body requestBody: ServiceDistrictsRequest): NetworkResponse<GenericResponse<List<DistrictData>>, ErrorResponse>

    @GET("api/Fetch/GetDTServices")
    suspend fun getDTService(): NetworkResponse<GenericResponse<List<ServiceInfoData>>, ErrorResponse>

    @GET("api/Fetch/GetMerchantCredit/{courierUserId}")
    fun getMerchantCredit(@Path("courierUserId") courierUserId: Int): Call<GenericResponse<Boolean>>

    @GET("api/Fetch/GetBreakableCharge")
    suspend fun getBreakableCharge(): NetworkResponse<GenericResponse<BreakableChargeData>, ErrorResponse>

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
    suspend fun updatePickupLocations(@Path("id") id: Int, @Body requestBody: PickupLocation): NetworkResponse<GenericResponse<PickupLocation>, ErrorResponse>

    @DELETE("api/Delete/DeletePickupLocations/{id}")
    suspend fun deletePickupLocations(@Path("id") id: Int): NetworkResponse<GenericResponse<Int>, ErrorResponse>

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
        @Path("count") count: Int
    ): NetworkResponse<GenericResponse<List<ReturnStatementData>>, ErrorResponse>

    @GET("api/Fetch/GetSurveyQuestion")
    suspend fun fetchSurveyQuestion(): NetworkResponse<GenericResponse<List<SurveyQuestionModel>>, ErrorResponse>

    @POST("api/Entry/AddSurveyQuestionAnswerLog")
    suspend fun fetchSubmitSurvey(@Body requestBody: List<SurveyQuestionAnswer>): NetworkResponse<GenericResponse<List<SurveyQuestionAnswerResponse>>, ErrorResponse>

    @GET("api/Offer/GetOfferByMerchant/{courierUserId}")
    suspend fun isGetOfferByMerchant(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<Boolean>, ErrorResponse>

    @GET("/api/Dashboard/GetCustomerInfoByMobile/{mobile}")
    suspend fun getCustomerInfoByMobile(@Path("mobile") mobile: String): NetworkResponse<GenericResponse<CustomerInfo>, ErrorResponse>

    @POST("api/Fetch/GetDeliveredReturnedCount")
    suspend fun fetchDeliveredReturnedCount(@Body requestBody: DeliveredReturnedCountRequest): NetworkResponse<GenericResponse<List<DeliveredReturnCountResponseItem>>, ErrorResponse>

    @POST("api/Fetch/GetDeliveredReturnedCountWiseDetails")
    suspend fun fetchDeliveredReturnedCountWiseDetails(@Body requestBody: DeliveryDetailsRequest): NetworkResponse<GenericResponse<List<DeliveryDetailsResponse>>, ErrorResponse>

    @GET("api/Dashboard/GetHelpLineNumbers")
    suspend fun fetchHelpLineNumbers(): NetworkResponse<GenericResponse<HelpLineNumberModel>, ErrorResponse>

    @POST("api/Fetch/GetRidersOfficeInfo")
    suspend fun getRidersOfficeInfo(@Body requestBody: CollectorInfoRequest): NetworkResponse<GenericResponse<CollectorInformation>, ErrorResponse>

    @PUT("api/Update/UpdateCourierOrdersAppV2/{orderId}")
    suspend fun updateOrderInfo(
        @Path("orderId") orderId: String,
        @Body requestBody: UpdateOrderReqBody
    ): NetworkResponse<GenericResponse<UpdateOrderResponse>, ErrorResponse>

    @POST("api/Fetch/GetCutomerListForApp")
    suspend fun fetchCustomerList(@Body requestBody: CustomerInfoRequest): NetworkResponse<GenericResponse<List<CustomerInformation>>, ErrorResponse>

    @POST("api/Fetch/GetCutomerWiseOrdersDetailsForApp")
    suspend fun fetchCustomerDetailsList(@Body requestBody: CustomerInfoDetailsRequest): NetworkResponse<GenericResponse<List<CustomerInfoDetails>>, ErrorResponse>

    //Quick Order Request
    @POST("api/Fetch/GetCollectionTimeSlotByTime")
    suspend fun getCollectionTimeSlot(@Body requestBody: TimeSlotRequest): NetworkResponse<GenericResponse<List<QuickOrderTimeSlotData>>, ErrorResponse>

    @POST("api/Entry/AddOrderRequest")
    suspend fun quickOrderRequest(@Body requestBody: QuickOrderRequest): NetworkResponse<GenericResponse<QuickOrderRequestResponse>, ErrorResponse>

    @POST("api/Bondhu/GetMerchantQuickOrders")
    suspend fun getMerchantQuickOrders(@Body requestBody: QuickOrderListRequest): NetworkResponse<GenericResponse<List<QuickOrderList>>, ErrorResponse>

    @PUT("api/QuickOrder/UpdateMultipleTimeSlot")
    suspend fun updateMultipleTimeSlot(@Body requestBody: List<TimeSlotUpdateRequest>): NetworkResponse<GenericResponse<Int>, ErrorResponse>

    @DELETE("api/QuickOrder/DeleteOrderRequest/{orderRequestId}")
    suspend fun deleteOrderRequest(@Path("orderRequestId") orderRequestId: Int): NetworkResponse<GenericResponse<Int>, ErrorResponse>

    @POST("api/Bondhu/AddLatLag")
    suspend fun updateCourierStatusDT(@Body requestBody: StatusLocationRequest): NetworkResponse<GenericResponse<Int>, ErrorResponse>

    @GET("api/Fetch/GetAcceptedCourierOrders/{courierUserId}")
    suspend fun fetchAcceptedCourierOrders(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<AcceptedOrder>, ErrorResponse>

    @PUT("api/Update/UpdateCustomerSMSLimit/{courierUserId}/{customerSMSLimit}")
    suspend fun updateCustomerSMSLimit(@Path("courierUserId") courierUserId: Int, @Path("customerSMSLimit") customerSMSLimit: Int): NetworkResponse<GenericResponse<CourierInfoModel>, ErrorResponse>

    @POST("api/SmsComunication/SendSms")
    suspend fun sendSMSCommunication(@Body requestBody: List<SMSModel>): NetworkResponse<GenericResponse<Boolean>, ErrorResponse>

    @PUT("api/Update/UpdateBulkStatus")
    suspend fun updateBulkStatus(@Body requestBody: List<StatusUpdateData>): NetworkResponse<GenericResponse<Int>, ErrorResponse>

    @GET("api/Fetch/GetDtCategories/true")
    suspend fun fetchCategory(): NetworkResponse<GenericResponse<List<CategoryData>>, ErrorResponse>

    @GET("api/Fetch/GetSubCategoryById/true/{categoryId}")
    suspend fun fetchSubCategoryById(@Path("categoryId") categoryId: Int): NetworkResponse<GenericResponse<List<SubCategoryData>>, ErrorResponse>

    @GET("api/Dashboard/GetCouriers")
    suspend fun fetchCourierList(): NetworkResponse<GenericResponse<List<CourierModel>>, ErrorResponse>

    @POST("api/Loan/AddLoanSurvey")
    suspend fun submitLoanSurvey(@Body requestBody: LoanSurveyRequestBody): NetworkResponse<GenericResponse<LoanSurveyRequestBody>, ErrorResponse>

    @POST("api/Entry/AddCouriersWithLoanSurvey")
    suspend fun submitCourierList(@Body requestBody: List<SelectedCourierModel>): NetworkResponse<List<SelectedCourierModel>, ErrorResponse>

    @POST("api/Entry/AddCustomerSMSLog")
    suspend fun saveCustomerSMSLog(@Body requestBody: List<SMSLogData>): NetworkResponse<GenericResponse<List<SMSLogData>>, ErrorResponse>

    // Add customer number to virtual phonebook
    @POST("api/Entry/AddOwnPhoneBook")
    suspend fun addToOwnPhoneBook(@Body requestBody: List<PhonebookData>): NetworkResponse<GenericResponse<List<PhonebookData>>, ErrorResponse>

    // Create new group in virtual phonebook
    @POST("api/Entry/AddPhoneBookGroup")
    suspend fun createPhoneBookGroup(@Body requestBody: List<PhonebookGroupData>): NetworkResponse<GenericResponse<List<PhonebookGroupData>>, ErrorResponse>

    // Get all group from my virtual phonebook
    @GET("api/Fetch/GetMyPhoneBookGroup/{courierUserId}")
    suspend fun fetchMyPhoneBookGroup(@Path("courierUserId") courierUserId: Int): NetworkResponse<GenericResponse<List<PhonebookGroupData>>, ErrorResponse>

    // Add customer number to virtual phonebook group
    @POST("api/Entry/AddNumnerInGroup")
    suspend fun addToOwnPhoneBookGroup(@Body requestBody: List<PhonebookData>): NetworkResponse<GenericResponse<Int>, ErrorResponse>


}