package com.bd.deliverytiger.app.ui.service_charge


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceReqBody
import com.bd.deliverytiger.app.api.model.billing_service.CourierOrderAmountDetail
import com.bd.deliverytiger.app.databinding.FragmentServiceChargeBinding
import com.bd.deliverytiger.app.ui.filter.FilterFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject
import java.util.*

class ServiceChargeFragment : Fragment() {

    private var binding: FragmentServiceChargeBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalOrder: TextView
    private lateinit var billingFilterLay: LinearLayout
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var emptyView: ImageView
    private lateinit var filterGroup: ChipGroup
    private lateinit var filterDateTag: Chip
    private lateinit var filterStatusTag: Chip
    private lateinit var filterSearchKeyTag: Chip

    private lateinit var dataChargeAdapter: ServiceChargeAdapter

    private var isLoading = false
    private val visibleThreshold = 6
    private var totalCount = 0




    private var courierOrderAmountDetailList: MutableList<CourierOrderAmountDetail> = mutableListOf()
    private var defaultDate = "2001-01-01"
    private var fromDate = "2001-01-01"
    private var toDate = "2001-01-01"
    private var status = -1
    private var statusGroup = "-1"
    private var orderId = ""
    private var mobileNumber = ""
    private var collectionName = ""
    private var searchKeys = ""
    private var searchTypes = 0
    private var isMoreDataAvailable = true
    private val statusList: MutableList<Int> = mutableListOf(-1)
    private val statusGroupList: MutableList<String> = mutableListOf("-1")

    private var selectedMonthIndex: Int = 0
    private var selectedYear: Int = 0

    private var currentFilter: Int = 1
    private val dataList: MutableList<CourierOrderAmountDetail> = mutableListOf()

    private val viewModel: ServiceChargeViewModel by inject()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentServiceChargeBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        tvTotalOrder = view.findViewById(R.id.tvTotalOrder)
        billingFilterLay = view.findViewById(R.id.allOrderFilterLay)
        emptyView = view.findViewById(R.id.emptyView)
        //topLay = view.findViewById(R.id.topLay)
        filterGroup = view.findViewById(R.id.filter_tag_group)
        filterDateTag = view.findViewById(R.id.filter_tag_date)
        filterStatusTag = view.findViewById(R.id.filter_tag_status)
        filterSearchKeyTag = view.findViewById(R.id.filter_tag_searchKey)

        val calender = Calendar.getInstance()
        val currentYear = calender.get(Calendar.YEAR)

        val monthList: MutableList<String> = mutableListOf()
        val yearList: MutableList<String> = mutableListOf()
        for (year in currentYear downTo 2019) {
            yearList.add(DigitConverter.toBanglaDigit(year))
        }
        for (monthIndex in 0..11) {
            monthList.add(DigitConverter.banglaMonth[monthIndex])
        }

        //calender.add(Calendar.MONTH, -1)
        val previousMonth = calender.get(Calendar.MONTH)
        selectedYear = calender.get(Calendar.YEAR)
        selectedMonthIndex = previousMonth
        generateDateRange(selectedYear, selectedMonthIndex)
        timber.log.Timber.d("selectedYear $selectedYear, selectedMonthIndex $selectedMonthIndex")

