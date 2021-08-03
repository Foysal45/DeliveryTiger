package com.bd.deliverytiger.app.api.model.quick_order


import com.google.gson.annotations.SerializedName

data class QuickOrderTimeSlotData(
    @SerializedName("collectionTimeSlotId")
    var collectionTimeSlotId: Int = 0,
    @SerializedName("startTime")
    var startTime: String? = "",
    @SerializedName("endTime")
    var endTime: String? = "",
    @SerializedName("ordering")
    var ordering: Int = 0,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("orderLimit")
    var orderLimit: Int = 0,
    @SerializedName("cutOffTime")
    var cutOffTime: String? = "",
    @SerializedName("formattingStartTime")
    var formattingStartTime: String? = "",
    @SerializedName("formattingEndTime")
    var formattingEndTime: String? = "",
    @SerializedName("formattingCutOffTime")
    var formattingCutOffTime: String? = "",
    @SerializedName("slotName")
    var slotName: String? = ""
)