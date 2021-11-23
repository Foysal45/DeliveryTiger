package com.bd.deliverytiger.app.api.model.loan_survey.get_loan_survey


import com.google.gson.annotations.SerializedName

data class CourierWithLoanSurveyX(
    @SerializedName("courierId")
    var courierId: Int = 0,
    @SerializedName("courierName")
    var courierName: String = "",
    @SerializedName("couriersWithLoanSurveyId")
    var couriersWithLoanSurveyId: Int = 0
)