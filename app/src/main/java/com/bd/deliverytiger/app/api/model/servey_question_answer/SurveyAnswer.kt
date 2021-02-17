package com.bd.deliverytiger.app.api.model.servey_question_answer

import com.google.gson.annotations.SerializedName

data class SurveyAnswer(
    @SerializedName("surveyAnswerId")
    var surveyAnswerId: Int = 0,
    @SerializedName("surveyQuestionId")
    var surveyQuestionId: Int = 0,
    @SerializedName("answerName")
    var answerName: String = "",
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("surveyRedirectNextQuestionId")
    var surveyRedirectNextQuestionId: Int = 0,
    @SerializedName("surveyRedirectPreviousQuestionId")
    var surveyRedirectPreviousQuestionId: Int = 0,

    // private fields
    var isSelected: Boolean = false,
    var comment: String? = null
)
