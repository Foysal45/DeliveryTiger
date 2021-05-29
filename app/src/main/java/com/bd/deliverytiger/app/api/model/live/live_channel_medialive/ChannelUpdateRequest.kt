package com.bd.deliverytiger.app.api.model.live.live_channel_medialive

import com.google.gson.annotations.SerializedName

data class ChannelUpdateRequest(
    @SerializedName("merchantid")
    var merchantid: String? = "",
    @SerializedName("liveid")
    var liveid: String? = "",
    @SerializedName("livename")
    var livename: String? = "",
    @SerializedName("facebook")
    var facebook: Boolean = false,
    @SerializedName("youtube")
    var youtube: Boolean = false,
    @SerializedName("facebookurl")
    var facebookurl: String? = "",
    @SerializedName("facebookstream")
    var facebookstream: String? = "",
    @SerializedName("youtubeurl")
    var youtubeurl: String? = "",
    @SerializedName("youtubestream")
    var youtubestream: String? = "",
    @SerializedName("input")
    var input: String? = "", // start stop
    @SerializedName("channelid")
    var channelId: String? = "",
    @SerializedName("region")
    var region: String = "", // 1 2
    @SerializedName("emergency")
    var emergency: Boolean = false,
    @SerializedName("bitrateInKB")
    var bitrateInKB: String = "400",
    @SerializedName("endtime")
    var endTime: String = ""
)