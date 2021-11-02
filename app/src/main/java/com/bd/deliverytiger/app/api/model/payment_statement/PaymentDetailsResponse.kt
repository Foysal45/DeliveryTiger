package com.bd.deliverytiger.app.api.model.payment_statement


import com.google.gson.annotations.SerializedName

data class PaymentDetailsResponse(
    @SerializedName("NetPaidAmount")
    var netPaidAmount: Int = 0,
    @SerializedName("TotalAdOrderCount")
    var totalAdOrderCount: Int = 0,
    @SerializedName("TotalAdvanceReceivableCount")
    var totalAdvanceReceivableCount: Int = 0,
    @SerializedName("TotalCrOrderCount")
    var totalCrOrderCount: Int = 0,
    @SerializedName("TotalOrderCount")
    var totalOrderCount: Int = 0,
    @SerializedName("NetTotalCharge")
    var netTotalCharge: Int = 0,
    @SerializedName("NetCollectionCharge")
    var netCollectionCharge: Int = 0,
    @SerializedName("NetPackagingCharge")
    var netPackagingCharge: Int = 0,
    @SerializedName("NetReturnCharge")
    var netReturnCharge: Int = 0,
    @SerializedName("NetBreakableCharge")
    var netBreakableCharge: Int = 0,
    @SerializedName("NetCODCharge")
    var netCODCharge: Int = 0,
    @SerializedName("NetDeliveryCharge")
    var netDeliveryCharge: Int = 0,
    @SerializedName("NetCollectedAmount")
    var netCollectedAmount: Int = 0,
    @SerializedName("NetAdjustedAmount")
    var netAdjustedAmount: Int = 0,
    @SerializedName("NetPayableAmount")
    var netPayableAmount: Int = 0,
    @SerializedName("NetAdvanceReceivable")
    var netAdvanceReceivable: Int = 0,
    @SerializedName("IPCharge")
    var iPCharge: Int = 0,

    @SerializedName("ModeOfPayment")
    var modeOfPayment: String? = "",
    @SerializedName("TransactionNo")
    var transactionNo: String? = "",
    @SerializedName("OrderList")
    var orderList: List<OrderHistoryData>? = listOf()
)