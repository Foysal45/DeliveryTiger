package com.bd.deliverytiger.app.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.DashBoardInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponseModel
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.billing_of_service.BillingofServiceFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.shipment_charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.utils.*
import com.bd.deliverytiger.app.utils.DigitConverter.banglaMonth
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    private lateinit var announcementLayout: ConstraintLayout
    private lateinit var monthSpinner: AppCompatSpinner
    private lateinit var dashboardRV: RecyclerView
    private lateinit var addOrderBtn: FloatingActionButton
    private lateinit var dashBoardProgress: ProgressBar
    private lateinit var addOrderNewBtn: LinearLayout

    private var currentYear = 0
    private var selectedYear = 0
    private var selectedMonth = 0
    private var isLoading = false


    companion object {
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
        dashBoardProgress = view.findViewById(R.id.dashBoardProgress)
        addOrderNewBtn = view.findViewById(R.id.dashboard_add_order_new)


        addOrderBtn.setOnClickListener {
            addOrderFragment()
        }
        addOrderNewBtn.setOnClickListener {
            addOrderFragment()
        }

        val calender = Calendar.getInstance()
        currentYear = calender.get(Calendar.YEAR)
        val currentMonth = calender.get(Calendar.MONTH)
        getDashBoardData(currentMonth + 1, currentYear)

        selectedYear = currentYear
        selectedMonth = currentMonth + 1
        /*val viewList: MutableList<String> = mutableListOf()
        for (item in banglaMonth) {
            viewList.add("${item}, ${DigitConverter.toBanglaDigit(currentYear)}")
        }*/

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

        val packagingAdapter =
            CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, viewList)
        monthSpinner.adapter = packagingAdapter
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!isLoading) {
                    val model = list[p2]
                    selectedYear = model.year
                    selectedMonth = model.monthId
                    getDashBoardData(model.monthId, model.year)
                    Timber.d("DashboardTag", "${model.monthId} $currentYear")
                }
            }
        }
        //monthSpinner.setSelection(currentMonth)


    }

    private fun getDashBoardData(selectedMonth: Int, selectedYear: Int) {
        isLoading = true
        dashBoardProgress.visibility = View.VISIBLE
        val dashBoardInterface =
            RetrofitSingleton.getInstance(context!!).create(DashBoardInterface::class.java)
        val responseModelList: MutableList<DashboardResponseModel> = mutableListOf()
        val dashBoardReqBody =
            DashBoardReqBody(selectedMonth, selectedYear, SessionManager.courierUserId)

        Timber.d("DashboardTag r ", dashBoardReqBody.toString())

        dashBoardInterface.getDashboardStatusGroup(dashBoardReqBody)
            .enqueue(object : Callback<GenericResponse<List<DashboardResponseModel>>> {
                override fun onFailure(
                    call: Call<GenericResponse<List<DashboardResponseModel>>>,
                    t: Throwable
                ) {
                    Timber.d("DashboardTag f ", t.toString())
                    isLoading = false
                    dashBoardProgress.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<GenericResponse<List<DashboardResponseModel>>>,
                    response: Response<GenericResponse<List<DashboardResponseModel>>>
                ) {
                    isLoading = false
                    dashBoardProgress.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        Timber.d("DashboardTag f ", response.body().toString())
                        responseModelList.addAll(response.body()!!.model)
                        setDashBoardAdapter(responseModelList)
                    }
                }

            })
    }

    private fun setDashBoardAdapter(responseModelList: MutableList<DashboardResponseModel>) {

        val dashboardAdapter = DashboardAdapter(context!!, responseModelList)
        val gridLayoutManager = GridLayoutManager(context!!, 2, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (responseModelList[position].dashboardSpanCount!! == 0 || responseModelList[position].dashboardSpanCount!! > 2) {
                    return 2
                } else {
                    return responseModelList[position].dashboardSpanCount!!
                }
            }
        }
        with(dashboardRV) {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = dashboardAdapter
        }

        dashboardAdapter.onItemClick = { position, model ->
            //dashBoardClickEvent(model?.dashboardRouteUrl!!)
            if (model?.count != 0){
                when (model?.dashboardRouteUrl) {
                    "add-order" -> {
                        addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
                    }
                    "billing-service" -> {
                        addFragment(BillingofServiceFragment.newInstance(), BillingofServiceFragment.tag)
                    }
                    "order-tracking" -> {
                        addFragment(OrderTrackingFragment.newInstance(""), OrderTrackingFragment.tag)
                    }
                    "shipment-charge" -> {
                        addFragment(ShipmentChargeFragment.newInstance(), ShipmentChargeFragment.tag)
                    }
                    "all-order" -> {
                        goToAllOrder(model)
                    }
                    "cod-collection" -> {
                        addFragment(CODCollectionFragment.newInstance(), CODCollectionFragment.tag)
                    }
                    else -> {
                        addFragment(AllOrdersFragment.newInstance(), AllOrdersFragment.tag)
                    }
                }
            } else {
                VariousTask.showShortToast(context, "বিস্তারিত দেখানোর মতো পর্যাপ্ত তথ্য নেই")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        //(activity as HomeActivity).setToolbarTitle("ড্যাশবোর্ড")
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    private fun goToAllOrder(model: DashboardResponseModel) {

        val calendar = Calendar.getInstance()
        calendar.set(selectedYear,selectedMonth-1,1)
        val lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val bundle = Bundle()
        bundle.putString("statusGroup", model.name)
        bundle.putString("fromDate", "$selectedYear-$selectedMonth-01")
        bundle.putString("toDate", "$selectedYear-$selectedMonth-$lastDate")
        bundle.putString("dashboardStatusFilter", model.dashboardStatusFilter)

        val fragment = AllOrdersFragment.newInstance(bundle)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
        ft?.addToBackStack(AllOrdersFragment.tag)
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
