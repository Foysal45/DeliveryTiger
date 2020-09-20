package com.bd.deliverytiger.app.api.model.barikoi


import com.google.gson.annotations.SerializedName

data class Route(
    @SerializedName("geometry")
    var geometry: String?,
    @SerializedName("legs")
    var legs: List<Leg>?,
    @SerializedName("duration")
    var duration: Double,
    @SerializedName("distance")
    var distance: Double
)