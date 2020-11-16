package com.bd.deliverytiger.app.repository

import com.bd.deliverytiger.app.api.endpoint.*
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceReqBody
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import com.bd.deliverytiger.app.api.model.complain.ComplainRequest
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.api.model.offer.OfferUpdateRequest
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackReqBody
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableUpdateRequest
import com.bd.deliverytiger.app.api.model.sms.SMSModel
import okhttp3.RequestBody

class AppRepository(
    private val apiInterfaceADM: ApiInterfaceADM,
    private val apiInterfaceCore: ApiInterfaceCore,
    private val apiInterfaceAPI: ApiInterfaceAPI,
    private val apiInterfaceBridge: ApiInterfaceBRIDGE,
    private val apiInterfaceLambda: ApiInterfaceLambda,
    private val apiInterfaceBariKoi: ApiInterfaceBariKoi
) {

    //******************** API ********************//
    suspend fun sendOTP(requestBody: OTPRequestModel) = apiInterfaceAPI.sendOTP(requestBody)

    suspend fun checkOTP(mobileNo: String, OPTCode: String) = apiInterfaceAPI.checkOTP(mobileNo, OPTCode)

    fun updateCourierStatus(requestBody: StatusLocationRequest) = apiInterfaceAPI.updateCourierStatus(requestBody)

    suspend fun uploadProductInfo(ProductUploadReqBody: RequestBody) = apiInterfaceAPI.uploadProductInfo(ProductUploadReqBody)

    //******************** BRIDGE ********************//

    suspend fun sendSMS(requestBody: List<SMSModel>) = apiInterfaceBridge.sendSMS(requestBody)

    //******************** LAMBDA ********************//

    suspend fun uploadProductImage(location: String, file: RequestBody) = apiInterfaceLambda.uploadProductImage(location = location, file = file)

    //******************** Bari Koi ********************//

    suspend fun getRoutingDetails(startLngLat: String, endLngLat: String) = apiInterfaceBariKoi.getRoutingDetails(startLngLat, endLngLat)

    //******************** ADM ********************//

    suspend fun getMerchantMonthlyReceivable(requestBody: MonthlyReceivableRequest) = apiInterfaceADM.getMerchantMonthlyReceivable(requestBody)

    suspend fun fetchMerchantReceivableList(courierUserId: Int) = apiInterfaceADM.fetchMerchantReceivableList(courierUserId)

    suspend fun bulkMerchantCashCollection(requestBody: MonthlyReceivableUpdateRequest) = apiInterfaceADM.bulkMerchantCashCollection(requestBody)

    suspend fun getPaymentHistory(courierUserId: Int) = apiInterfaceADM.getPaymentHistory(courierUserId)

    suspend fun getPaymentHistoryDetails(courierUserId: Int, transactionId: String) = apiInterfaceADM.getPaymentHistoryDetails(courierUserId, transactionId)

    suspend fun fetchFreezeAmountData(courierUserId: Int) = apiInterfaceADM.fetchFreezeAmountData(courierUserId)

    suspend fun fetchFreezeAmountDetails(courierUserId: Int) = apiInterfaceADM.fetchFreezeAmountDetails(courierUserId)

    suspend fun submitComplain(requestBody: ComplainRequest) = apiInterfaceADM.submitComplain(requestBody)

    suspend fun fetchBillPayHistory(courierUserId: Int) = apiInterfaceADM.fetchBillPayHistory(courierUserId)

    suspend fun fetchUnpaidCOD(courierUserId: Int) = apiInterfaceADM.fetchUnpaidCOD(courierUserId)

    suspend fun updateInstantPaymentRequest(courierUserId: Int) = apiInterfaceADM.updateInstantPaymentRequest(courierUserId)

    suspend fun fetchMerchantCurrentAdvanceBalance(courierUserId: Int) = apiInterfaceADM.fetchMerchantCurrentAdvanceBalance(courierUserId)


    //******************** ADCORE ********************//

    suspend fun getBannerInfo() = apiInterfaceCore.getBannerInfo()

    suspend fun getDashboardStatusGroup(requestBody: DashBoardReqBody) = apiInterfaceCore.getDashboardStatusGroup(requestBody)

    fun getAllDistrictFromApi(id: Int) = apiInterfaceCore.getAllDistrictFromApi(id)

    fun getMerchantCredit(courierUserId: Int) = apiInterfaceCore.getMerchantCredit(courierUserId)

    fun getBreakableCharge() = apiInterfaceCore.getBreakableCharge()

    fun getPackagingCharge() = apiInterfaceCore.getPackagingCharge()

    fun getDeliveryCharge(requestBody: DeliveryChargeRequest) = apiInterfaceCore.getDeliveryCharge(requestBody)

    fun placeOrder(requestBody: OrderRequest) = apiInterfaceCore.placeOrder(requestBody)

    fun placeOrderUpdate(courierOrdersId: String, requestBody: UpdateOrderReqBody) = apiInterfaceCore.placeOrderUpdate(courierOrdersId, requestBody)

    suspend fun fetchCollectionTimeSlot() = apiInterfaceCore.fetchCollectionTimeSlot()

    suspend fun fetchDTOrderGenericLimit() = apiInterfaceCore.fetchDTOrderGenericLimit()

    suspend fun getPickupLocations(courierUserId: Int) = apiInterfaceCore.getPickupLocations(courierUserId)

    suspend fun fetchPickupLocationsWithAcceptedOrderCount(courierUserId: Int) = apiInterfaceCore.fetchPickupLocationsWithAcceptedOrderCount(courierUserId)

    fun updateMerchantInformation(courierOrdersId: Int, requestBody: ProfileUpdateReqBody) = apiInterfaceCore.updateMerchantInformation(courierOrdersId, requestBody)

    suspend fun updatePickupLocations(id: Int, requestBody: PickupLocation) = apiInterfaceCore.updatePickupLocations(id, requestBody)

    suspend fun addPickupLocations(requestBody: PickupLocation) = apiInterfaceCore.addPickupLocations(requestBody)

    suspend fun  deletePickupLocations(id: Int) = apiInterfaceCore.deletePickupLocations(id)

    fun getCollectionCharge(courierUserId: Int) = apiInterfaceCore.getCollectionCharge(courierUserId)

    suspend fun getCourierUsersInformation(courierUserId: Int) = apiInterfaceCore.getCourierUsersInformation(courierUserId)

    suspend fun updateOffer(orderId: Int, requestBody: OfferUpdateRequest) = apiInterfaceCore.updateOffer(orderId, requestBody)

    suspend fun getOrderTrackingList(flag: String, requestBody: OrderTrackReqBody) = apiInterfaceCore.getOrderTrackingList(flag, requestBody)

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

}