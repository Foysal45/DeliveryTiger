package com.bd.deliverytiger.app.api.model.collection_history


import com.google.gson.annotations.SerializedName

data class CollectionData(
    @SerializedName("id")
    var id: Int,
    @SerializedName("courierOrderId")
    var courierOrderId: String?,
    @SerializedName("isConfirmedBy")
    var isConfirmedBy: String?,
    @SerializedName("orderDate")
    var orderDate: String?,
    @SerializedName("status")
    var status: Int,
    @SerializedName("postedOn")
    var postedOn: String?,
    @SerializedName("postedBy")
    var postedBy: Int,
    @SerializedName("merchantId")
    var merchantId: Int,
    @SerializedName("comment")
    var comment: String?,
    @SerializedName("podNumber")
    var podNumber: String?,
    @SerializedName("courierId")
    var courierId: Int,
    @SerializedName("hubName")
    var hubName: String?
)