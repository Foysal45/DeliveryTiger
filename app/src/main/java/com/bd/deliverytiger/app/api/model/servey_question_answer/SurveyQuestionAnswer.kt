package com.bd.deliverytiger.app.api.model.servey_question_answer


import com.google.gson.annotations.SerializedName

data class SurveyQuestionAnswer(
    @SerializedName("surveyQuestionId")
    var surveyQuestionId: Int = 0,
    @SerializedName("surveyAnswerId")
    var surveyAnswerId: Int = 0,
    @SerializedName("merchantId")
    var merchantId: Int = 0,
    @SerializedName("surveyRedirectNextQuestionId")
    var surveyRedirectNextQuestionId: Int = 0,
    @SerializedName("surveyRedirectPreviousQuestionId")
    var surveyRedirectPreviousQuestionId: Int = 0,
    @SerializedName("surveyOpenAnswer")
    var surveyOpenAnswer: String? = ""
)