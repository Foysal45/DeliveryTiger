package com.bd.deliverytiger.app.api.model.helpline_number


import com.google.gson.annotations.SerializedName

data class HelpLineNumberModel(
    @SerializedName("helpLine1")
    var helpLine1: String? = "",
    @SerializedName("helpLine2")
    var helpLine2: String? = ""
)