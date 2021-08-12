package com.bd.deliverytiger.app.api.model.live.live_schedule_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MyLiveSchedule(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("LiveDate")
    var liveDate: String? = "",
    @SerializedName("FromTime")
    var fromTime: String? = "",
    @SerializedName("ToTime")
    var toTime: String? = "",
    @SerializedName("ScheduleId")
    var scheduleId: Int = 0,
    @SerializedName("LiveTitle")
    var liveTitle: String? = "",
    @SerializedName("CoverPhoto")
    var coverPhoto: String? = "",
    @SerializedName("LiveChannelName")
    var liveChannelName: String? = "",
    @SerializedName("SuggestedPrice")
    var suggestedPrice: String? = "",
    @SerializedName("LiveChannelUrl")
    var liveChannelUrl: LiveChannelInfo = LiveChannelInfo(),

    @SerializedName("PaymentMode")
    var paymentMode: String? = "",

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
    @SerializedName("LiveStatus")
    var liveStatus: String? = "",
    @SerializedName("TotalCount")
    var totalCount: Int = 0,
    @SerializedName("TotalOrderCount")
    var totalOrderCount: Int = 0

): Parcelable