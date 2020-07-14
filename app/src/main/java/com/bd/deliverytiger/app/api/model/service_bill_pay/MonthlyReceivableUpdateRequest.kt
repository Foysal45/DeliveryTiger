package com.bd.deliverytiger.app.api.model.service_bill_pay


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MonthlyReceivableUpdateRequest(
    @SerializedName("JDate")
    var transactionDate: String? = "",
    @SerializedName("JDescription")
    var transactionId: String? = "",
    @SerializedName("OrderCodes")
    var orderCodes: List<OrderCode> = listOf(),
    @SerializedName("UserId")
    var userId: Int = 3013,
    @SerializedName("LedgerId")
    var ledgerId: Int = 8053,
    @SerializedName("Type")
    var type: String = "M"
) : Parcelable