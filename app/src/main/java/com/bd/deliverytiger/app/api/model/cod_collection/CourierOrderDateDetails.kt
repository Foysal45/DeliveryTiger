package com.bd.deliverytiger.app.api.model.cod_collection


import com.google.gson.annotations.SerializedName

data class CourierOrderDateDetails(
    @SerializedName("confirmationDate")
    var confirmationDate: String? = null,
    @SerializedName("orderDate")
    var orderDate: String? = null,
    @SerializedName("postedOn")
    var postedOn: String? = null,
    @SerializedName("logPostedOn")
    var logPostedOn: String? = null
)