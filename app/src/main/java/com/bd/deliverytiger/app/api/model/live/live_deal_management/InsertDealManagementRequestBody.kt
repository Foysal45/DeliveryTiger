package com.bd.deliverytiger.app.api.model.live.live_deal_management

import com.google.gson.annotations.SerializedName

data class InsertDealManagementRequestBody(
    @SerializedName("DealId")
    var dealId: Int = 0,
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("ChannelId")
    var channelId: Int = 0
)