        val monthAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, monthList)
        binding?.spinnerMonth?.adapter = monthAdapter
        binding?.spinnerMonth?.setSelection(selectedMonthIndex)

        val yearAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, yearList)
        binding?.spinnerYear?.adapter = yearAdapter
        binding?.spinnerYear?.setSelection(yearList.indexOf(selectedYear.toString()))

        Handler(Looper.getMainLooper()).postDelayed({
            binding?.spinnerMonth?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                    if (view != null) {
                        selectedMonthIndex = position
                        generateDateRange(selectedYear, selectedMonthIndex)
                        fetchServiceBillDetails(0, 20)
                        //Timber.d("serviceChargeLog","selectedMonthIndex $selectedMonthIndex")
                    }
                }
            }
            binding?.spinnerYear?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                    if (view != null) {
                        selectedYear = yearList[position].toInt()
                        fetchServiceBillDetails(0, 20)
                        //Timber.d("serviceChargeLog","selectedYear $selectedYear")
                    }
                }
            }
        },300L)

        linearLayoutManager = LinearLayoutManager(requireContext())
        dataChargeAdapter = ServiceChargeAdapter()
        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = dataChargeAdapter
            //addItemDecoration(DividerItemDecoration(rvBillingService.getContext(), DividerItemDecoration.VERTICAL))
        }

        dataChargeAdapter.onItemClick = { model, position ->
            addOrderTrackFragment(model.courierOrdersId.toString())
        }

        dataChargeAdapter.onPaymentClick = { model, position ->
            model.courierOrdersId?.let {
                paymentGateway(it)
            }
        }



        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //Timber.e("layoutPosition", "$layoutPosition $totalLoadedData $isLoading $totalCount $isMoreDataAvailable")
                if (dy > 0) {
                    //val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val currentItemCount = dataList.size
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (!isLoading && currentItemCount <= lastVisibleItem + visibleThreshold && currentItemCount < totalCount) {
                        isLoading = true
                        fetchServiceBillDetails(currentItemCount, 20)
                    }
                }
            }
        })

        billingFilterLay.setOnClickListener {
            goToFilter()
        }

        viewModel.pagingState.observe(viewLifecycleOwner, Observer { model ->
            isLoading = false
            if (model.isInitLoad) {
                totalCount = model.totalCount
                val totalAmount = model.totalAmount

                val msg = "মোট পার্সেলঃ <font color='#CC000000'><b>${DigitConverter.toBanglaDigit(totalCount)}</b></font> টি"
                tvTotalOrder.text = HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY)

                //val amountMsg = "COD: <font color='#E6000000'><b>৳ ${DigitConverter.toBanglaDigit(model.totalAmountDeliveryTakaCollection)}</b></font> | Only Delivery: <font color='#E6000000'><b>৳ ${DigitConverter.toBanglaDigit(model.totalAmountOnlyDelivery)}</b></font>"
                ///binding?.includeFilter?.totalAmount?.text = HtmlCompat.fromHtml(amountMsg, HtmlCompat.FROM_HTML_MODE_LEGACY)
                //binding?.includeFilter?.totalAmount?.visibility = View.VISIBLE
                dataList.clear()
                dataList.addAll(model.dataList)
                filterData(dataList, currentFilter)

                binding?.filterTab?.getTabAt(0)?.text = "COD (৳ ${DigitConverter.toBanglaDigit(model.totalAmountDeliveryTakaCollection)})"
                binding?.filterTab?.getTabAt(1)?.text = "Only Delivery (৳ ${DigitConverter.toBanglaDigit(model.totalAmountOnlyDelivery)})"

            } else {
                dataList.addAll(model.dataList)
                filterData(model.dataList, currentFilter, true)
                //dataChargeAdapter.pagingLoad(model.dataList)
            }
        })

        binding?.filterTab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        currentFilter = 1
                        filterData(dataList, currentFilter)
                    }
                    1 -> {
                        currentFilter = 2
                        filterData(dataList, currentFilter)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun filterData(list: List<CourierOrderAmountDetail>, flag: Int, isLazyLoad: Boolean = false) {
        var filterList: List<CourierOrderAmountDetail> = listOf()
        if (flag == 1) {
            filterList = list.filter { it.collectionAmount > 0.0 }
        } else if (flag == 2) {
            filterList = list.filter { it.collectionAmount == 0.0 }
        }
        if (isLazyLoad) {
            dataChargeAdapter.pagingLoad(filterList)
        } else {
            if (filterList.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
                dataChargeAdapter.clear()
            } else {
                binding?.emptyView?.visibility = View.GONE
                dataChargeAdapter.initLoad(filterList)
            }
        }
    }

    private fun generateDateRange(year: Int, monthIndex: Int) {

        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, monthIndex)
        val lastDay = calender.getActualMaximum(Calendar.DAY_OF_MONTH)

        fromDate = "$year-${monthIndex+1}-01"
        toDate = "$year-${monthIndex+1}-$lastDay"

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.service_charge))
        fetchServiceBillDetails(0, 20)
    }

    private fun fetchServiceBillDetails(index: Int, count: Int) {

        val requestBody = BillingServiceReqBody(
            status, statusList, statusGroupList, fromDate, toDate, SessionManager.courierUserId,
            "", orderId, collectionName, mobileNumber, index, count
        )
        viewModel.fetchServiceBillDetails(requestBody, index)
    }

    private fun addOrderTrackFragment(orderId: String) {

        val bundle = bundleOf("orderID" to orderId)
        findNavController().navigate(R.id.nav_allOrder_orderTracking, bundle)

        /*val fragment = OrderTrackingFragment.newInstance(orderId)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, OrderTrackingFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()*/
    }

    private fun paymentGateway(orderId: String) {

        val url = "${AppConstant.GATEWAY}?CID=$orderId"
        val bundle = bundleOf(
            "url" to url,
            "title" to getString(R.string.payment)
        )
        findNavController().navigate(R.id.nav_web_view, bundle)

        /*val fragment = WebViewFragment.newInstance(url, "পেমেন্ট")
        val tag = WebViewFragment.tag
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()*/
    }

    private fun goToFilter() {

        activity?.let {
            (activity as HomeActivity).openRightDrawer()
        }

        val fragment = FilterFragment.newInstance(fromDate, toDate, status, statusGroup, searchKeys)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container_drawer, fragment, FilterFragment.tag)
        //ft?.addToBackStack(FilterFragment.tag)
        ft?.commit()

        fragment.setFilterListener(object : FilterFragment.FilterListener {
            override fun selectedDate(fromDate1: String, toDate1: String, status1: Int, statusGroup1: String, searchKey: String, searchType: Int, orderType: String) {
                fromDate = fromDate1
                toDate = toDate1
                status = status1
                statusGroup = statusGroup1
                statusGroupList.clear()
                statusGroupList.add(statusGroup1)

                searchKeys = searchKey
                searchTypes = searchType

                if (searchType == 0) {
                    mobileNumber = ""
                    collectionName = ""
                    orderId = ""
                }

                when (searchType) {
                    1 -> {
                        mobileNumber = searchKey
                    }
                    2 -> {
                        orderId = searchKey
                    }
                    3 -> {
                        collectionName = searchKey
                    }
                }

                val msg = "${DigitConverter.toBanglaDate(fromDate1, "yyyy-MM-dd")} - ${DigitConverter.toBanglaDate(toDate1, "yyyy-MM-dd")}"
                filterDateTag.text = msg
                filterDateTag.visibility = View.VISIBLE

                /*if (fromDate1 != defaultDate) {

                } else {
                    filterDateTag.text = ""
                    filterDateTag.visibility = View.GONE
                    generateDateRange(selectedYear, selectedMonthIndex)
                }*/

                if (statusGroup != "-1") {
                    filterStatusTag.text = statusGroup1
                    filterStatusTag.visibility = View.VISIBLE
                } else {
                    filterStatusTag.text = ""
                    filterStatusTag.visibility = View.GONE
                    status = -1
                    statusGroup = "-1"
                    statusGroupList.clear()
                    statusGroupList.add(statusGroup)
                }

                if (searchTypes != 0) {
                    filterSearchKeyTag.text = searchKeys
                    filterSearchKeyTag.visibility = View.VISIBLE
                } else {
                    filterSearchKeyTag.text = ""
                    filterSearchKeyTag.visibility = View.GONE

                }

                filterDateTag.setOnClickListener {
                    filterDateTag.text = ""
                    filterDateTag.visibility = View.GONE
                    generateDateRange(selectedYear, selectedMonthIndex)

                    courierOrderAmountDetailList.clear()
                    dataChargeAdapter.notifyDataSetChanged()
                    fetchServiceBillDetails(0, 20)
                }

                filterStatusTag.setOnClickListener {
                    filterStatusTag.text = ""
                    filterStatusTag.visibility = View.GONE
                    status = -1
                    statusGroup = "-1"
                    statusGroupList.clear()
                    statusGroupList.add(statusGroup)

                    courierOrderAmountDetailList.clear()
                    dataChargeAdapter.notifyDataSetChanged()
                    fetchServiceBillDetails(0, 20)
                }

                filterDateTag.setOnCloseIconClickListener {
                    filterDateTag.performClick()
                }

                filterStatusTag.setOnCloseIconClickListener {
                    filterStatusTag.performClick()
                }

                filterSearchKeyTag.setOnClickListener {
                    filterSearchKeyTag.text = ""
                    filterSearchKeyTag.visibility = View.GONE
                    generateDateRange(selectedYear, selectedMonthIndex)
                    status = -1
                    statusGroup = "-1"
                    statusGroupList.clear()
                    statusGroupList.add(statusGroup)
                    mobileNumber = ""
                    collectionName = ""
                    orderId = ""
                    searchKeys = ""
                    searchTypes = 0

                    courierOrderAmountDetailList.clear()
                    dataChargeAdapter.notifyDataSetChanged()
                    fetchServiceBillDetails(0, 20)
                }

                filterSearchKeyTag.setOnCloseIconClickListener {
                    filterSearchKeyTag.performClick()
                }

                courierOrderAmountDetailList.clear()
                dataChargeAdapter.notifyDataSetChanged()
                fetchServiceBillDetails(0, 20)

                activity?.onBackPressed()
            }
        })
    }


}
