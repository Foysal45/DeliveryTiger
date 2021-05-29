package com.bd.deliverytiger.app.api.model.live.live_status

import com.google.gson.annotations.SerializedName

data class LiveStatusUpdateRequest(
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("StatusName")
    var statusName: String? = "",
    @SerializedName("LiveVideoUrl")
    var liveVideoUrl: String? = ""
)