package com.bd.deliverytiger.app.repository

import com.bd.deliverytiger.app.api.ApiInterfaceInfobip
import com.bd.deliverytiger.app.api.endpoint.*
import com.bd.deliverytiger.app.api.model.accounts.BankCheckForEftRequest
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceReqBody
import com.bd.deliverytiger.app.api.model.bulk_status.StatusUpdateData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.SpecialServiceRequestBody
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.collector_info.CollectorInfoRequest
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import com.bd.deliverytiger.app.api.model.complain.ComplainListRequest
import com.bd.deliverytiger.app.api.model.complain.ComplainRequest
import com.bd.deliverytiger.app.api.model.complain.general_complain.GeneralComplainListRequest
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.deal_management.DealManagementRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnedCountRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsRequest
import com.bd.deliverytiger.app.api.model.district.DistrictData
import com.bd.deliverytiger.app.api.model.fcm.FCMRequest
import com.bd.deliverytiger.app.api.model.instant_payment_update.UpdatePaymentCycleRequest
import com.bd.deliverytiger.app.api.model.live.auth.AuthRequestBody
import com.bd.deliverytiger.app.api.model.live.auth.SignUpNew
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductInsertData
import com.bd.deliverytiger.app.api.model.live.live_product_insert.ProductGalleryData
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule_insert.LiveScheduleInsertRequest
import com.bd.deliverytiger.app.api.model.live.live_status.LiveStatusUpdateRequest
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSRequest
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInfoRequest
import com.bd.deliverytiger.app.api.model.lead_management.GetLocationInfoRequest
import com.bd.deliverytiger.app.api.model.lead_management.SMSLogData
import com.bd.deliverytiger.app.api.model.lead_management.customer_details.CustomerInfoDetailsRequest
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookData
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookGroupData
import com.bd.deliverytiger.app.api.model.loan_survey.LoanSurveyRequestBody
import com.bd.deliverytiger.app.api.model.loan_survey.SelectedCourierModel
import com.bd.deliverytiger.app.api.model.log_sms.SMSLogRequest
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.api.model.offer.OfferUpdateRequest
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackReqBody
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantInstantPaymentRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceiveableDetailRequest
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsRequest
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderRequest
import com.bd.deliverytiger.app.api.model.quick_order.TimeSlotRequest
import com.bd.deliverytiger.app.api.model.quick_order.TimeSlotUpdateRequest
import com.bd.deliverytiger.app.api.model.quick_order.quick_order_history.QuickOrderListRequest
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionAnswer
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableUpdateRequest
import com.bd.deliverytiger.app.api.model.service_selection.ServiceDistrictsRequest
import com.bd.deliverytiger.app.api.model.sms.SMSModel
import com.bd.deliverytiger.app.api.model.voice_SMS.VoiceSmsAudiRequestBody
import com.bd.deliverytiger.app.api.model.voucher.VoucherCheckRequest
import com.bd.deliverytiger.app.database.AppDatabase
import com.bd.deliverytiger.app.database.dao.DistrictDao
import com.bd.deliverytiger.app.database.dao.NotificationDao
import com.bd.deliverytiger.app.fcm.FCMData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body

