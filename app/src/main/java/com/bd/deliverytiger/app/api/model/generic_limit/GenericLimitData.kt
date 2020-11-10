package com.bd.deliverytiger.app.api.model.generic_limit


import com.google.gson.annotations.SerializedName

data class GenericLimitData(
    @SerializedName("collectionAmount")
    var collectionAmount: Double = 0.0,
    @SerializedName("actualPackagePrice")
    var actualPackagePrice: Double = 0.0
)