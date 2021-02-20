package com.bd.deliverytiger.app.api.model.complain

import com.google.gson.annotations.SerializedName

data class IsComplainExistsResponse(
        @SerializedName("IsClosed")
        var isClosed: Boolean = false,
        @SerializedName("IsExists")
        var isExists:  Boolean = false
)
