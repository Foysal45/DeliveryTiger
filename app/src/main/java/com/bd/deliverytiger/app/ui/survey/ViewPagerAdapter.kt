package com.bd.deliverytiger.app.ui.survey

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bd.deliverytiger.app.api.model.servey_question_answer.SurveyQuestionModel
import timber.log.Timber

class ViewPagerAdapter(private val fa: FragmentActivity, private val list: List<SurveyQuestionModel>): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return if (position == list.lastIndex){
            Timber.d("LastIndex ${list.lastIndex}")
            SurveyThanksFragment.newInstance(list[position])
        }else{
            SurveyFragment.newInstance(list[position])
        }
    }
}