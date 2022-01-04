package com.bd.deliverytiger.app.api.model.poh


import com.google.gson.annotations.SerializedName

data class PohApplicableResponse(
    @SerializedName("pohApplicable")
    var pohApplicable: Int = 0
)