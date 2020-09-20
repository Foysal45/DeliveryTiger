package com.bd.deliverytiger.app.api.model.barikoi


import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("code")
    var code: String? = "",
    @SerializedName("routes")
    var routes: List<Route>?,
    @SerializedName("waypoints")
    var waypoints: List<Waypoint>?
)