package com.bd.deliverytiger.app.api.model.servey_question_answer

import com.google.gson.annotations.SerializedName

data class SurveyQuestionAnswerResponse(
        @SerializedName("surveyQuestionAnswerLogId")
        var surveyQuestionAnswerLogId: Int = 0,
        @SerializedName("surveyQuestionId")
        var surveyQuestionId: Int = 0,
        @SerializedName("surveyAnswerId")
        var surveyAnswerId: Int = 0,
        @SerializedName("merchantId")
        var merchantId: Int = 0,
        @SerializedName("currentDate")
        var currentDate: String = "",
        @SerializedName("surveyOpenAnswer")
        var surveyOpenAnswer: String? = ""
)
