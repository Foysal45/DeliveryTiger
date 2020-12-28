package com.bd.deliverytiger.app.api.model.order_track


import com.google.gson.annotations.SerializedName

data class DistrictsViewModel(
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("district")
    var district: String? = ""
)