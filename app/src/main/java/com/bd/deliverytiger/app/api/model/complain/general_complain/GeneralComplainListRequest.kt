package com.bd.deliverytiger.app.api.model.complain.general_complain


import com.google.gson.annotations.SerializedName

data class GeneralComplainListRequest(
    @SerializedName("FromDate")
    val fromDate: String = "",
    @SerializedName("ToDate")
    val toDate: String = ""
)