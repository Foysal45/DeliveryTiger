package com.bd.deliverytiger.app.api.model.service_selection


import com.bd.deliverytiger.app.api.model.district.AllDistrictListsModel
import com.google.gson.annotations.SerializedName

data class ServiceInfoData(
    @SerializedName("serviceTypeName")
    var serviceTypeName: String? = "",
    @SerializedName("serviceInfo")
    var serviceInfo: String? = "",
    @SerializedName("deliveryRangeId")
    var deliveryRangeId: List<Int> = listOf(),

    // private
    var locationList: List<AllDistrictListsModel> = listOf(),
    var districtList: List<AllDistrictListsModel> = listOf(),
    var index: Int = 0
)