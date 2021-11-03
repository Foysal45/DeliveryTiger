package com.bd.deliverytiger.app.api.model.loan_survey


import com.google.gson.annotations.SerializedName

data class LoanSurveyRequestBodyV2(
    @SerializedName("age")
    var age: String?,
    @SerializedName("applicationDate")
    var applicationDate: String?,
    @SerializedName("bankName")
    var bankName: String?,
    @SerializedName("basketValue")
    var basketvalue: String?,
    @SerializedName("cardHolder")
    var cardHolder: String?,
    @SerializedName("cardLimit")
    var cardLimit: String?,
    @SerializedName("courierUserId")
    var courierUserId: Int,
    @SerializedName("eduLevel")
    var eduLevel: String?,
    @SerializedName("famMem")
    var famMem: String?,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("guarantorMobile")
    var guarantorMobile: String?,
    @SerializedName("guarantorName")
    var guarantorName: String?,
    @SerializedName("hasCreditCard")
    var hasCreditCard: Boolean,
    @SerializedName("hasTin")
    var hasTin: Boolean,
    @SerializedName("homeOwnership")
    var homeOwnership: String?,
    @SerializedName("interestedAmount")
    var interestedAmount: Int,
    @SerializedName("isBankAccount")
    var isBankAccount: Boolean,
    @SerializedName("isLocalShop")
    var isLocalShop: Boolean,
    @SerializedName("loanAmount")
    var loanAmount: Int,
    @SerializedName("loanEmi")
    var loanEmi: String?,
    @SerializedName("loanSurveyId")
    var loanSurveyId: Int,
    @SerializedName("married")
    var married: String?,
    @SerializedName("merchantName")
    var merchantName: String?,
    @SerializedName("monthlyExp")
    var monthlyExp: String?,
    @SerializedName("monthlyOrder")
    var monthlyOrder: String?,
    @SerializedName("monthlyTotalAverageSale")
    var monthlyTotalAverageSale: Int,
    @SerializedName("monthlyTotalCodAmount")
    var monthlyTotalCodAmount: Int,
    @SerializedName("recommend")
    var recommend: String?,
    @SerializedName("relationMarchent")
    var relationMarchent: String?,
    @SerializedName("repayType")
    var repayType: String?,
    @SerializedName("shopOwnership")
    var shopOwnership: String?,
    @SerializedName("tinNumber")
    var tinNumber: String?,
    @SerializedName("tradeLicenseImageUrl")
    var tradeLicenseImageUrl: String?,
    @SerializedName("transactionAmount")
    var transactionAmount: Int
)