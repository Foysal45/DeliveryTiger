package com.bd.deliverytiger.app.api.model.poh


import com.google.gson.annotations.SerializedName

data class MerchantPoHEligibilityCheckResponse(
    @SerializedName("NetPayableAmount")
    var netPayableAmount: Int = 0
)