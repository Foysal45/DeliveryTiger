package com.bd.deliverytiger.app.api.model.fcm


import com.google.gson.annotations.SerializedName

data class FCMResponse (
    @SerializedName("multicast_id")
    var multicastId: Long = 0,
    @SerializedName("success")
    var success: Int = 0,
    @SerializedName("failure")
    var failure: Int = 0,
    @SerializedName("canonical_ids")
    var canonicalIds: Int = 0,
    @SerializedName("results")
    var results: List<FCMResult>? = listOf()
)