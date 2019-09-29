package com.bd.deliverytiger.app.api.model.charge


import com.google.gson.annotations.SerializedName

data class BreakableChargeData(
    @SerializedName("id")
    var id: Int,
    @SerializedName("breakableCharge")
    var breakableCharge: Double,
    @SerializedName("codChargePercentage")
    var codChargePercentage: Double,
    @SerializedName("codChargeMin")
    var codChargeMin: Int
)