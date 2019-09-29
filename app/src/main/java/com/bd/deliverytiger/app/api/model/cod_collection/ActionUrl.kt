package com.bd.deliverytiger.app.api.model.cod_collection


import com.google.gson.annotations.SerializedName

data class ActionUrl(
    @SerializedName("buttonName")
    var buttonName: String? = null,
    @SerializedName("url")
    var url: String? = null
)