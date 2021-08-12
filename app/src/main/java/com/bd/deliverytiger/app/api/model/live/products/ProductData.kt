package com.bd.deliverytiger.app.api.model.live.products

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductData(
    @SerializedName("RegularPrice")
    var regularPrice: Int = 0,
    @SerializedName("DealDiscountInPercent")
    var dealDiscountInPercent: Double = 0.0,
    @SerializedName("DealId")
    var dealId: Int = 0,
    @SerializedName("DealTitle")
    var dealTitle: String? = "",
    @SerializedName("DealPrice")
    var dealPrice: Int = 0,
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("ImageLink")
    var imageLink: String? = ""
) : Parcelable