package com.bd.deliverytiger.app.repository

import com.bd.deliverytiger.app.api.`interface`.ApiInterfaceADM
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest

class AppRepository(private val apiInterface: ApiInterfaceADM) {

    suspend fun getMerchantMonthlyReceivable(requestBody: MonthlyReceivableRequest) = apiInterface.getMerchantMonthlyReceivable(requestBody)
}