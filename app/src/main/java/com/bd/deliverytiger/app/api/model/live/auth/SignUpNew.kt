package com.bd.deliverytiger.app.api.model.live.auth


import com.google.gson.annotations.SerializedName

data class SignUpNew(
    @SerializedName("DeviceId")
    var deviceId: String? = "",
    @SerializedName("FireBaseToken")
    var fireBaseToken: String? = "",
    @SerializedName("Address")
    var address: String? = "",
    @SerializedName("DistrictId")
    var districtId: Int = 0,
    @SerializedName("Email")
    var email: String? = "",
    @SerializedName("FacebookPageUrl")
    var facebookPageUrl: String? = "",
    @SerializedName("Gender")
    var gender: String? = "",
    @SerializedName("LocationId")
    var locationId: Int = 0,
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("Password")
    var password: String? = "",
    @SerializedName("IsSignUpByLivePlaza")
    var isSignUpByLivePlaza: Int = 4
)