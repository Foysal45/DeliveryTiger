package com.bd.deliverytiger.app.api.model.return_statement

import com.google.gson.annotations.SerializedName

data class ReturnStatementData(
        @SerializedName("DealId")
        var dealId: Int,
        @SerializedName("CouponId")
        var couponId: Int,
        @SerializedName("FolderName")
        var folderName: String?,
        @SerializedName("ConfirmationDate")
        var confirmationDate: String?,
        @SerializedName("DealTitle")
        var dealTitle: String?,
        @SerializedName("ProductImageLink")
        var productImageLink: String?
)