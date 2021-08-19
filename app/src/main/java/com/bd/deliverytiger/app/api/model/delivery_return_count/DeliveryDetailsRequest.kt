package com.bd.deliverytiger.app.api.model.delivery_return_count

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryDetailsRequest(
        @SerializedName("FromDate")
        var fromDate: String = "",
        @SerializedName("ToDate")
        var toDate: String = "",
        @SerializedName("MerchantId")
        var merchantId: Int = 0,
        @SerializedName("Type")
        var type: String = ""
): Parcelable
