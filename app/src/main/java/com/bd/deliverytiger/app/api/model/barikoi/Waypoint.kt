package com.bd.deliverytiger.app.api.model.barikoi


import com.google.gson.annotations.SerializedName

data class Waypoint(
    @SerializedName("hint")
    var hint: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("location")
    var location: List<Double>?
)