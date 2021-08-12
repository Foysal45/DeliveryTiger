package com.bd.deliverytiger.app.api.model.live.share_sms

import com.google.gson.annotations.SerializedName

data class FreeSMSServiceModel(
    @SerializedName("FreeSMSActive")
    var freeSMSActive: Boolean = false,
    @SerializedName("FreeSMSCount")
    var freeSMSCount: Int = 0
)
