package com.bd.deliverytiger.app.api.model.payment_receieve


import com.google.gson.annotations.SerializedName

data class MerchantPayableReceivableDetailResponse(
    @SerializedName("BKashStatus")
    val bKashStatus: Int = 0,
    @SerializedName("BankStatus")
    val bankStatus: Int = 0,
    @SerializedName("NagadNoStatus")
    val nagadNoStatus: Int = 0,
    @SerializedName("Limit")
    val limit: Int = 0,
    @SerializedName("ExpressTime")
    val expressTime: String = "",
    @SerializedName("ExpressCharge")
    val expressCharge: Int = 0,
    @SerializedName("ExpressNetPayableAmount")
    val expressNetPayableAmount: Int = 0,
    @SerializedName("InstantPaymentCharge")
    val instantPaymentCharge: Int = 0,
    @SerializedName("NetPayableAmount")
    val netPayableAmount: Int = 0,
    @SerializedName("NormalTime")
    val normalTime: String = "",
    @SerializedName("BankACNo")
    val bankACNo: String = "",
    @SerializedName("BankName")
    val bankName: String = "",
    @SerializedName("BKashNo")
    val bKashNo: String = "",
    @SerializedName("NagadNo")
    val nagadNo: String = "",
    @SerializedName("BkashTime")
    val bkashTime: String = "",
    @SerializedName("OptionImageUrl")
    val optionImageUrl: List<OptionImageUrl> = listOf(),
    @SerializedName("PayableAmount")
    val payableAmount: Int = 0
)