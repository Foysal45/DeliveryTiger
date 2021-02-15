package com.bd.deliverytiger.app.ui.survey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.databinding.FragmentSurveyBinding
import com.bd.deliverytiger.app.databinding.FragmentSurveyThanksBinding
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject


class SurveyThanksFragment : Fragment() {
    private var binding: FragmentSurveyThanksBinding? = null

    private lateinit var model: SurveyQuestionModel

    companion object {
        @JvmStatic
        fun newInstance(model: SurveyQuestionModel): SurveyThanksFragment = SurveyThanksFragment().apply {
            this.model = model
        }

        @JvmField
        val tag: String = SurveyThanksFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentSurveyThanksBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.surveyQues?.text = "ধন্যবাদ"


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}