package com.bd.deliverytiger.app.api.model.live.brand

import com.google.gson.annotations.SerializedName

data class BrandRequest (
    @SerializedName("SearchString")
    var searchString: String = "",
    @SerializedName("FromRow")
    var index: Int = 0,
    @SerializedName("ToRow")
    var count: Int = 20
)