package com.bd.deliverytiger.app.api.model.live.share_sms

import com.google.gson.annotations.SerializedName

data class SMSRequest(
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("TotalSMS")
    var totalSMS: Int = 0,
    @SerializedName("ChannelId")
    var channelId: Int = 0,
    @SerializedName("LiveShoppingShareSMSModels")
    var liveShoppingShareSMSModels: List<SMSBody> = listOf()
)