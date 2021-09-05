package com.bd.deliverytiger.app.api.model.loan_survey


import com.google.gson.annotations.SerializedName

data class LoanSurveyRequestBody (
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("gender")
    var gender: String? = "",
    @SerializedName("tradeLicenseImageUrl")
    var tradeLicenseImageUrl: String? = "",


    @SerializedName("loadSurveyId")
    var loadSurveyId: Int = 0,

    /*
    @SerializedName("loanRange")
    var loanRange: String? = "",
    @SerializedName("monthlyParcelCount")
    var monthlyParcelCount: String? = "",
    @SerializedName("monthlyTransaction")
    var monthlyTransaction: String? = "",

    @SerializedName("hasBankAccount")
    var hasBankAccount: Boolean? = false,
    @SerializedName("hasPhysicalShop")
    var hasPhysicalShop: Boolean? = false,

    @SerializedName("hasTradeLicence")
    var hasTradeLicence: Boolean? = false,
    @SerializedName("tradeLicence")
    var tradeLicence: String? = ""*/
)