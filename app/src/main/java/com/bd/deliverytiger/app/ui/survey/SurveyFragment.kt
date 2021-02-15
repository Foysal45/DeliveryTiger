package com.bd.deliverytiger.app.ui.survey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.databinding.FragmentSurveyBinding
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject

class SurveyFragment : Fragment() {

    private var binding: FragmentSurveyBinding? = null
    private val viewModel: SurveyViewModel by inject()
    private lateinit var dataAdapter: SurveyAdapter

    private lateinit var model: SurveyQuestionModel

    companion object {
        @JvmStatic
        fun newInstance(model: SurveyQuestionModel): SurveyFragment = SurveyFragment().apply {
            this.model = model
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
        Glide.with(binding?.surveyImageView!!).load(model.imageUrl).into(binding?.surveyImageView!!)
        dataAdapter = SurveyAdapter()
        binding?.recyclerview?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
            }
        }
        dataAdapter.initLoad(model.surveyAnswer, model.multipleAnswer)

        if (model.surveyAnswer != null && model.surveyAnswer.first().answerName == "Comment"){
            binding?.recyclerview?.visibility = GONE
            binding?.inputTextView?.isVisible = true
        }else{
            binding?.inputTextView?.isVisible = false
            binding?.recyclerview?.visibility = VISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}