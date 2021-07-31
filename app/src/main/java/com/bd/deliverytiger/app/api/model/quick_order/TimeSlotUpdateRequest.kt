package com.bd.deliverytiger.app.api.model.quick_order


import com.google.gson.annotations.SerializedName

data class TimeSlotUpdateRequest(
    @SerializedName("orderRequestId")
    var orderRequestId: Int = 0,
    @SerializedName("collectionTimeSlotId")
    var collectionTimeSlotId: Int = 0
)