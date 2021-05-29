package com.bd.deliverytiger.app.api.model.live.profile

import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("ProfileId")
    var profileId: Int = 0,
    @SerializedName("CustomerId")
    var customerId: Int = 0,
    @SerializedName("FacebookPageLinkEnable")
    var facebookPageLinkEnable: Boolean = false,
    @SerializedName("FacebookPageUrl")
    var fbPageUrl: String? = "",
    @SerializedName("FBStreamURL")
    var fbStreamUrl: String? = "",
    @SerializedName("FBStreamKey")
    var fbStreamKey: String? = "",
    @SerializedName("YoutubeStreamURL")
    var youtubeStreamUrl: String? = "",
    @SerializedName("YoutubeStreamKey")
    var youtubeStreamKey: String? = ""
)