class AppRepository(
    private val apiInterfaceADM: ApiInterfaceADM,
    private val apiInterfaceCore: ApiInterfaceCore,
    private val apiInterfaceAPI: ApiInterfaceAPI,
    private val apiInterfaceBridge: ApiInterfaceBRIDGE,
    private val apiInterfaceLambda: ApiInterfaceLambda,
    private val apiInterfaceMerchant: ApiInterfaceMerchant,
    private val apiInterfaceBariKoi: ApiInterfaceBariKoi,
    private val apiInterfaceANA: ApiInterfaceANA,
    private val apiInterfaceFCM: ApiInterfaceFCM,
    private val apiInterfaceINFOBIP: ApiInterfaceInfobip,
    private val database: AppDatabase
) {

    //#region AppDatabase
    private val notificationDao: NotificationDao = database.notificationDao()
    private val districtDao: DistrictDao = database.districtDao()

    //#region NotificationDao
    suspend fun insert(model: FCMData): Long {
        return if (model.uid == 0) {
            notificationDao.upsert(model)
        } else {
            updateNotification(model).toLong()
        }
    }

    suspend fun updateNotification(model: FCMData): Int = notificationDao.updateNotification(model)

    suspend fun getAllNotification() = notificationDao.getAllNotification()

    fun getAllNotificationFlow() = notificationDao.getAllNotificationFlow()

    suspend fun getNotificationById(id: Int) = notificationDao.getNotificationById(id)

    suspend fun deleteNotificationById(id: Int) = notificationDao.deleteNotificationById(id)

    suspend fun deleteAllNotification() = notificationDao.deleteAllNotification()
    //#endregion

    //#region DistrictDao
    suspend fun insert(list: List<DistrictData>): List<Long> {
        return districtDao.insertAll(list)
    }

    suspend fun insert(model: DistrictData): Long {
        return if (model.uid == 0) {
            districtDao.upsert(model)
        } else {
            updateDistrict(model).toLong()
        }
    }

    private suspend fun updateDistrict(model: DistrictData): Int = districtDao.updateDistrict(model)

    suspend fun getAllDistrict() = districtDao.getAllDistrict()

    suspend fun getDistrictById(districtId: Int) = districtDao.getDistrictById(districtId)

    suspend fun getDistrictByParentId(parentId: Int) = districtDao.getDistrictByParentId(parentId)

    fun getDistrictByParentIdFlow(parentId: Int) = districtDao.getDistrictByParentIdFlow(parentId)

    suspend fun deleteDistrictById(id: Int) = districtDao.deleteDistrictById(id)

    suspend fun deleteAllDistrict() = districtDao.deleteAllDistrict()

    suspend fun deleteAndInsert(list: List<DistrictData>) = districtDao.deleteAndInsert(list)
    //#endregion
    //#endregion

    //******************** API ********************//
    suspend fun sendOTP(requestBody: OTPRequestModel) = apiInterfaceAPI.sendOTP(requestBody)

    suspend fun checkOTP(mobileNo: String, OPTCode: String) = apiInterfaceAPI.checkOTP(mobileNo, OPTCode)

    suspend fun uploadProductInfo(ProductUploadReqBody: RequestBody) = apiInterfaceAPI.uploadProductInfo(ProductUploadReqBody)

    suspend fun fetchAllDistricts() = apiInterfaceAPI.fetchAllDistricts()

    suspend fun uploadProductImage(data: RequestBody, file: List<MultipartBody.Part>?) = apiInterfaceAPI.uploadProductImage(data, file)

    //Live CustomerExistsCheck
    suspend fun customerAuthenticationCheck(requestBody: AuthRequestBody) = apiInterfaceAPI.customerAuthenticationCheck(requestBody)

    suspend fun signUpForLivePlaza(requestBody: SignUpNew) = apiInterfaceAPI.signUpForLivePlaza(requestBody)

    // Live
    suspend fun getProductList(
        categoryId: Int, subCategoryId: Int, subSUbCategoryId: Int, routingName: String, index: Int, count: Int
    ) = apiInterfaceAPI.getProductList(categoryId, subCategoryId, subSUbCategoryId, routingName, index, count)


    suspend fun fetchLiveSchedule(requestBody: ScheduleRequest) = apiInterfaceAPI.fetchLiveSchedule(requestBody)

    suspend fun insertLiveSchedule(requestBody: LiveScheduleInsertRequest) = apiInterfaceAPI.insertLiveSchedule(requestBody)

    suspend fun uploadLiveCoverPhoto(requestBody: RequestBody, file1: MultipartBody.Part? = null) = apiInterfaceAPI.uploadLiveCoverPhoto(requestBody, file1)

    suspend fun fetchUserSchedule(customerId: Int, type: String, index: Int, count: Int) = apiInterfaceAPI.fetchUserSchedule(customerId, type, index, count)

    suspend fun fetchUserScheduleReplay(customerId: Int, type: String, index: Int, count: Int) = apiInterfaceAPI.fetchUserScheduleReplay(customerId, type, index, count)

    //SMS
    suspend fun shareSMS(requestBody: SMSRequest) = apiInterfaceAPI.shareSMS(requestBody)

    suspend fun fetchTotalSMSCount(liveId: Int) = apiInterfaceAPI.fetchTotalSMSCount(liveId)

    suspend fun fetchFreeSMSCondition() = apiInterfaceAPI.fetchFreeSMSCondition()

    suspend fun insertLiveProducts(requestBody: List<LiveProductInsertData>) = apiInterfaceAPI.insertLiveProducts(requestBody)

    suspend fun uploadProductPhoto(productId: RequestBody, folderName: RequestBody, files: List<MultipartBody.Part>? = null) = apiInterfaceAPI.uploadProductPhoto(productId, folderName, files)

    suspend fun fetchLiveProducts(requestBody: LiveProductRequest) = apiInterfaceAPI.fetchLiveProducts(requestBody)

    suspend fun updateProductSoldOut(productId: Int, flag: Boolean) = apiInterfaceAPI.updateProductSoldOut(productId, flag)

    suspend fun deleteProductFromLive(productId: Int) = apiInterfaceAPI.deleteProductFromLive(productId)

    suspend fun updateLiveStatus(requestBody: LiveStatusUpdateRequest) = apiInterfaceAPI.updateLiveStatus(requestBody)

    suspend fun insertProductByID(responseBody: List<ProductGalleryData>) = apiInterfaceAPI.insertProductByID(responseBody)

    //******************** ApiInterfaceMerchant ********************//

    suspend fun fetchDealManagementData(requestBody: DealManagementRequest) = apiInterfaceMerchant.fetchDealManagementData(requestBody)

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

    suspend fun fetchWithoutOrderCodeComplains(requestBody: GeneralComplainListRequest) = apiInterfaceADM.fetchWithoutOrderCodeComplains(requestBody)

    suspend fun fetchBillPayHistory(courierUserId: Int) = apiInterfaceADM.fetchBillPayHistory(courierUserId)

    suspend fun fetchUnpaidCOD(courierUserId: Int) = apiInterfaceADM.fetchUnpaidCOD(courierUserId)

    suspend fun updateInstantPaymentRequest(courierUserId: Int) = apiInterfaceADM.updateInstantPaymentRequest(courierUserId)

    suspend fun getInstantPaymentRate() = apiInterfaceADM.getInstantPaymentRate()

    suspend fun getEftPaymentRate() = apiInterfaceADM.getEftPaymentRate()

    suspend fun getSuperEftPaymentRate() = apiInterfaceADM.getSuperEftPaymentRate()

    suspend fun fetchMerchantCurrentAdvanceBalance(courierUserId: Int) = apiInterfaceADM.fetchMerchantCurrentAdvanceBalance(courierUserId)

    suspend fun fetchDTMerchantInstantPaymentStatus(courierUserId: Int) = apiInterfaceADM.fetchDTMerchantInstantPaymentStatus(courierUserId)

    suspend fun getInstantPaymentActivationStatus(courierUserId: Int) = apiInterfaceADM.getInstantPaymentActivationStatus(courierUserId)

    suspend fun getComplainHistory(bookingCode: Int, isVisibleToMerchant: Int) = apiInterfaceADM.getComplainHistory(bookingCode, isVisibleToMerchant)

    suspend fun merchantBalanceLoadHistory(merchantID: Int) = apiInterfaceADM.merchantBalanceLoadHistory(merchantID)

    suspend fun merchantPayableReceiveableDetailForInstantPayment(requestBody: MerchantPayableReceiveableDetailRequest) = apiInterfaceADM.merchantPayableReceiveableDetailForInstantPayment(requestBody)

    suspend fun instantOr24hourPayment(requestBody: MerchantInstantPaymentRequest) = apiInterfaceADM.instantOr24hourPayment(requestBody)

    suspend fun getMessageAlertForIP() = apiInterfaceADM.getMessageAlertForIP()

    suspend fun checkBankNameForEFT(requestBody: BankCheckForEftRequest) = apiInterfaceADM.checkBankNameForEFT(requestBody)

    suspend fun fetchCourierList() = apiInterfaceCore.fetchCourierList()

    suspend fun submitLoanSurvey(requestBody: LoanSurveyRequestBody) = apiInterfaceCore.submitLoanSurvey(requestBody)

    suspend fun getLoanSurveyByCourierUser(courierUserId: Int) = apiInterfaceCore.getLoanSurveyByCourierUser(courierUserId)

    suspend fun submitCourierList(requestBody: List<SelectedCourierModel>) = apiInterfaceCore.submitCourierList(requestBody)

    suspend fun saveCustomerSMSLog(requestBody: List<SMSLogData>) = apiInterfaceCore.saveCustomerSMSLog(requestBody)

    suspend fun addToOwnPhoneBook(requestBody: List<PhonebookData>) = apiInterfaceCore.addToOwnPhoneBook(requestBody)

    suspend fun createPhoneBookGroup(requestBody: List<PhonebookGroupData>) = apiInterfaceCore.createPhoneBookGroup(requestBody)

    suspend fun fetchMyPhoneBookGroup(courierUserId: Int) = apiInterfaceCore.fetchMyPhoneBookGroup(courierUserId)

    suspend fun addToOwnPhoneBookGroup(requestBody: List<PhonebookData>) = apiInterfaceCore.addToOwnPhoneBookGroup(requestBody)

    suspend fun imageUploadForFile(
        fileName: RequestBody,
        imagePath: RequestBody,
        file: MultipartBody.Part?
    ) = apiInterfaceADM.imageUploadForFile(fileName, imagePath, file)

    //******************** ADCORE ********************//

    suspend fun getBannerInfo() = apiInterfaceCore.getBannerInfo()

    suspend fun getDashboardStatusGroup(requestBody: DashBoardReqBody) = apiInterfaceCore.getDashboardStatusGroup(requestBody)

    suspend fun loadAllDistrictsById(id: Int) = apiInterfaceCore.loadAllDistrictsById(id)

    suspend fun loadAllDistricts() = apiInterfaceCore.loadAllDistricts()

    suspend fun loadAllDistrictsByIdList(requestBody: List<GetLocationInfoRequest>) = apiInterfaceCore.loadAllDistrictsByIdList(requestBody)

    suspend fun fetchServiceDistricts(requestBody: ServiceDistrictsRequest) = apiInterfaceCore.fetchServiceDistricts(requestBody)

    suspend fun getDTService() = apiInterfaceCore.getDTService()

    fun getMerchantCredit(courierUserId: Int) = apiInterfaceCore.getMerchantCredit(courierUserId)

    suspend fun getBreakableCharge() = apiInterfaceCore.getBreakableCharge()

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

    suspend fun getRidersOfficeInfo(requestBody: CollectorInfoRequest) = apiInterfaceCore.getRidersOfficeInfo(requestBody)

    suspend fun updateOrderInfo(orderId: String, requestBody: UpdateOrderReqBody) = apiInterfaceCore.updateOrderInfo(orderId, requestBody)

    suspend fun updateCourierStatusDT(requestBody: StatusLocationRequest) = apiInterfaceCore.updateCourierStatusDT(requestBody)

    suspend fun fetchCustomerList(requestBody: CustomerInfoRequest) = apiInterfaceCore.fetchCustomerList(requestBody)

    suspend fun fetchCustomerDetailsList(requestBody: CustomerInfoDetailsRequest) = apiInterfaceCore.fetchCustomerDetailsList(requestBody)

    suspend fun fetchAcceptedCourierOrders(courierUserId: Int) = apiInterfaceCore.fetchAcceptedCourierOrders(courierUserId)

    suspend fun getCustomerInfoByMobile(mobile: String) = apiInterfaceCore.getCustomerInfoByMobile(mobile)

    suspend fun updateCustomerSMSLimit(
        courierUserId: Int,
        customerSMSLimit: Int
    ) = apiInterfaceCore.updateCustomerSMSLimit(courierUserId, customerSMSLimit)

    suspend fun updateCustomerVoiceSmsLimit(
        courierUserId: Int,
        customerSMSLimit: Int
    ) = apiInterfaceCore.updateCustomerVoiceSmsLimit(courierUserId, customerSMSLimit)

    suspend fun sendSMSCommunication(requestBody: List<SMSModel>) = apiInterfaceCore.sendSMSCommunication(requestBody)

    suspend fun updateBulkStatus(requestBody: List<StatusUpdateData>) = apiInterfaceCore.updateBulkStatus(requestBody)

    suspend fun fetchCategory() = apiInterfaceCore.fetchCategory()

    suspend fun fetchSubCategoryById(categoryId: Int) = apiInterfaceCore.fetchSubCategoryById(categoryId)

    //Quick Order
    suspend fun getCollectionTimeSlot(requestBody: TimeSlotRequest) = apiInterfaceCore.getCollectionTimeSlot(requestBody)

    suspend fun quickOrderRequest(requestBody: QuickOrderRequest) = apiInterfaceCore.quickOrderRequest(requestBody)

    suspend fun getMerchantQuickOrders(requestBody: QuickOrderListRequest) = apiInterfaceCore.getMerchantQuickOrders(requestBody)

    suspend fun updateMultipleTimeSlot(requestBody: List<TimeSlotUpdateRequest>) = apiInterfaceCore.updateMultipleTimeSlot(requestBody)

    suspend fun deleteOrderRequest(orderRequestId: Int) = apiInterfaceCore.deleteOrderRequest(orderRequestId)

    suspend fun sendPushNotifications(authToken: String, @Body requestBody: FCMRequest) = apiInterfaceFCM.sendPushNotifications(authToken, requestBody)

    //#region Infobip Voice SMS

    suspend fun sendVoiceSms(requestBody: VoiceSmsAudiRequestBody) = apiInterfaceINFOBIP.sendVoiceSms(requestBody)

    //#endregion

    //#region Voucher

    suspend fun checkVoucher(requestBody: VoucherCheckRequest) = apiInterfaceCore.checkVoucher(requestBody)

    suspend fun getPreviousLoanSurveyResponse(courierUserId: Int) = apiInterfaceCore.previousLoanSurveyResponse(courierUserId)

    //#endregion

    suspend fun updateCourierWithLoanSurvey(requestBody: List<SelectedCourierModel>, loanSurveyId: Int) = apiInterfaceCore.updateCourierWithLoanSurvey(requestBody, loanSurveyId)

    suspend fun updateLoanSurvey(loanSurveyId: Int, requestBody: LoanSurveyRequestBody) = apiInterfaceCore.updateLoanSurvey(loanSurveyId, requestBody)

    suspend fun fetchSpecialService(requestBody: SpecialServiceRequestBody) = apiInterfaceCore.fetchSpecialService(requestBody)

}