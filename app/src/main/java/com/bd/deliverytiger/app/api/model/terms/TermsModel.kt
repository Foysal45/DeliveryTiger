package com.bd.deliverytiger.app.api.model.terms


import com.google.gson.annotations.SerializedName

data class TermsModel(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("termsConditions")
    var termsConditions: String? = ""
)