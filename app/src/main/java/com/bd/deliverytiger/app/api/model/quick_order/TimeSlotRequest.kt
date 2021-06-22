package com.bd.deliverytiger.app.api.model.quick_order


import com.google.gson.annotations.SerializedName

data class TimeSlotRequest(
    @SerializedName("requestDate")
    var requestDate: String? = ""
)