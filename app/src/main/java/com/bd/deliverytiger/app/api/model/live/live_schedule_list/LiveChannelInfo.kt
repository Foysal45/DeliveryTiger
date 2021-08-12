package com.bd.deliverytiger.app.api.model.live.live_schedule_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveChannelInfo(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Input")
    var input: String? = ""
): Parcelable