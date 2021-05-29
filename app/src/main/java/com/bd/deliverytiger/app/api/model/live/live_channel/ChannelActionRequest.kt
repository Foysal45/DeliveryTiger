package com.bd.deliverytiger.app.api.model.live.live_channel

import com.google.gson.annotations.SerializedName

data class ChannelActionRequest(
    @SerializedName("channelid")
    var channelid: String,
    @SerializedName("input")
    var input: String, // start stop
    @SerializedName("merchantid")
    var merchantid: String,
    @SerializedName("liveid")
    var liveid: String,
    @SerializedName("livename")
    var livename: String
)