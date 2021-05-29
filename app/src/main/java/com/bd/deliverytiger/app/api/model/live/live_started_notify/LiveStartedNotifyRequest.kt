package com.bd.deliverytiger.app.api.model.live.live_started_notify

import com.google.gson.annotations.SerializedName

data class LiveStartedNotifyRequest(
    @SerializedName("ChannelId")
    var channelId: Int = 0,
    @SerializedName("LiveId")
    var liveId: String? = "0",
    @SerializedName("LiveType")
    var liveType: String? = "0",
    @SerializedName("notificationType")
    var notificationType: String? = "30",
    @SerializedName("title")
    var title: String? = "আমি এখন একটা লাইভ শপিং সেশন দেখছি",
    @SerializedName("body")
    var body: String? = "লাইভটি আমার সাথে দেখার জন্য ক্লিক করুন",
    @SerializedName("image")
    var image: String? = ""
)