package com.bd.deliverytiger.app.api.model.order


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderPreviewData(
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("district")
    var district: String? = "",
    @SerializedName("thana")
    var thana: String? = "",
    @SerializedName("codCharge")
    var codCharge: String? = "",
    var isCollection: Boolean = false

) : Parcelable