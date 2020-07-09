package com.bd.deliverytiger.app.fcm


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FCMData(
    @SerializedName("notificationType")
    var notificationType: String? = "",
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("imageUrl")
    var imageUrl: String? = "",
    @SerializedName("productImage")
    var productImage: String? = "",
    @SerializedName("bigText")
    var bigText: String? = "",
    @SerializedName("serviceType")
    var serviceType: String? = ""
): Parcelable