package com.bd.deliverytiger.app.api.model.collector_info


import com.google.gson.annotations.SerializedName

data class OfficeInfoViewModel(
    @SerializedName("customerCareMobile")
    var customerCareMobile: String? = ""
)