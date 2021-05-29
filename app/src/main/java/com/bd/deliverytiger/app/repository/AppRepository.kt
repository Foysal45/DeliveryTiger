package com.bd.deliverytiger.app.repository

import com.bd.deliverytiger.app.api.endpoint.*
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceReqBody
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import com.bd.deliverytiger.app.api.model.complain.ComplainListRequest
import com.bd.deliverytiger.app.api.model.complain.ComplainRequest
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnedCountRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsRequest
import com.bd.deliverytiger.app.api.model.instant_payment_update.UpdatePaymentCycleRequest
import com.bd.deliverytiger.app.api.model.live.brand.BrandRequest
import com.bd.deliverytiger.app.api.model.live.catalog.CatalogRequest
import com.bd.deliverytiger.app.api.model.live.live_deal_management.InsertDealManagementRequestBody
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductInsertData
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule_insert.LiveScheduleInsertRequest
import com.bd.deliverytiger.app.api.model.live.live_started_notify.LiveStartedNotifyRequest
import com.bd.deliverytiger.app.api.model.live.live_status.LiveStatusUpdateRequest
import com.bd.deliverytiger.app.api.model.live.profile.ProfileData
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSRequest
import com.bd.deliverytiger.app.api.model.log_sms.SMSLogRequest
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.api.model.offer.OfferUpdateRequest
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackReqBody
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsRequest
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionAnswer
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableUpdateRequest
import com.bd.deliverytiger.app.api.model.service_selection.ServiceDistrictsRequest
import com.bd.deliverytiger.app.api.model.sms.SMSModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AppRepository(
    private val apiInterfaceADM: ApiInterfaceADM,
    private val apiInterfaceCore: ApiInterfaceCore,
    private val apiInterfaceAPI: ApiInterfaceAPI,
    private val apiInterfaceBridge: ApiInterfaceBRIDGE,
    private val apiInterfaceLambda: ApiInterfaceLambda,
    private val apiInterfaceBariKoi: ApiInterfaceBariKoi,
    private val apiInterfaceANA: ApiInterfaceANA
) {

    //******************** API ********************//
    suspend fun sendOTP(requestBody: OTPRequestModel) = apiInterfaceAPI.sendOTP(requestBody)

    suspend fun checkOTP(mobileNo: String, OPTCode: String) = apiInterfaceAPI.checkOTP(mobileNo, OPTCode)

    fun updateCourierStatus(requestBody: StatusLocationRequest) = apiInterfaceAPI.updateCourierStatus(requestBody)

    suspend fun uploadProductInfo(ProductUploadReqBody: RequestBody) = apiInterfaceAPI.uploadProductInfo(ProductUploadReqBody)

    suspend fun fetchAllDistricts() = apiInterfaceAPI.fetchAllDistricts()

    suspend fun uploadProductImage(data: RequestBody, file: List<MultipartBody.Part>?) = apiInterfaceAPI.uploadProductImage(data, file)

    // Live
    suspend fun getProductList(
        categoryId: Int, subCategoryId: Int, subSUbCategoryId: Int, routingName: String, index: Int, count: Int
    ) = apiInterfaceAPI.getProductList(categoryId, subCategoryId, subSUbCategoryId, routingName, index, count)

    suspend fun getAllCategory(categoryId: Int, type: Int = 0) = apiInterfaceAPI.getAllCategory(categoryId, type)

    suspend fun getVideoList(requestBody: CatalogRequest) = apiInterfaceAPI.getVideoList(requestBody)

    suspend fun getBrandListWithSearch(requestBody: BrandRequest) = apiInterfaceAPI.getBrandListWithSearch(requestBody)

    suspend fun fetchLiveSchedule(requestBody: ScheduleRequest) = apiInterfaceAPI.fetchLiveSchedule(requestBody)

    suspend fun insertLiveSchedule(requestBody: LiveScheduleInsertRequest) = apiInterfaceAPI.insertLiveSchedule(requestBody)

    suspend fun uploadLiveCoverPhoto(requestBody: RequestBody, file1: MultipartBody.Part? = null) = apiInterfaceAPI.uploadLiveCoverPhoto(requestBody, file1)

    suspend fun fetchUserSchedule(customerId: Int, type: String, index: Int, count: Int) = apiInterfaceAPI.fetchUserSchedule(customerId, type, index, count)

    suspend fun fetchUserScheduleReplay(customerId: Int, type: String, index: Int, count: Int) = apiInterfaceAPI.fetchUserScheduleReplay(customerId, type, index, count)

    suspend fun fetchLiveUserProfile(profileId: Int) = apiInterfaceAPI.fetchLiveUserProfile(profileId)

    suspend fun updateLiveUserProfile(requestBody: ProfileData) = apiInterfaceAPI.updateLiveUserProfile(requestBody)

    suspend fun insertFromOrderManagement(requestBody: InsertDealManagementRequestBody) = apiInterfaceAPI.insertFromOrderManagement(requestBody)

    //SMS
    suspend fun shareSMS(requestBody: SMSRequest) = apiInterfaceAPI.shareSMS(requestBody)

    suspend fun fetchTotalSMSCount(liveId: Int) = apiInterfaceAPI.fetchTotalSMSCount(liveId)

    suspend fun fetchFreeSMSCondition() = apiInterfaceAPI.fetchFreeSMSCondition()

    suspend fun insertLiveProducts(requestBody: List<LiveProductInsertData>) = apiInterfaceAPI.insertLiveProducts(requestBody)

    suspend fun uploadProductPhoto(productId: RequestBody, folderName: RequestBody, files: List<MultipartBody.Part>? = null) = apiInterfaceAPI.uploadProductPhoto(productId, folderName, files)

    suspend fun fetchLiveProducts(requestBody: LiveProductRequest) = apiInterfaceAPI.fetchLiveProducts(requestBody)

    suspend fun updateProductSoldOut(productId: Int, flag: Boolean) = apiInterfaceAPI.updateProductSoldOut(productId, flag)

    suspend fun fetchLiveOrderList(liveId: Int) = apiInterfaceAPI.fetchLiveOrderList(liveId)

    suspend fun deleteProductFromLive(productId: Int) = apiInterfaceAPI.deleteProductFromLive(productId)

    suspend fun updateLiveStatus(requestBody: LiveStatusUpdateRequest) = apiInterfaceAPI.updateLiveStatus(requestBody)

    suspend fun liveStartedNotify(requestBody: LiveStartedNotifyRequest) = apiInterfaceAPI.liveStartedNotify(requestBody)

    //******************** Analytics ********************//

    suspend fun logSMS(requestBody: SMSLogRequest) = apiInterfaceANA.logSMS(requestBody)

    //******************** BRIDGE ********************//

    suspend fun sendSMS(requestBody: List<SMSModel>) = apiInterfaceBridge.sendSMS(requestBody)

    //******************** LAMBDA ********************//

    suspend fun uploadProductImage(location: String, title: String, file: RequestBody) = apiInterfaceLambda.uploadProductImage(location = location, title = title, file = file)

    //******************** Bari Koi ********************//

    suspend fun getRoutingDetails(startLngLat: String, endLngLat: String) = apiInterfaceBariKoi.getRoutingDetails(startLngLat, endLngLat)

    //******************** ADM ********************//

    suspend fun getMerchantMonthlyReceivable(requestBody: MonthlyReceivableRequest) = apiInterfaceADM.getMerchantMonthlyReceivable(requestBody)

    suspend fun fetchMerchantReceivableList(courierUserId: Int) = apiInterfaceADM.fetchMerchantReceivableList(courierUserId)

    suspend fun bulkMerchantCashCollection(requestBody: MonthlyReceivableUpdateRequest) = apiInterfaceADM.bulkMerchantCashCollection(requestBody)

    suspend fun getPaymentHistory(courierUserId: Int) = apiInterfaceADM.getPaymentHistory(courierUserId)

    suspend fun getPaymentHistoryDetails(requestBody: PaymentDetailsRequest) = apiInterfaceADM.getPaymentHistoryDetails(requestBody)

    suspend fun fetchFreezeAmountData(courierUserId: Int) = apiInterfaceADM.fetchFreezeAmountData(courierUserId)

    suspend fun fetchFreezeAmountDetails(courierUserId: Int) = apiInterfaceADM.fetchFreezeAmountDetails(courierUserId)

    suspend fun submitComplain(requestBody: ComplainRequest) = apiInterfaceADM.submitComplain(requestBody)

    suspend fun fetchComplainList(requestBody: ComplainListRequest) = apiInterfaceADM.fetchComplainList(requestBody)

    suspend fun fetchBillPayHistory(courierUserId: Int) = apiInterfaceADM.fetchBillPayHistory(courierUserId)

    suspend fun fetchUnpaidCOD(courierUserId: Int) = apiInterfaceADM.fetchUnpaidCOD(courierUserId)

    suspend fun updateInstantPaymentRequest(courierUserId: Int) = apiInterfaceADM.updateInstantPaymentRequest(courierUserId)

    suspend fun fetchMerchantCurrentAdvanceBalance(courierUserId: Int) = apiInterfaceADM.fetchMerchantCurrentAdvanceBalance(courierUserId)

    suspend fun fetchDTMerchantInstantPaymentStatus(courierUserId: Int) = apiInterfaceADM.fetchDTMerchantInstantPaymentStatus(courierUserId)

    suspend fun getInstantPaymentActivationStatus(courierUserId: Int) = apiInterfaceADM.getInstantPaymentActivationStatus(courierUserId)

    //******************** ADCORE ********************//

    suspend fun getBannerInfo() = apiInterfaceCore.getBannerInfo()

    suspend fun getDashboardStatusGroup(requestBody: DashBoardReqBody) = apiInterfaceCore.getDashboardStatusGroup(requestBody)

    fun getAllDistrictFromApi(id: Int) = apiInterfaceCore.getAllDistrictFromApi(id)

    suspend fun loadAllDistricts() = apiInterfaceCore.loadAllDistricts()

    suspend fun fetchServiceDistricts(requestBody: ServiceDistrictsRequest) = apiInterfaceCore.fetchServiceDistricts(requestBody)

    suspend fun getDTService() = apiInterfaceCore.getDTService()

    fun getMerchantCredit(courierUserId: Int) = apiInterfaceCore.getMerchantCredit(courierUserId)

    fun getBreakableCharge() = apiInterfaceCore.getBreakableCharge()

    fun getPackagingCharge() = apiInterfaceCore.getPackagingCharge()

    fun getDeliveryCharge(requestBody: DeliveryChargeRequest) = apiInterfaceCore.getDeliveryCharge(requestBody)

    fun placeOrder(requestBody: OrderRequest) = apiInterfaceCore.placeOrder(requestBody)

    fun placeOrderUpdate(courierOrdersId: String, requestBody: UpdateOrderReqBody) = apiInterfaceCore.placeOrderUpdate(courierOrdersId, requestBody)

    suspend fun fetchCollectionTimeSlot() = apiInterfaceCore.fetchCollectionTimeSlot()

    suspend fun fetchDTOrderGenericLimit() = apiInterfaceCore.fetchDTOrderGenericLimit()

    suspend fun fetchOfferCharge(courierUserId: Int) = apiInterfaceCore.fetchOfferCharge(courierUserId)

    suspend fun getPickupLocations(courierUserId: Int) = apiInterfaceCore.getPickupLocations(courierUserId)

    suspend fun fetchPickupLocationsWithAcceptedOrderCount(courierUserId: Int) = apiInterfaceCore.fetchPickupLocationsWithAcceptedOrderCount(courierUserId)

    fun updateMerchantInformation(courierOrdersId: Int, requestBody: ProfileUpdateReqBody) = apiInterfaceCore.updateMerchantInformation(courierOrdersId, requestBody)

    suspend fun updatePickupLocations(id: Int, requestBody: PickupLocation) = apiInterfaceCore.updatePickupLocations(id, requestBody)

    suspend fun addPickupLocations(requestBody: PickupLocation) = apiInterfaceCore.addPickupLocations(requestBody)

    suspend fun  deletePickupLocations(id: Int) = apiInterfaceCore.deletePickupLocations(id)

    fun getCollectionCharge(courierUserId: Int) = apiInterfaceCore.getCollectionCharge(courierUserId)

    suspend fun getCourierUsersInformation(courierUserId: Int) = apiInterfaceCore.getCourierUsersInformation(courierUserId)

    suspend fun updatePaymentCycle(requestBody: UpdatePaymentCycleRequest) = apiInterfaceCore.updatePaymentCycle(requestBody)

    suspend fun updateOffer(orderId: Int, requestBody: OfferUpdateRequest) = apiInterfaceCore.updateOffer(orderId, requestBody)

    suspend fun getOrderTrackingList(flag: String, requestBody: OrderTrackReqBody) = apiInterfaceCore.getOrderTrackingList(flag, requestBody)

    suspend fun getOrderTrackingNewList(flag: String, requestBody: OrderTrackReqBody) = apiInterfaceCore.getOrderTrackingNewList(flag, requestBody)

    suspend fun fetchCustomerOrder(mobileNumber: String) = apiInterfaceCore.fetchCustomerOrder(mobileNumber)

    suspend fun fetchAllHubInfo() = apiInterfaceCore.fetchAllHubInfo()

    suspend fun fetchHubByPickupLocation(requestBody: PickupLocation) = apiInterfaceCore.fetchHubByPickupLocation(requestBody)

    suspend fun fetchRiderByPickupLocation(requestBody: PickupLocation) = apiInterfaceCore.fetchRiderByPickupLocation(requestBody)

    suspend fun fetchCollection(courierUserId: Int) = apiInterfaceCore.fetchCollection(courierUserId)

    suspend fun fetchDeliveryChargeCalculationInfo() = apiInterfaceCore.fetchDeliveryChargeCalculationInfo()

    suspend fun fetchPriceList(districtId: Int, deliveryRangeId: Int) = apiInterfaceCore.fetchPriceList(districtId, deliveryRangeId)

    suspend fun fetchServiceBillDetails(requestBody: BillingServiceReqBody) = apiInterfaceCore.fetchServiceBillDetails(requestBody)

    suspend fun fetchCODCollectionDetails(requestBody: CODReqBody) = apiInterfaceCore.fetchCODCollectionDetails(requestBody)

    suspend fun fetchBalanceLimit(merchantId: Int) = apiInterfaceCore.fetchBalanceLimit(merchantId)

    suspend fun fetchMerchantBalanceInfo(courierUserId: Int, amount: Int) = apiInterfaceCore.fetchMerchantBalanceInfo(courierUserId, amount)

    suspend fun fetchRefereeInfo() = apiInterfaceCore.fetchRefereeInfo()

    suspend fun fetchReferrerInfo() = apiInterfaceCore.fetchReferrerInfo()

    suspend fun fetchCollectionHistory(courierUserId: Int) = apiInterfaceCore.fetchCollectionHistory(courierUserId)

    suspend fun fetchReturnStatement(courierUserId: Int, index: Int, count: Int) = apiInterfaceCore.fetchReturnStatement(courierUserId, index, count)

    suspend fun fetchSurveyQuestion() = apiInterfaceCore.fetchSurveyQuestion()

    suspend fun fetchSubmitSurvey(requestBody: List<SurveyQuestionAnswer>) = apiInterfaceCore.fetchSubmitSurvey(requestBody)

    suspend fun isGetOfferByMerchant(courierUserId: Int) = apiInterfaceCore.isGetOfferByMerchant(courierUserId)

    suspend fun fetchDeliveredReturnedCount(requestBody: DeliveredReturnedCountRequest) = apiInterfaceCore.fetchDeliveredReturnedCount(requestBody)

    suspend fun fetchDeliveredReturnedCountWiseDetails(requestBody: DeliveryDetailsRequest) = apiInterfaceCore.fetchDeliveredReturnedCountWiseDetails(requestBody)

    suspend fun fetchHelpLineNumbers() = apiInterfaceCore.fetchHelpLineNumbers()

}