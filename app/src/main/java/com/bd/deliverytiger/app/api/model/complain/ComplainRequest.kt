package com.bd.deliverytiger.app.api.model.complain


import com.google.gson.annotations.SerializedName

data class ComplainRequest(
    @SerializedName("OrderId")
    var orderId: String?,
    @SerializedName("Comments")
    var comments: String?,
    @SerializedName("OrderFrom")
    var orderFrom: String = "app",
    @SerializedName("CompanyName")
    var companyName: String = "",
    @SerializedName("Mobile")
    var mobile: String = ""

)