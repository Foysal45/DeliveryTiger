package com.bd.deliverytiger.app.ui.survey

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel

class ViewPagerAdapter(private val fa: FragmentActivity, private val list: MutableList<SurveyQuestionModel>): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return SurveyFragment.newInstance(list, position)
    }
}