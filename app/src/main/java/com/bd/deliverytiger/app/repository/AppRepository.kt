package com.bd.deliverytiger.app.repository

import com.bd.deliverytiger.app.api.endpoint.ApiInterfaceADM
import com.bd.deliverytiger.app.api.endpoint.ApiInterfaceAPI
import com.bd.deliverytiger.app.api.endpoint.ApiInterfaceCore
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.collector_status.StatusLocationRequest
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableUpdateRequest

class AppRepository(private val apiInterfaceADM: ApiInterfaceADM, private val apiInterfaceCore: ApiInterfaceCore, private val apiInterfaceAPI: ApiInterfaceAPI) {

    //******************** API ********************//

    fun updateCourierStatus(requestBody: StatusLocationRequest) = apiInterfaceAPI.updateCourierStatus(requestBody)

    //******************** ADM ********************//

    suspend fun getMerchantMonthlyReceivable(requestBody: MonthlyReceivableRequest) = apiInterfaceADM.getMerchantMonthlyReceivable(requestBody)

    suspend fun bulkMerchantCashCollection(requestBody: MonthlyReceivableUpdateRequest) = apiInterfaceADM.bulkMerchantCashCollection(requestBody)

    suspend fun getPaymentHistory(courierUserId: Int) = apiInterfaceADM.getPaymentHistory(courierUserId)

    suspend fun getPaymentHistoryDetails(courierUserId: Int, transactionId: String) = apiInterfaceADM.getPaymentHistoryDetails(courierUserId, transactionId)

    //******************** ADCORE ********************//

    suspend fun getBannerInfo() = apiInterfaceCore.getBannerInfo()

    fun getDashboardStatusGroup(requestBody: DashBoardReqBody) = apiInterfaceCore.getDashboardStatusGroup(requestBody)

    fun getAllDistrictFromApi(id: Int) = apiInterfaceCore.getAllDistrictFromApi(id)

    fun getMerchantCredit(courierUserId: Int) = apiInterfaceCore.getMerchantCredit(courierUserId)

    fun getBreakableCharge() = apiInterfaceCore.getBreakableCharge()

    fun getPackagingCharge() = apiInterfaceCore.getPackagingCharge()

    fun getDeliveryCharge(requestBody: DeliveryChargeRequest) = apiInterfaceCore.getDeliveryCharge(requestBody)

    fun placeOrder(requestBody: OrderRequest) = apiInterfaceCore.placeOrder(requestBody)

    fun placeOrderUpdate(courierOrdersId: String, requestBody: UpdateOrderReqBody) = apiInterfaceCore.placeOrderUpdate(courierOrdersId,requestBody)

    fun getPickupLocations(courierUserId: Int) = apiInterfaceCore.getPickupLocations(courierUserId)

    fun updateMerchantInformation(courierOrdersId: Int, requestBody: ProfileUpdateReqBody) = apiInterfaceCore.updateMerchantInformation(courierOrdersId, requestBody)

    fun updatePickupLocations(id: Int, requestBody: PickupLocation) = apiInterfaceCore.updatePickupLocations(id, requestBody)

    fun getCollectionCharge(courierUserId: Int) = apiInterfaceCore.getCollectionCharge(courierUserId)
}