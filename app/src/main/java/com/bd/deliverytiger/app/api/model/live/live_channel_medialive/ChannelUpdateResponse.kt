package com.bd.deliverytiger.app.api.model.live.live_channel_medialive

import com.google.gson.annotations.SerializedName

data class ChannelUpdateResponse(
    @SerializedName("status")
    var status: Boolean = false,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("channelid")
    var channelId: String? = "",
    @SerializedName("cloudfront")
    var cloudfront: String? = "",
    @SerializedName("archiveurl")
    var archiveUrl: String? = "",
    @SerializedName("inputurl")
    var inputData: List<InputData>? = listOf()
)