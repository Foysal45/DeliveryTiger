package com.bd.deliverytiger.app.api.model.collector_info


import com.google.gson.annotations.SerializedName

data class CollectorInformation(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("isActive")
    var isActive: Int = 0,
    @SerializedName("isNowOffline")
    var isNowOffline: Boolean = false,
    @SerializedName("latitude")
    var latitude: Any? = Any(),
    @SerializedName("longitude")
    var longitude: Any? = Any(),
    @SerializedName("updatedOn")
    var updatedOn: String? = "",
    @SerializedName("isPermanentRider")
    var isPermanentRider: Boolean = false,
    @SerializedName("firebaseToken")
    var firebaseToken: Any? = Any(),
    @SerializedName("riderType")
    var riderType: Any? = Any(),
    @SerializedName("officeInfoViewModel")
    var officeInfoViewModel: OfficeInfoViewModel? = OfficeInfoViewModel()
)