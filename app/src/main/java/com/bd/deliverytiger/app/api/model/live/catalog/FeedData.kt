package com.bd.deliverytiger.app.api.model.live.catalog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeedData(
    var name: String = "",
    var url: String = "",
    var description: String = ""
) : Parcelable