package com.bd.deliverytiger.app.api.model.order_track


import com.google.gson.annotations.SerializedName

data class OrderTrackMainResponse(
    @SerializedName("orderTrackStatusGroup")
    var orderTrackStatusGroup: String? = "",
    @SerializedName("dateTime")
    var dateTime: String? = "",
    @SerializedName("status")
    var status: List<Statu?>? = listOf(),
    @SerializedName("districtId")
    var districtId: Int? = 0,
    @SerializedName("district")
    var district: String? = "",
    @SerializedName("districtBng")
    var districtBng: String? = "",
    @SerializedName("areaType")
    var areaType: Int? = 0,
    @SerializedName("parentId")
    var parentId: Int? = 0,
    @SerializedName("postalCode")
    var postalCode: String? = "",
    @SerializedName("isActive")
    var isActive: Boolean? = false
)