package com.bd.deliverytiger.app.api.model.loan_survey.get_loan_survey


import com.google.gson.annotations.SerializedName

data class CourierWithLoanSurvey(
    @SerializedName("courierId")
    val courierId: Int = 0,
    @SerializedName("courierName")
    val courierName: String = "",
    @SerializedName("couriersWithLoanSurveyId")
    val couriersWithLoanSurveyId: Int = 0
)