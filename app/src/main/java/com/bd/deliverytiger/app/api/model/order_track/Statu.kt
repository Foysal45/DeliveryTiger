package com.bd.deliverytiger.app.api.model.order_track


import com.google.gson.annotations.SerializedName

data class Statu(
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("dateTime")
    var dateTime: String? = ""
)