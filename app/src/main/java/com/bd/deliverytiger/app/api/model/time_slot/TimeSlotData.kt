package com.bd.deliverytiger.app.api.model.time_slot


import com.google.gson.annotations.SerializedName

data class TimeSlotData(
    @SerializedName("collectionTimeSlotId")
    var collectionTimeSlotId: Int = 0,
    @SerializedName("startTime")
    var startTime: String? = "",
    @SerializedName("endTime")
    var endTime: String? = "",
    @SerializedName("ordering")
    var ordering: Int = 0,
    @SerializedName("isActive")
    var isActive: Boolean = false
)