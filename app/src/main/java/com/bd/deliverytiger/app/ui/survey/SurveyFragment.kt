package com.bd.deliverytiger.app.ui.survey

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.databinding.FragmentSurveyBinding
import com.bd.deliverytiger.app.utils.toast
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import timber.log.Timber


class SurveyFragment : Fragment() {

    private var binding: FragmentSurveyBinding? = null
    private val viewModel: SurveyViewModel by inject()
    private lateinit var dataAdapter: SurveyAdapter

    private var dataList: MutableList<SurveyQuestionModel> = mutableListOf()
    private var position = -1
    private lateinit var model: SurveyQuestionModel


    companion object {
        @JvmStatic
        fun newInstance(dataList: MutableList<SurveyQuestionModel>, position: Int): SurveyFragment = SurveyFragment().apply {
            this.dataList = dataList
            this.position = position
            this.model = dataList[position]
        }

        @JvmField
        val tag: String = SurveyFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentSurveyBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.surveyQues?.text = model.questionName
        if (model.isMultipleAnswer) {
            binding?.surveyQuesHint?.text = "(চাইলে একের অধিক উত্তর সিলেক্ট করুন)"
        } else {
            binding?.surveyQuesHint?.text = "(একটি উত্তর সিলেক্ট করুন)"
        }
        binding?.surveyImageView?.let { view ->
            Glide.with(view)
                    .load(model.imageUrl)
                    .into(view)
        }
        Timber.d("ImageUrlCheck ${model.imageUrl}")

        dataAdapter = SurveyAdapter()
        binding?.recyclerview?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
                itemAnimator = null
            }
        }

        dataAdapter.onAnswerChecked = { model, isMultipleAnswer ->
            Timber.d("CheckAnswerDebug $isMultipleAnswer $model")
            Timber.d("CheckAnswerDebug ${this.model}")
            if (!isMultipleAnswer) {
                when (model.surveyQuestionId) {
                    2 -> {
                        if (model.surveyAnswerId == 6) {
                            model.surveyRedirectNextQuestionId = 5
                            val nextQuestion = dataList.find { it.surveyQuestionId == 5 }
                            nextQuestion?.surveyAnswer?.forEach { question ->
                                question.surveyRedirectPreviousQuestionId = 2
                            }
                        }
                    }
                    4 -> {
                        if (model.surveyAnswerId == 11) {
                            model.surveyRedirectNextQuestionId = 6
                            val nextQuestion = dataList.find { it.surveyQuestionId == 6 }
                            nextQuestion?.surveyAnswer?.forEach { question ->
                                question.surveyRedirectPreviousQuestionId = 4
                            }
                        }else{
                            model.surveyRedirectNextQuestionId = 5
                            val nextQuestion = dataList.find { it.surveyQuestionId == 5 }
                            nextQuestion?.surveyAnswer?.forEach { question ->
                                question.surveyRedirectPreviousQuestionId = 4
                            }
                        }
                    }
                }
                nextQuestion()
            } else {

            }
        }



        binding?.nextBtn?.setOnClickListener {
            when {
                model.surveyAnswer.first().answerName == "comment" -> {
                    model.surveyAnswer.first().comment = binding?.inputTextView?.text.toString()
                    nextQuestion()
                }
                model.surveyAnswer.any { it.isSelected } -> {
                    nextQuestion()
                }
                else -> {
                    context?.toast("${binding?.surveyQuesHint?.text?.toString()}")
                }
            }
        }

        binding?.previousBtn?.setOnClickListener{
            previousQuestion()
        }

        binding?.submitBtn?.setOnClickListener {
            (activity as SurveyActivity).submitFeedback()
        }

    }

    override fun onResume() {
        super.onResume()
        dataAdapter.initLoad(model.surveyAnswer, model.isMultipleAnswer)
        // CommentBox
        if (model.surveyAnswer.first().answerName == "comment"){
            binding?.recyclerview?.visibility = GONE
            binding?.inputTextView?.isVisible = true
            binding?.surveyQuesHint?.isVisible = false
        }else if (model.surveyAnswer.first().answerName == "thanks"){
            binding?.surveyQues?.isVisible = false
            binding?.surveyThanks?.isVisible = true
            binding?.surveyThanks?.text = model.questionName
            binding?.surveyThanksSubTitle?.isVisible = true
            binding?.surveyThanksSubTitle?.text = "আপনার ফিডব্যাক আমাদের জন্য অত্যন্ত গুরুত্বপূর্ণ"
            binding?.inputTextView?.isVisible = false
            binding?.recyclerview?.visibility = GONE
            binding?.surveyImageView?.setBackgroundResource(R.drawable.ic_hand)
            binding?.submitBtn?.isVisible = true
            binding?.nextBtn?.isVisible = false
            binding?.previousBtn?.isVisible = true
            binding?.surveyQuesHint?.isVisible = false
            (activity as SurveyActivity).updatefootter(false)

        } else{
            binding?.inputTextView?.isVisible = false
            binding?.recyclerview?.visibility = VISIBLE
        }
    }

    private fun nextQuestion() {
        (activity as SurveyActivity).nextQuesFragment()
    }

    private fun previousQuestion() {
        (activity as SurveyActivity).previousQuesFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}