package com.bd.deliverytiger.app.api.model.cod_collection


import com.google.gson.annotations.SerializedName

data class CourierAddressContactInfo(
    @SerializedName("mobile")
    var mobile: String? = null,
    @SerializedName("otherMobile")
    var otherMobile: String? = null,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("districtNameEng")
    var districtNameEng: String? = null,
    @SerializedName("thanaNameEng")
    var thanaNameEng: String? = null,
    @SerializedName("areaNameEng")
    var areaNameEng: String? = null,
    @SerializedName("districtName")
    var districtName: String? = null,
    @SerializedName("thanaName")
    var thanaName: String? = null,
    @SerializedName("areaName")
    var areaName: String? = null,
    @SerializedName("districtId")
    var districtId: Int? = null,
    @SerializedName("thanaId")
    var thanaId: Int? = null,
    @SerializedName("areaId")
    var areaId: Int? = null,
    @SerializedName("thanaPostalCode")
    var thanaPostalCode: String? = null,
    @SerializedName("areaPostalCode")
    var areaPostalCode: String? = null,
    @SerializedName("assignedExpressCourierId")
    var assignedExpressCourierId: Int = 0,
    @SerializedName("assignedCourierId")
    var assignedCourierId: Int = 0,
    @SerializedName("collectAddressDistrictId")
    var collectAddressDistrictId: Int = 0,
    @SerializedName("collectAddressThanaId")
    var collectAddressThanaId: Int = 0,
    @SerializedName("collectDistrictName")
    var collectDistrictName: String? = "",
    @SerializedName("collectThanaName")
    var collectThanaName: String? = ""

)