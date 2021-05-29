package com.bd.deliverytiger.app.api.model.live.live_schedule_insert

import com.google.gson.annotations.SerializedName

data class LiveScheduleInsertRequest(

    @SerializedName("LiveDate")
    var liveDate: String? = "",
    @SerializedName("FromTime")
    var fromTime: String? = "",
    @SerializedName("ToTime")
    var toTime: String? = "",
    @SerializedName("ChannelId")
    var channelId: Int = 0,
    @SerializedName("ChannelType")
    var channelType: String? = "",
    @SerializedName("IsActive")
    var isActive: Int = 1,
    @SerializedName("InsertedBy")
    var insertedBy: Int = 0,
    @SerializedName("ScheduleId")
    var scheduleId: Int = 0,
    @SerializedName("LiveTitle")
    var liveTitle: String? = "",
    @SerializedName("SuggestedPrice")
    var suggestedPrice: String? = "",
    @SerializedName("PaymentMode")
    var paymentMode: String? = "",

    @SerializedName("RedirectToFB")
    var redirectToFB: Boolean? = false,
    @SerializedName("IsShowMobile")
    var isShowMobile: Boolean? = false,
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("AlternativeMobile")
    var alternativeMobile: String? = "",

    @SerializedName("FacebookPageUrl")
    var fbPageUrl: String? = "",
    @SerializedName("Facebook")
    var facebook: Boolean = false,
    @SerializedName("FacebookUrl")
    var facebookUrl: String? = "",
    @SerializedName("FacebookStream")
    var facebookStream: String? = "",

    @SerializedName("Youtube")
    var youtube: Boolean = false,
    @SerializedName("YoutubeUrl")
    var youtubeUrl: String? = "",
    @SerializedName("YoutubeStream")
    var youtubeStream: String? = "",
    @SerializedName("IsInstantLive")
    var isInstantLive: Int = 0
)