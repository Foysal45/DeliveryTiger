package com.bd.deliverytiger.app.api.model.quick_order.quick_order_history


import com.google.gson.annotations.SerializedName

data class DistrictsViewModel(
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("district")
    var district: String? = "",
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("thana")
    var thana: String? = "",
    @SerializedName("area")
    var area: Any? = Any(),
    @SerializedName("edeshMobileNo")
    var edeshMobileNo: String? = "",
    @SerializedName("tigerMobileNo")
    var tigerMobileNo: String? = ""
)