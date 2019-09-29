package com.bd.deliverytiger.app.api.model.cod_collection


import com.google.gson.annotations.SerializedName

data class AdCourierCommunicationInfo(
    @SerializedName("isCustomerSms")
    var isCustomerSms: Boolean? = null,
    @SerializedName("isCustomerEmail")
    var isCustomerEmail: Boolean? = null,
    @SerializedName("isSms")
    var isSms: Boolean? = null,
    @SerializedName("isEmail")
    var isEmail: Boolean? = null
)