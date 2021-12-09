package com.bd.deliverytiger.app.api.model.instant_payment_rate


import com.google.gson.annotations.SerializedName

data class AllAlertMessage(
    @SerializedName("BankTransferLimitAlert")
    var bankTransferLimitAlert: String = "",
    @SerializedName("BankTransferMinimumLimit")
    var bankTransferMinimumLimit: String = "",
    @SerializedName("EftChargeTitle")
    var eftChargeTitle: String = "",
    @SerializedName("ExpressTimeOverAlert")
    var expressTimeOverAlert: String = "",
    @SerializedName("FridayAlert")
    var fridayAlert: String = "",
    @SerializedName("InstantChargeTitle")
    var instantChargeTitle: String = "",
    @SerializedName("InstantPaymentLimitAlert")
    var instantPaymentLimitAlert: String = "",
    @SerializedName("InsufficiantBalanceAlert")
    var insufficiantBalanceAlert: String = "",
    @SerializedName("SuperEftChargeTitle")
    var superEftChargeTitle: String = "",
    @SerializedName("TransectionIdNullAlert")
    var transectionIdNullAlert: String = ""
)