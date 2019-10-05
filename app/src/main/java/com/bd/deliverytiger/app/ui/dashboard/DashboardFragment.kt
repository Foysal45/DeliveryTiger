package com.bd.deliverytiger.app.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.dashboard.DashboardModel
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.Timber
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    private lateinit var announcementLayout: ConstraintLayout
    private lateinit var monthSpinner: AppCompatSpinner
    private lateinit var dashboardRV: RecyclerView
    private lateinit var addOrderBtn: FloatingActionButton

    companion object{
        fun newInstance(): DashboardFragment = DashboardFragment().apply {

        }
        val tag = DashboardFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        announcementLayout = view.findViewById(R.id.dashboard_announce_layout)
        monthSpinner = view.findViewById(R.id.spinner_month_selection)
        dashboardRV = view.findViewById(R.id.dashboard_rv)
        addOrderBtn = view.findViewById(R.id.dashboard_add_order)


        addOrderBtn.setOnClickListener {
            addOrderFragment()
        }

        val banglaMonth = arrayOf("জানুয়ারী","ফেব্রুয়ারী","মার্চ","এপ্রিল","মে","জুন","জুলাই","আগস্ট","সেপ্টেম্বর","অক্টোবর","নভেম্বর","ডিসেম্বর")
        val calender = Calendar.getInstance()
        val currentYear = calender.get(Calendar.YEAR)
        val currentMonth = calender.get(Calendar.MONTH)
        val list: MutableList<MonthDataModel> = mutableListOf()
        val viewList: MutableList<String> = mutableListOf()
        for (year in currentYear..2019){
            var lastMonth = 11
            if (year == currentYear) {
                lastMonth = currentMonth
            }
            for (monthIndex in lastMonth downTo 0){
                if (year == 2019 && monthIndex == 6) {
                    break
                }
                list.add(MonthDataModel(monthIndex+1, year))
                viewList.add("${banglaMonth[monthIndex]}, ${DigitConverter.toBanglaDigit(year)}")
            }
        }

        val packagingAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, viewList)
        monthSpinner.adapter = packagingAdapter
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val model = list[p2]
                Timber.d("DashboardTag", "${model.monthId} ${model.year}")
            }
        }

        val modelList: MutableList<DashboardModel> = mutableListOf()
        modelList.add(DashboardModel(1890,"পেমেন্ট রেডি", 2, "positive"))
        modelList.add(DashboardModel(171,"শিপমেন্ট হয়েছে", 1, "neutral"))
        modelList.add(DashboardModel(24,"ডেলিভারি হয়েছে", 1, "positive"))
        modelList.add(DashboardModel(3,"রিটার্ন হয়েছে", 1, "negative"))
        modelList.add(DashboardModel(17,"প্রসেসিং", 1, "waiting"))

        val dashboardAdapter = DashboardAdapter(context!!, modelList)
        val gridLayoutManager = GridLayoutManager(context!!, 2, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return modelList[position].spanCount
            }
        }
        with(dashboardRV){
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = dashboardAdapter
        }


    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("ড্যাশবোর্ড")
    }

    private fun addFragment(fragment: Fragment, tag: String){
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    private fun addOrderFragment() {

        val fragment = AddOrderFragmentOne.newInstance()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AddOrderFragmentOne.tag)
        ft?.addToBackStack(AddOrderFragmentOne.tag)
        ft?.commit()

    }

}
