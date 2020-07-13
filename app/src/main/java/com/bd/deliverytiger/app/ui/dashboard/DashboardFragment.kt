package com.bd.deliverytiger.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponseModel
import com.bd.deliverytiger.app.databinding.FragmentDashboardBinding
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.billing_of_service.BillingofServiceFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.payment_statement.PaymentStatementFragment
import com.bd.deliverytiger.app.ui.shipment_charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.utils.*
import com.bd.deliverytiger.app.utils.DigitConverter.banglaMonth
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.koin.android.ext.android.inject
import java.util.*

class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null

    private lateinit var dashboardAdapter: DashboardAdapter
    private val responseModelList: MutableList<DashboardResponseModel> = mutableListOf()
    private val viewModel: DashboardViewModel by inject()

    private var currentYear = 0
    private var selectedYear = 0
    private var selectedMonth = 0
    private var isLoading = false

    companion object {
        fun newInstance(): DashboardFragment = DashboardFragment().apply {}
        val tag: String = DashboardFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDashboardBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setDashBoardAdapter()
        setSpinner()

        binding?.bannerImage?.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(AppConstant.TEST_BANNER)
            .apply(RequestOptions().placeholder(R.drawable.ic_banner_place))
            .into(binding?.bannerImage!!)

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                        isLoading = true
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        isLoading = false
                    }
                }
            }
        })
    }

    private fun setDashBoardAdapter() {

        dashboardAdapter = DashboardAdapter(requireContext(), responseModelList)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (responseModelList[position].dashboardSpanCount!! == 0 || responseModelList[position].dashboardSpanCount!! > 2) {
                    2
                } else {
                    responseModelList[position].dashboardSpanCount!!
                }
            }
        }
        with(binding?.recyclerview!!) {
            setHasFixedSize(false)
            layoutManager = gridLayoutManager
            adapter = dashboardAdapter
        }

        dashboardAdapter.onItemClick = { _, model ->
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
                        goToAllOrder(model.name ?: "", model.dashboardStatusFilter)
                    }
                    "cod-collection" -> {
                        addFragment(CODCollectionFragment.newInstance(), CODCollectionFragment.tag)
                    }
                    else -> {
                        addFragment(AllOrdersFragment.newInstance(), AllOrdersFragment.tag)
                    }
                }
            } else {
                VariousTask.showShortToast(context, "পর্যাপ্ত তথ্য নেই")
            }
        }

        binding?.paymentInfoLayout?.setOnClickListener {
            goToAllOrder("পেমেন্ট রেডি", "পেমেন্ট রেডি")
        }

        binding?.paymentDoneLayout?.setOnClickListener {
            addFragment(PaymentStatementFragment.newInstance(), PaymentStatementFragment.tag)
        }

    }

    private fun setSpinner() {
        val calender = Calendar.getInstance()
        currentYear = calender.get(Calendar.YEAR)
        val currentMonth = calender.get(Calendar.MONTH)
        selectedYear = currentYear
        selectedMonth = currentMonth + 1
        getDashBoardData(selectedMonth, selectedYear)

        val list: MutableList<MonthDataModel> = mutableListOf()
        val viewList: MutableList<String> = mutableListOf()
        for (year in currentYear downTo 2019){
            //Timber.d("DashboardTag", "year: $year")
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

        val packagingAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, viewList)
        binding?.monthSpinner?.adapter = packagingAdapter
        binding?.monthSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        val dashBoardReqBody = DashBoardReqBody(selectedMonth, selectedYear, SessionManager.courierUserId)
        Timber.d("DashboardTag r ", dashBoardReqBody.toString())
        viewModel.getDashboardStatusGroup(dashBoardReqBody).observe(viewLifecycleOwner, Observer {
            responseModelList.clear()
            responseModelList.addAll(it)
            dashboardAdapter.notifyDataSetChanged()
        })
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    private fun goToAllOrder(statusGroupName: String, statusFilter: String) {

        val calendar = Calendar.getInstance()
        calendar.set(selectedYear,selectedMonth-1,1)
        val lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val bundle = Bundle()
        bundle.putString("statusGroup", statusGroupName)
        bundle.putString("fromDate", "$selectedYear-$selectedMonth-01")
        bundle.putString("toDate", "$selectedYear-$selectedMonth-$lastDate")
        bundle.putString("dashboardStatusFilter", statusFilter)

        val fragment = AllOrdersFragment.newInstance(bundle)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
        ft?.addToBackStack(AllOrdersFragment.tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}
