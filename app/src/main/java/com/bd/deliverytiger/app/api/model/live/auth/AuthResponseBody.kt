package com.bd.deliverytiger.app.api.model.live.auth


import com.google.gson.annotations.SerializedName

data class AuthResponseBody(
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("Email")
    var email: String? = "",
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("Gender")
    var gender: String? = "",
    @SerializedName("Address")
    var address: String? = "",
    @SerializedName("Location")
    var location: String? = "",
    @SerializedName("District")
    var district: String? = "",
    @SerializedName("DistrictId")
    var districtId: Int = 0,
    @SerializedName("KnowingSource")
    var knowingSource: String? = "",
    @SerializedName("LocationBng")
    var locationBng: String? = "",
    @SerializedName("AreaId")
    var areaId: Int = 0,
    @SerializedName("ProfileImage")
    var profileImage: String? = "",
    @SerializedName("ProfileImageDefault")
    var profileImageDefault: String? = "",
    @SerializedName("ReferrerMobile")
    var referrerMobile: String? = "",
    @SerializedName("ReferrerStatus")
    var referrerStatus: Int = 0,
    @SerializedName("ReferrerAmount")
    var referrerAmount: Int = 0,
    @SerializedName("ReferralAmount")
    var referralAmount: Int = 0,
    @SerializedName("ReferralCode")
    var referralCode: String? = "",
    @SerializedName("IsVerified")
    var isVerified: Int = 0,
    @SerializedName("CurrentCustomerId")
    var currentCustomerId: Int = 0,
    @SerializedName("FacebookPageUrl")
    var facebookPageUrl: String? = ""
)