package com.bd.deliverytiger.app.ui.survey

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.databinding.ActivitySurveyBinding
import com.bd.deliverytiger.app.databinding.FragmentSurveyBinding
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

@SuppressLint("SetTextI18n")
class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val viewModel: SurveyViewModel by inject()

    private  var dataList: MutableList<SurveyQuestionModel> = mutableListOf()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbarTitle.text = "Survey"
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
            pagerAdapter = ViewPagerAdapter(this, list)
            with(binding.viewPager) {
                adapter = pagerAdapter
                offscreenPageLimit = 1
                //Log.e("dataTest", "$list")
                //setCurrentItem(currentPlayingIndex, false)
                //setPageTransformer(null)
                //isUserInputEnabled = false //disable scrolling
            }
            initProgress()
        })
    }

    fun nextQuesFragment(){

        val currentIndex = binding.viewPager.currentItem
        val currentQuestion = dataList[currentIndex]
        val nextIndex = currentQuestion.surveyAnswer.first().surveyRedirectNextQuestionId
        val questionIndex = dataList.indexOfFirst { it.surveyQuestionId == nextIndex }
        binding.viewPager.currentItem = questionIndex

        updateProgress(currentIndex)
    }

    fun previousQuesFragment(){
        val currentIndex = binding.viewPager.currentItem
        val previousIndex = currentIndex - 1
        if (previousIndex < dataList.size){
            binding.viewPager.currentItem = previousIndex
            updateProgress(currentIndex)
        }
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