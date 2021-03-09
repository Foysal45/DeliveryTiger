package com.bd.deliverytiger.app.api.model.location

import com.google.gson.annotations.SerializedName

data class LocationData(
    var id: Int = 0,
    var displayName: String = "",
    var searchKey: String = "" // lower case
)
