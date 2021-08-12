package com.bd.deliverytiger.app.api.model.lead_management


import com.google.gson.annotations.SerializedName

data class GetLocationInfoRequest(
    @SerializedName("districtId")
    var districtId: Int = 0
)