package com.bd.deliverytiger.app.api.model.barikoi


import com.google.gson.annotations.SerializedName

data class Leg(
    @SerializedName("summary")
    var summary: String?,
    @SerializedName("duration")
    var duration: Double,
    @SerializedName("steps")
    var steps: List<Any>?,
    @SerializedName("distance")
    var distance: Double
)