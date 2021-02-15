package com.bd.deliverytiger.app.ui.survey

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionAnswer
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.databinding.ActivitySurveyBinding
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.math.roundToInt

@SuppressLint("SetTextI18n")
class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val viewModel: SurveyViewModel by inject()

    private var dataList: MutableList<SurveyQuestionModel> = mutableListOf()
    private val answersList: MutableList<SurveyQuestionAnswer> = mutableListOf()

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

        binding.previousBtn.setOnClickListener{
            previousQuesFragment()
        }

        binding.nextBtn.setOnClickListener {
           nextQuesFragment()
        }

        viewModel.fetchSurveyQuestion().observe(this, Observer { list ->

            dataList.addAll(list)
            dataList.addAll(listOf(SurveyQuestionModel(list.size, "ধন্যবাদ",0, "@drawable/ic_hand", false, listOf())))
            Timber.d("feedbackDebug ${dataList[7]}")
            pagerAdapter = ViewPagerAdapter(this, dataList)
            with(binding.viewPager) {
                adapter = pagerAdapter
                offscreenPageLimit = 1
                //Log.e("dataTest", "$list")
                //setCurrentItem(currentPlayingIndex, false)
                setPageTransformer(null)
                //isUserInputEnabled = false //disable scrolling
            }
            initProgress()
        })
    }

    @SuppressLint("ResourceAsColor")
    fun nextQuesFragment(){

        val currentIndex = binding.viewPager.currentItem
        val currentQuestion = dataList[currentIndex]
        //answersList.add(SurveyQuestionAnswer(currentQuestion.surveyQuestionId,))
        val nextIndex = currentQuestion.surveyAnswer.first().surveyRedirectNextQuestionId
            if (nextIndex == 0) {
                binding.viewPager.currentItem = dataList.lastIndex
                binding.nextBtn.text = "সাবমিট"
                binding.nextBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
                binding.progressBar.isVisible = false
                binding.progressText.isVisible = false
                binding.footerTextView.isVisible = false
                updateProgress(dataList.lastIndex + 1)
            } else {
                binding.nextBtn.text = "পরবর্তী ধাপ"
                val questionIndex = dataList.indexOfFirst { it.surveyQuestionId == nextIndex }
                binding.viewPager.currentItem = questionIndex
                updateProgress(questionIndex + 1)
            }
    }

    fun previousQuesFragment(){

        val currentIndex = binding.viewPager.currentItem
        val previousQuestion = dataList[currentIndex]
        val previousIndex = previousQuestion.surveyAnswer.first().surveyRedirectPreviousQuestionId
        val quesIndex = dataList.indexOfFirst { it.surveyQuestionId == previousIndex }
        binding.viewPager.currentItem = quesIndex

        updateProgress(quesIndex)
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
}