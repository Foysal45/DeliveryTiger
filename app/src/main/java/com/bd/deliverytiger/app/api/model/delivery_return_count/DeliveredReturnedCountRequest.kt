package com.bd.deliverytiger.app.api.model.delivery_return_count

import com.google.gson.annotations.SerializedName

data class DeliveredReturnedCountRequest(

        @SerializedName("FromDate")
        var fromDate: String = "",
        @SerializedName("ToDate")
        var toDate: String = "",
        @SerializedName("CourierUserId")
        var courierUserId: Int = 0,
)
