package com.bd.deliverytiger.app.api.model.notification


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FCMNotification(
    @SerializedName("notificationType")
    var notificationType: String? = "0",
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("bigText")
    var bigText: String? = "",
    @SerializedName("imageLink")
    var imageLink: String? = ""
): Parcelable