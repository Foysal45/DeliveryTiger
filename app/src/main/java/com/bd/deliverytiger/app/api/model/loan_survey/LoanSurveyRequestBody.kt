package com.bd.deliverytiger.app.api.model.loan_survey


import com.google.gson.annotations.SerializedName

data class LoanSurveyRequestBody(
    @SerializedName("Age")
    var age: String = "",
    @SerializedName("AnnualTotalIncome")
    var annualTotalIncome: Int = 0,
    @SerializedName("BankName")
    var bankName: String = "",
    @SerializedName("Basketvalue")
    var basketvalue: String = "",
    @SerializedName("CardHolder")
    var cardHolder: String = "",
    @SerializedName("CardLimit")
    var cardLimit: String = "",
    @SerializedName("CompanyBankAccName")
    var companyBankAccName: String = "",
    @SerializedName("CompanyBankAccNo")
    var companyBankAccNo: String = "",
    @SerializedName("CourierUserId")
    var courierUserId: Int = 0,
    @SerializedName("DateOfBirth")
    var dateOfBirth: String = "",
    @SerializedName("EduLevel")
    var eduLevel: String = "",
    @SerializedName("FamMem")
    var famMem: String = "",
    @SerializedName("Gender")
    var gender: String = "",
    @SerializedName("GuarantorMobile")
    var guarantorMobile: String = "",
    @SerializedName("GuarantorName")
    var guarantorName: String = "",
    @SerializedName("HasCreditCard")
    var hasCreditCard: Boolean = false,
    @SerializedName("HasTin")
    var hasTin: Boolean = false,
    @SerializedName("HasTradeLicense")
    var hasTradeLicense: Boolean = false,
    @SerializedName("HomeOwnership")
    var homeOwnership: String = "",
    @SerializedName("InterestedAmount")
    var interestedAmount: Int = 0,
    @SerializedName("IsBankAccount")
    var isBankAccount: Boolean = false,
    @SerializedName("IsLocalShop")
    var isLocalShop: Boolean = false,
    @SerializedName("LoanAmount")
    var loanAmount: Int = 0,
    @SerializedName("LoanEmi")
    var loanEmi: String = "",
    @SerializedName("Married")
    var married: String = "",
    @SerializedName("MerchantName")
    var merchantName: String = "",
    @SerializedName("MonthlyExp")
    var monthlyExp: String = "",
    @SerializedName("MonthlyOrder")
    var monthlyOrder: String = "",
    @SerializedName("MonthlyTotalAverageSale")
    var monthlyTotalAverageSale: Int = 0,
    @SerializedName("MonthlyTotalCodAmount")
    var monthlyTotalCodAmount: Int = 0,
    @SerializedName("NidNo")
    var nidNo: String = "",
    @SerializedName("OthersIncome")
    var othersIncome: Int = 0,
    @SerializedName("Recommend")
    var recommend: String = "",
    @SerializedName("RelationMarchent")
    var relationMarchent: String = "",
    @SerializedName("RepayType")
    var repayType: String = "",
    @SerializedName("ReqTenorMonth")
    var reqTenorMonth: String = "",
    @SerializedName("ResidenceLocation")
    var residenceLocation: String = "",
    @SerializedName("ShopOwnership")
    var shopOwnership: String = "",
    @SerializedName("TinNumber")
    var tinNumber: String = "",
    @SerializedName("TradeLicenseExpireDate")
    var tradeLicenseExpireDate: String = "",
    @SerializedName("TradeLicenseImageUrl")
    var tradeLicenseImageUrl: String = "",
    @SerializedName("TradeLicenseNo")
    var tradeLicenseNo: String = "",
    @SerializedName("TransactionAmount")
    var transactionAmount: Int = 0,
    @SerializedName("hasPreviousLoan")
    var hasPreviousLoan: Boolean = false
)