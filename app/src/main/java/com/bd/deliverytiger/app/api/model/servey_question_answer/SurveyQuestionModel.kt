package com.bd.deliverytiger.app.api.model.servey_question_answer

import com.google.gson.annotations.SerializedName

data class SurveyQuestionModel(
    @SerializedName("surveyQuestionId")
    var surveyQuestionId: Int = 0,
    @SerializedName("questionName")
    var questionName: String = "",
    @SerializedName("ordering")
    var ordering: Int = 0,
    @SerializedName("imageUrl")
    var imageUrl: String = "",
    @SerializedName("multipleAnswer")
    var multipleAnswer: Boolean = false,
    @SerializedName("surveyAnswerViewModel")
    var surveyAnswer: List<SurveyAnswer>
)
