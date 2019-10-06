package com.bd.deliverytiger.app.api.model.dashboard


import com.google.gson.annotations.SerializedName

data class DashBoardReqBody(
    @SerializedName("month")
    var month: Int? = 0,
    @SerializedName("year")
    var year: Int? = 0,
    @SerializedName("courierUserId")
    var courierUserId: Int? = 0
)