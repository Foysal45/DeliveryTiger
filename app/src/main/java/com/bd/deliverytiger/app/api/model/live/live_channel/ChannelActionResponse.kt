package com.bd.deliverytiger.app.api.model.live.live_channel

import com.google.gson.annotations.SerializedName

data class ChannelActionResponse(
    @SerializedName("status")
    var status: Boolean = false,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("archiveurl")
    var archiveUrl: String? = ""
)