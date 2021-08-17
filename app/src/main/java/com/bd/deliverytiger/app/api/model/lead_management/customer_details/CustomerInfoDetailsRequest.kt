package com.bd.deliverytiger.app.api.model.lead_management.customer_details


import com.google.gson.annotations.SerializedName

data class CustomerInfoDetailsRequest(
    @SerializedName("Mobile")
    var mobile: String? = ""
)