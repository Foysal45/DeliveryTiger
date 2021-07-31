package com.bd.deliverytiger.app.api.model.quick_order


import com.google.gson.annotations.SerializedName

data class QuickOrderRequest(
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("requestOrderAmount")
    var requestOrderAmount: Int = 0,
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("collectionDate")
    var collectionDate: String = "",
    @SerializedName("collectionTimeSlotId")
    var collectionTimeSlotId:  Int = 0
)