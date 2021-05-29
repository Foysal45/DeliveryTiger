package com.bd.deliverytiger.app.api.model.live.live_started_notify

import com.google.gson.annotations.SerializedName

data class LiveStartedNotifyResponse(
    @SerializedName("ChannelId")
    var channelId: Int = 0,
    @SerializedName("TotalFollowers")
    var totalFollowers: Int = 0,
    @SerializedName("TotalNotified")
    var totalNotified: Int = 0,
    @SerializedName("TotalFailureToNotify")
    var totalFailureToNotify: Int = 0
)