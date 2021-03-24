package com.bd.deliverytiger.app.api.model.district


import com.google.gson.annotations.SerializedName

data class AllDistrictListsModel(
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("district")
    var district: String? = "",
    @SerializedName("districtBng")
    var districtBng: String? = "",
    @SerializedName("areaType")
    var areaType: Int = 0,
    @SerializedName("parentId")
    var parentId: Int = 0,
    @SerializedName("postalCode")
    var postalCode: String? = "",
    @SerializedName("isCity")
    var isCity: Boolean = false,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("isActiveForCorona")
    var isActiveForCorona: Boolean = false,
    @SerializedName("isPickupLocation")
    var isPickupLocation: Boolean = false,
    @SerializedName("districtPriority")
    var districtPriority: Int = 0
)