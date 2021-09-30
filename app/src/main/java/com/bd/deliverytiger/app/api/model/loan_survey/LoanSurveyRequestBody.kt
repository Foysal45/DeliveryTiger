package com.bd.deliverytiger.app.api.model.loan_survey


import com.google.gson.annotations.SerializedName

data class LoanSurveyRequestBody (
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("MerchantName")
    var merchantName: String? = "",
    @SerializedName("gender")
    var gender: String? = "",
    @SerializedName("tradeLicenseImageUrl")
    var tradeLicenseImageUrl: String? = "",
    @SerializedName("interestedAmount")
    var interestedAmount: String? = "0",
    @SerializedName("transactionAmount")
    var transactionAmount: String? = "0",
    @SerializedName("isBankAccount")
    var isBankAccount: Boolean = false,
    @SerializedName("isLocalShop")
    var isLocalShop: Boolean = false,
    @SerializedName("MonthlyTotalAverageSale")
    var totalMonthlyAverageSell: Int? = 0,

    @SerializedName("totalMonthlyCOD")
    var totalMonthlyCOD: String? = "0",
    @SerializedName("guarantorName")
    var guarantorName: String? = "",
    @SerializedName("guarantorNumber")
    var guarantorNumber: String? = "",


    @SerializedName("loanSurveyId")
    var loanSurveyId: Int = 0,
    @SerializedName("loanAmount")
    var loanAmount: Int = 0,
    @SerializedName("bankName")
    var bankName:  String? = ""
)