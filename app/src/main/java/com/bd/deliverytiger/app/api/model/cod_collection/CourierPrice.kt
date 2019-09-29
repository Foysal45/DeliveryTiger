package com.bd.deliverytiger.app.api.model.cod_collection


import com.google.gson.annotations.SerializedName

data class CourierPrice(
    @SerializedName("collectionAmount")
    var collectionAmount: Double? = null,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Double? = null,
    @SerializedName("breakableCharge")
    var breakableCharge: Double? = null,
    @SerializedName("codCharge")
    var codCharge: Double? = null,
    @SerializedName("collectionCharge")
    var collectionCharge: Double? = null,
    @SerializedName("returnCharge")
    var returnCharge: Double? = null,
    @SerializedName("packagingName")
    var packagingName: Any? = null,
    @SerializedName("packagingCharge")
    var packagingCharge: Double? = null,
    @SerializedName("totalServiceCharge")
    var totalServiceCharge: Double? = null,
    @SerializedName("totalAmount")
    var totalAmount: Double? = null
)