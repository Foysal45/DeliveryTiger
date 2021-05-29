package com.bd.deliverytiger.app.api.model.live

import com.google.gson.annotations.SerializedName

data class DateData(
    @SerializedName("date")
    var date: Int = 0,
    @SerializedName("month")
    var month: Int = 0,
    @SerializedName("year")
    var year: Int = 0,
    @SerializedName("monthName")
    var monthName: String = "",
    @SerializedName("dateName")
    var dateName: String = "",
    @SerializedName("formattedDate")
    var formattedDate: String = "",
)