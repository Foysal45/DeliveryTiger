package com.bd.deliverytiger.app.api.model.service_bill_pay


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderCode(
    @SerializedName("OrderCode")
    var orderCode: String = "",
    @SerializedName("CollectedAmount")
    var collectedAmount: Int = 0,
    @SerializedName("TransactionNo")
    var transactionNo: String = ""
) : Parcelable