package com.bd.deliverytiger.app.api.model.live.live_schedule

import com.google.gson.annotations.SerializedName

data class ScheduleData(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("TimeBookingCount")
    var timeBookingCount: Int = 0,
    @SerializedName("IsTimeActive")
    var isTimeActive: Int = 0,
    @SerializedName("FromScheduleTime")
    var fromScheduleTime: String? = "",
    @SerializedName("ToScheduleTime")
    var toScheduleTime: String? = ""
)