package com.bd.deliverytiger.app.api.model.bulk_status


import com.google.gson.annotations.SerializedName

data class StatusUpdateData(
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("comment")
    var comment: String = "",
    @SerializedName("updatedBy")
    var updatedBy: Int = 0,
    @SerializedName("courierOrdersId")
    var courierOrdersId: String = "",
    @SerializedName("reAttemptCharge")
    var reAttemptCharge: Float = 0f,
    @SerializedName("isConfirmedBy")
    var isConfirmedBy: String = "merchantapp",
    @SerializedName("hubName")
    var hubName: String = ""
)