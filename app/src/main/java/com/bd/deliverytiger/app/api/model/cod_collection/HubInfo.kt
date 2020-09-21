package com.bd.deliverytiger.app.api.model.cod_collection


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HubInfo(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("value")
    var value: String? = "",
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("hubAddress")
    var hubAddress: String? = "",
    @SerializedName("longitude")
    var longitude: String? = "",
    @SerializedName("latitude")
    var latitude: String? = "",
    @SerializedName("hubMobile")
    var hubMobile: String? = ""
) : Parcelable