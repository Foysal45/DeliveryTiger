package com.bd.deliverytiger.app.api.model.service_selection


import com.bd.deliverytiger.app.api.model.district.DistrictData
import com.google.gson.annotations.SerializedName

data class ServiceInfoData(
    @SerializedName("serviceId")
    var serviceId: Int = 0,
    @SerializedName("serviceTypeName")
    var serviceTypeName: String? = "",
    @SerializedName("serviceInfo")
    var serviceInfo: String? = "",
    @SerializedName("deliveryRangeId")
    var deliveryRangeId: List<Int> = listOf(),
    @SerializedName("serviceTypeShow")
    var serviceTypeShow: List<String> = listOf(),

    // private
    var districtList: List<DistrictData> = listOf(),
    var index: Int = 0
)