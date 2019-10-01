package com.bd.deliverytiger.app.api.model.status


import com.google.gson.annotations.SerializedName

data class StatusModel(
    @SerializedName("statusId")
    var statusId: Int = 0,
    @SerializedName("statusNameEng")
    var statusNameEng: String? = "",
    @SerializedName("statusNameBng")
    var statusNameBng: String? = "",
    @SerializedName("statusGroup")
    var statusGroup: String? = "",
    @SerializedName("fulfillmentStatusGroup")
    var fulfillmentStatusGroup: String? = "",
    @SerializedName("orderTrackStatusGroup")
    var orderTrackStatusGroup: String? = "",
    @SerializedName("orderTrackStatusPublicGroup")
    var orderTrackStatusPublicGroup: String? = "",
    @SerializedName("statusType")
    var statusType: String? = "",
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("postedOn")
    var postedOn: String? = "",
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("customerMessage")
    var customerMessage: String? = "",
    @SerializedName("customerEmail")
    var customerEmail: String? = ""
)