package com.bd.deliverytiger.app.ui.survey

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyAnswer
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionAnswer
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.databinding.ActivitySurveyBinding
import com.bd.deliverytiger.app.utils.SessionManager
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.math.roundToInt

@SuppressLint("SetTextI18n")
class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val viewModel: SurveyViewModel by inject()

    private var dataList: MutableList<SurveyQuestionModel> = mutableListOf()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbarTitle.text = "ফিডব্যাক"
        binding.backBtn.setOnClickListener {
            super.onBackPressed()
        }

        viewModel.fetchSurveyQuestion().observe(this, Observer { list ->

            dataList.addAll(list)
            Timber.d("CheckAnswerDebug ${list.size} ${list.lastIndex}")
            val lastQuestion = list.last()
            lastQuestion.surveyAnswer.forEach { question ->
                question.surveyRedirectNextQuestionId = lastQuestion.surveyQuestionId + 1
                question.isSelected = true
            }
            val finalModel = SurveyQuestionModel(
                    list.size + 1, "ধন্যবাদ", 0, "thanks", false,
                    mutableListOf(SurveyAnswer(0, 0, "thanks", true, -1, lastQuestion.surveyQuestionId))
            )
            dataList.add(finalModel)

            pagerAdapter = ViewPagerAdapter(this, dataList)
            with(binding.viewPager) {
                adapter = pagerAdapter
                offscreenPageLimit = 1
                setPageTransformer(null)
                isUserInputEnabled = false //disable scrolling
            }
            initProgress()
        })
    }

    fun nextQuesFragment() {

        val currentIndex = binding.viewPager.currentItem
        val currentQuestion = dataList[currentIndex]
        val nextQuestionIndex = currentQuestion.surveyAnswer.find { it.isSelected }?.surveyRedirectNextQuestionId ?: -1
        Timber.d("CheckAnswerDebug nextQuestionIndex ${dataList.size}, $nextQuestionIndex")
        if (nextQuestionIndex == 0) {
            binding.viewPager.currentItem = dataList.lastIndex
            updatefootter(false)

        } else {
            updatefootter(true)
            // binding.nextBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.ashColor))
            val questionIndex = dataList.indexOfFirst { it.surveyQuestionId == nextQuestionIndex }
            binding.viewPager.currentItem = questionIndex
            updateProgress(questionIndex + 1)
        }
    }

    fun previousQuesFragment() {
        updatefootter(true)
        val currentIndex = binding.viewPager.currentItem
        val currentQuestion = dataList[currentIndex]
        val previousIndex = currentQuestion.surveyAnswer.first().surveyRedirectPreviousQuestionId
        val quesIndex = dataList.indexOfFirst { it.surveyQuestionId == previousIndex }
        binding.viewPager.currentItem = quesIndex
        updateProgress(quesIndex)

    }

    fun submitFeedback() {

        val surveyRequest: MutableList<SurveyQuestionAnswer> = mutableListOf()
        dataList.forEach { question ->
            question.surveyAnswer.forEach { answer ->
                if (answer.isSelected) {
                    val model = SurveyQuestionAnswer(
                            answer.surveyQuestionId,
                            answer.surveyAnswerId, SessionManager.courierUserId,
                            answer.surveyRedirectNextQuestionId,
                            answer.surveyRedirectPreviousQuestionId,
                            answer.comment
                    )
                    surveyRequest.add(model)
                }
            }
        }

        viewModel.fetchSubmitSurvey(surveyRequest).observe(this, Observer { list ->
            if (list.isNotEmpty()) {
                finish()
            }
        })

    }


    private fun initProgress() {

        binding.progressBar.progress = 0
        binding.progressBar.max = dataList.size
        binding.progressText.text = "${((binding.progressBar.progress / (dataList.size.toFloat())) * 100f).roundToInt()}%"

    }

    private fun updateProgress(progress: Int) {
        binding.progressBar.progress = progress
        binding.progressText.text = "${((binding.progressBar.progress / (dataList.size.toFloat())) * 100f).roundToInt()}%"
    }

    fun updatefootter(isVisible: Boolean) {

        binding.progressBar.isVisible = isVisible
        binding.progressText.isVisible = isVisible
        binding.footerTextView.isVisible = isVisible
    }
}