package com.bd.deliverytiger.app.api.model.service_selection


import com.google.gson.annotations.SerializedName

data class GetServiceDistrictsRequest(
    @SerializedName("rangeId")
    var rangeId: List<Int>? = listOf()
)