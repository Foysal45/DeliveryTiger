package com.bd.deliverytiger.app.api.model.loan_survey.get_loan_survey


import com.google.gson.annotations.SerializedName

data class LoanSurveyData(
    @SerializedName("age")
    val age: String = "",
    @SerializedName("annualTotalIncome")
    val annualTotalIncome: Int = 0,
    @SerializedName("applicationDate")
    val applicationDate: String = "",
    @SerializedName("bankName")
    val bankName: String = "",
    @SerializedName("basketValue")
    val basketValue: String = "",
    @SerializedName("cardHolder")
    val cardHolder: String = "",
    @SerializedName("cardLimit")
    val cardLimit: String = "",
    @SerializedName("companyBankAccName")
    val companyBankAccName: String = "",
    @SerializedName("companyBankAccNo")
    val companyBankAccNo: String = "",
    @SerializedName("courierWithLoanSurvey")
    val courierWithLoanSurvey: List<CourierWithLoanSurvey> = listOf(),
    @SerializedName("dateOfBirth")
    val dateOfBirth: String = "",
    @SerializedName("eduLevel")
    val eduLevel: String = "",
    @SerializedName("famMem")
    val famMem: String = "",
    @SerializedName("gender")
    val gender: String = "",
    @SerializedName("guarantorMobile")
    val guarantorMobile: String = "",
    @SerializedName("guarantorName")
    val guarantorName: String = "",
    @SerializedName("hasCreditCard")
    val hasCreditCard: Boolean = false,
    @SerializedName("hasPreviousLoan")
    val hasPreviousLoan: Boolean = false,
    @SerializedName("hasTin")
    val hasTin: Boolean = false,
    @SerializedName("hasTradeLicense")
    val hasTradeLicense: Boolean = false,
    @SerializedName("homeOwnership")
    val homeOwnership: String = "",
    @SerializedName("interestedAmount")
    val interestedAmount: Int = 0,
    @SerializedName("isBankAccount")
    val isBankAccount: Boolean = false,
    @SerializedName("isLocalShop")
    val isLocalShop: Boolean = false,
    @SerializedName("loanAmount")
    val loanAmount: Int = 0,
    @SerializedName("loanEmi")
    val loanEmi: String = "",
    @SerializedName("loanSurveyId")
    val loanSurveyId: Int = 0,
    @SerializedName("married")
    val married: String = "",
    @SerializedName("merchantName")
    val merchantName: String = "",
    @SerializedName("monthlyExp")
    val monthlyExp: String = "",
    @SerializedName("monthlyOrder")
    val monthlyOrder: String = "",
    @SerializedName("monthlyTotalAverageSale")
    val monthlyTotalAverageSale: Int = 0,
    @SerializedName("monthlyTotalCodAmount")
    val monthlyTotalCodAmount: Int = 0,
    @SerializedName("nidNo")
    val nidNo: String = "",
    @SerializedName("othersIncome")
    val othersIncome: Int = 0,
    @SerializedName("recommend")
    val recommend: String = "",
    @SerializedName("relationMarchent")
    val relationMarchent: String = "",
    @SerializedName("repayType")
    val repayType: String = "",
    @SerializedName("reqTenorMonth")
    val reqTenorMonth: Int = 0,
    @SerializedName("residenceLocation")
    val residenceLocation: String = "",
    @SerializedName("shopOwnership")
    val shopOwnership: String = "",
    @SerializedName("tinNumber")
    val tinNumber: String = "",
    @SerializedName("tradeLicenseExpireDate")
    val tradeLicenseExpireDate: String = "",
    @SerializedName("tradeLicenseImageUrl")
    val tradeLicenseImageUrl: String = "",
    @SerializedName("tradeLicenseNo")
    val tradeLicenseNo: String = "",
    @SerializedName("transactionAmount")
    val transactionAmount: Int = 0
)