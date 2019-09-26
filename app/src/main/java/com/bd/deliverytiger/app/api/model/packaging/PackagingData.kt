package com.bd.deliverytiger.app.api.model.packaging


import com.google.gson.annotations.SerializedName

data class PackagingData(
    @SerializedName("packagingChargeId")
    var packagingChargeId: Int,
    @SerializedName("packagingName")
    var packagingName: String,
    @SerializedName("packagingCharge")
    var packagingCharge: Double,
    @SerializedName("isActive")
    var isActive: Boolean
)