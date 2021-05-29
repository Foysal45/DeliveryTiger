package com.bd.deliverytiger.app.api.model.live.live_product_list

import com.google.gson.annotations.SerializedName

data class LiveProductRequest(
    @SerializedName("LiveId")
    var liveId: Int = 0,
    @SerializedName("Flag")
    var flag: Int = 0 // 1 means only Sold Out product load, 0 means All product load
)