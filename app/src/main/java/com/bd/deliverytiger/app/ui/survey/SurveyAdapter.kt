package com.bd.deliverytiger.app.ui.survey

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyAnswer
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import com.bd.deliverytiger.app.databinding.ItemViewSurveyQuestionOptionBinding
import timber.log.Timber

class SurveyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataList: MutableList<SurveyAnswer> = mutableListOf()
    private var isMultipleAnswer: Boolean = false

    var onItemClicked: ((model: SurveyAnswer) -> Unit)? = null
    var onAnswerChecked: ((model: SurveyAnswer, isMultipleAnswer: Boolean) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewSurveyQuestionOptionBinding = ItemViewSurveyQuestionOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.checkBoxRightAnswer.text = model.answerName
            binding.checkBoxRightAnswer.isChecked = model.isSelected
            binding.checkBoxRightAnswer.setOnCheckedChangeListener { _, b ->
                val checkedModel = dataList[holder.adapterPosition]
                if (b) {
                    if (!isMultipleAnswer) {
                        dataList.forEach {
                            it.isSelected = false
                        }
                    }
                    checkedModel.isSelected = true
                    if (!isMultipleAnswer) {
                        onAnswerChecked?.invoke(checkedModel, isMultipleAnswer)
                    }
                } else {
                    checkedModel.isSelected = false
                }
            }

        }
    }

    inner class ViewModel(val binding: ItemViewSurveyQuestionOptionBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    fun initLoad(list: MutableList<SurveyAnswer>, isMultipleAnswer: Boolean) {
        this.dataList = list
        this.isMultipleAnswer = isMultipleAnswer
        notifyDataSetChanged()
    }

}