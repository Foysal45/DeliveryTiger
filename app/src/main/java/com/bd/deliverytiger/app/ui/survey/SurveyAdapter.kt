package com.bd.deliverytiger.app.ui.survey

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyAnswer
import com.bd.deliverytiger.app.databinding.ItemViewSurveyQuestionOptionBinding
import kotlinx.android.synthetic.main.fragment_survey.*

class SurveyAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataList : MutableList<SurveyAnswer> = mutableListOf()
    var onItemClicked: ((model: SurveyAnswer) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewSurveyQuestionOptionBinding = ItemViewSurveyQuestionOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding

            binding.surveyQuestion.text = model.answerName
        }
    }

    inner class ViewModel(val binding: ItemViewSurveyQuestionOptionBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    onItemClicked?.invoke(dataList[adapterPosition])
            }
        }
    }

    fun initLoad(list: List<SurveyAnswer>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}