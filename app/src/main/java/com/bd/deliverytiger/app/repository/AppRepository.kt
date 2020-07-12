package com.bd.deliverytiger.app.repository

import com.bd.deliverytiger.app.api.`interface`.ApiInterfaceADM
import com.bd.deliverytiger.app.api.`interface`.ApiInterfaceCore
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest

class AppRepository(private val apiInterfaceADM: ApiInterfaceADM, private val apiInterfaceCore: ApiInterfaceCore) {

    //******************** ADM ********************//

    suspend fun getMerchantMonthlyReceivable(requestBody: MonthlyReceivableRequest) = apiInterfaceADM.getMerchantMonthlyReceivable(requestBody)

    suspend fun getPaymentHistory(courierUserId: Int) = apiInterfaceADM.getPaymentHistory(courierUserId)

    suspend fun getPaymentHistoryDetails(transactionId: String) = apiInterfaceADM.getPaymentHistoryDetails(transactionId)

    //******************** ADCORE ********************//

    fun getDashboardStatusGroup(requestBody: DashBoardReqBody) = apiInterfaceCore.getDashboardStatusGroup(requestBody)

    fun getAllDistrictFromApi(id: Int) = apiInterfaceCore.getAllDistrictFromApi(id)

    fun getMerchantCredit(courierUserId: Int) = apiInterfaceCore.getMerchantCredit(courierUserId)


}