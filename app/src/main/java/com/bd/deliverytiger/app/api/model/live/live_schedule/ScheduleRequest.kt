package com.bd.deliverytiger.app.api.model.live.live_schedule

import com.google.gson.annotations.SerializedName

data class ScheduleRequest(
    @SerializedName("RequestDate")
    var requestDate: String? = ""
)