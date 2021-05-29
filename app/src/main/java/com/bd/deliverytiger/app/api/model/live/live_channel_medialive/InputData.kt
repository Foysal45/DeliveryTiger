package com.bd.deliverytiger.app.api.model.live.live_channel_medialive

import com.google.gson.annotations.SerializedName

data class InputData(
    @SerializedName("Port")
    var port: String? = "",
    @SerializedName("Ip")
    var ip: String? = "",
    @SerializedName("Url")
    var url: String? = ""
)