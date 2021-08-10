package com.bd.deliverytiger.app.api.model.order


import com.google.gson.annotations.SerializedName

data class DefaultDistrictRequestItem(
    @SerializedName("districtId")
    var districtId: Int = 0
)