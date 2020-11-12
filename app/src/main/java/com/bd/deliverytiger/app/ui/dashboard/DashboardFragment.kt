package com.bd.deliverytiger.app.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.config.BannerModel
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.databinding.FragmentDashboardBinding
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.balance_load.BalanceLoadFragment
import com.bd.deliverytiger.app.ui.banner.SliderAdapter
import com.bd.deliverytiger.app.ui.charge_calculator.DeliveryChargeCalculatorFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.collector_tracking.MapFragment
import com.bd.deliverytiger.app.ui.complain.ComplainFragment
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.payment_details.PaymentDetailsFragment
import com.bd.deliverytiger.app.ui.payment_statement.PaymentStatementFragment
import com.bd.deliverytiger.app.ui.quick_order.QuickOrderFragment
import com.bd.deliverytiger.app.ui.service_charge.ServiceChargeFragment
import com.bd.deliverytiger.app.ui.shipment_charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.ui.unpaid_cod.UnpaidCODFragment
import com.bd.deliverytiger.app.utils.*
import com.bd.deliverytiger.app.utils.DigitConverter.banglaMonth
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null

    private lateinit var dashboardAdapter: DashboardAdapter
    private val dataList: MutableList<DashboardData> = mutableListOf()
    private val viewModel: DashboardViewModel by inject()
    private val homeViewModel: HomeViewModel by inject()
    private lateinit var monthSpinnerAdapter: CustomSpinnerAdapter

    private val calenderNow = Calendar.getInstance()
    private val viewList: MutableList<String> = mutableListOf()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var dayOfYear = 0
    private var today = 0
    private var currentMonth = 0
    private var currentYear = 0
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedStartDate = ""
    private var selectedEndDate = ""
    private var currentDate = ""
    private var freezeDate = ""

    private var isBannerEnable: Boolean = false
    private var worker: Runnable? = null
    private var handler = Handler(Looper.getMainLooper())
    private var paymentDashboardModel: DashboardData = DashboardData(dashboardSpanCount = 1, viewType = 1)


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
        showDeliveryChargeCalculator()

        fetchAccountsData()
        fetchCollection()

        binding?.swipeRefresh?.setOnRefreshListener {
            getDashBoardData(selectedMonth, selectedYear)
            fetchCollection()
        }
        binding?.retryBtn?.setOnClickListener {
            getDashBoardData(selectedMonth, selectedYear)
        }

        homeViewModel.bannerInfo.observe(viewLifecycleOwner, Observer { model ->
            val bannerModel = model.bannerModel
            showBanner(bannerModel)
        })

        binding?.collectorTrackBtn?.setOnClickListener {
            addFragment(MapFragment.newInstance(null), MapFragment.tag)
        }

        binding?.paymentDoneLayout?.setOnClickListener {
            addFragment(PaymentStatementFragment.newInstance(), PaymentStatementFragment.tag)
        }

        binding?.nearByHubBtn?.setOnClickListener {
            val bundle = bundleOf(
                "isNearByHubView" to true
            )
            addFragment(MapFragment.newInstance(bundle), MapFragment.tag)
        }

        binding?.complainBtn?.setOnClickListener {
            addFragment(ComplainFragment.newInstance(), ComplainFragment.tag)
        }

        binding?.unpaidLayout?.setOnClickListener {
            //goToAllOrder("ডেলিভারি হয়েছে", "ডেলিভারি হয়েছে", selectedStartDate, selectedEndDate)
            addFragment(UnpaidCODFragment.newInstance(), UnpaidCODFragment.tag)
        }

        binding?.balanceLoadLayout?.setOnClickListener {
            addFragment(BalanceLoadFragment.newInstance(), BalanceLoadFragment.tag)
        }

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
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (isBannerEnable) {
            animateSlider()
        }
    }

    override fun onPause() {
        super.onPause()
        worker?.let {
            handler.removeCallbacks(it)
        }
    }

    private fun showDeliveryChargeCalculator() {
        val tag = DeliveryChargeCalculatorFragment.tag
        val fragment = DeliveryChargeCalculatorFragment.newInstance()
        binding?.container?.let { container ->
            childFragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit()
        }
    }

    private fun showBanner(bannerModel: BannerModel) {

        isBannerEnable = bannerModel.showBanner
        if (isBannerEnable) {
            binding?.sliderView?.visibility = View.VISIBLE

            val bannerList = bannerModel.bannerData.filter { it.isActive }
            val bannerUrlList = bannerList.map { it.bannerUrl ?: "" }

            val sliderAdapter = SliderAdapter()
            sliderAdapter.initList(bannerUrlList)
            binding?.sliderView?.setSliderAdapter(sliderAdapter)

            binding?.sliderView?.let { view ->
                with(view) {
                    setIndicatorAnimation(IndicatorAnimationType.WORM)
                    indicatorSelectedColor = Color.WHITE
                    indicatorUnselectedColor = Color.GRAY
                    setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    scrollTimeInSec = 1
                    autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
                    //startAutoCycle()
                }
            }

            animateSlider()

        } else {
            binding?.sliderView?.visibility = View.GONE
        }
    }

    private fun animateSlider() {
        worker?.let {
            handler.removeCallbacks(it)
        }
        worker = object : Runnable {
            override fun run() {
                binding?.sliderView?.slideToNextPosition()
                handler.postDelayed(this, 2000L)
            }
        }
        handler.postDelayed(worker!!, 2000L)
    }

    private fun setDashBoardAdapter() {

        dashboardAdapter = DashboardAdapter(requireContext(), dataList)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (dataList[position].dashboardSpanCount!! == 0 || dataList[position].dashboardSpanCount!! > 2) {
                    2
                } else {
                    dataList[position].dashboardSpanCount!!
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
            if (model?.count != 0) {
                when (model?.dashboardRouteUrl) {
                    "add-order" -> {
                        addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
                    }
                    "billing-service" -> {
                        addFragment(ServiceChargeFragment.newInstance(), ServiceChargeFragment.tag)
                    }
                    "order-tracking" -> {
                        addFragment(OrderTrackingFragment.newInstance(""), OrderTrackingFragment.tag)
                    }
                    "shipment-charge" -> {
                        addFragment(ShipmentChargeFragment.newInstance(), ShipmentChargeFragment.tag)
                    }
                    "all-order" -> {
                        goToAllOrder(model.name ?: "", model.dashboardStatusFilter, selectedStartDate, selectedEndDate)
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

        dashboardAdapter.onPayDetailsClick = { position, model ->
            if (model.totalAmount.toInt() > 0) {
                goToPaymentDetails()
            } else {
                context?.toast("পর্যাপ্ত তথ্য নেই")
            }
        }

        binding?.orderBtn?.setOnClickListener {

            orderDialog()
        }

        binding?.dateRangePicker?.setOnClickListener {

            val builder = MaterialDatePicker.Builder.dateRangePicker()
            builder.setTheme(R.style.CustomMaterialCalendarTheme)
            builder.setTitleText("ডেট রেঞ্জ সিলেক্ট করুন")
            val picker = builder.build()
            picker.show(childFragmentManager, "Picker")
            picker.addOnPositiveButtonClickListener {

                selectedStartDate = sdf.format(it.first)
                selectedEndDate = sdf.format(it.second)
                selectedMonth = 0
                selectedYear = 0

                val sdf1 = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                //context?.toast("$selectedStartDate $selectedEndDate")
                viewList[0] = DigitConverter.toBanglaDigit("${sdf1.format(it.first)} - ${sdf1.format(it.second)}")
                monthSpinnerAdapter.notifyDataSetChanged()
                binding?.monthSpinner?.setSelection(0)

                getDashBoardData(selectedMonth, selectedYear)
            }
        }

    }

    private fun setSpinner() {
        //val calender = Calendar.getInstance()
        currentYear = calenderNow.get(Calendar.YEAR)
        dayOfYear = calenderNow.get(Calendar.DAY_OF_YEAR)
        today = calenderNow.get(Calendar.DAY_OF_MONTH)
        currentMonth = calenderNow.get(Calendar.MONTH)
        val lastActualDay = calenderNow.getActualMaximum(Calendar.DAY_OF_MONTH)
        currentDate = sdf.format(calenderNow.timeInMillis)
        selectedYear = currentYear
        selectedMonth = currentMonth + 1
        selectedStartDate = "$selectedYear-$selectedMonth-01"
        selectedEndDate = "$selectedYear-$selectedMonth-$lastActualDay"

        getDashBoardData(selectedMonth, selectedYear)
        Timber.d("DashboardTag", "fetchDashBoard $selectedMonth $selectedYear")

        val list: MutableList<MonthDataModel> = mutableListOf()
        viewList.clear()
        viewList.add(getString(R.string.dashboard_spinner_temp))
        for (year in currentYear downTo 2019) {
            //Timber.d("DashboardTag", "year: $year")
            var lastMonth = 11
            if (year == currentYear) {
                lastMonth = currentMonth
            }
            for (monthIndex in lastMonth downTo 0) {
                if (year == 2019 && monthIndex == 6) {
                    break
                }
                list.add(MonthDataModel(monthIndex + 1, year))
                viewList.add("${banglaMonth[monthIndex]}, ${DigitConverter.toBanglaDigit(year)}")
            }
        }

        monthSpinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, viewList)
        binding?.monthSpinner?.adapter = monthSpinnerAdapter
        binding?.monthSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (position > 0) {
                    val model = list[position - 1]
                    if (selectedMonth != model.monthId) {
                        selectedYear = model.year
                        selectedMonth = model.monthId

                        val calendar = Calendar.getInstance()
                        calendar.set(selectedYear, selectedMonth - 1, 1)
                        val lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        selectedStartDate = "$selectedYear-$selectedMonth-01"
                        selectedEndDate = "$selectedYear-$selectedMonth-$lastDate"

                        getDashBoardData(model.monthId, model.year)
                        Timber.d("DashboardTag", "fetchDashBoard ${model.monthId} $currentYear")
                    }
                }
            }
        }
        handler.postDelayed({
            binding?.monthSpinner?.setSelection(1)
        }, 300L)
    }

    private fun fetchCollection() {

        viewModel.fetchCollection(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->

            val currentDate = "${DigitConverter.toBanglaDigit(today)} ${DigitConverter.banglaMonth[currentMonth]}"
            binding?.msg3?.text = "আজকে ($currentDate) পার্সেল দিয়েছি"
            binding?.amount3?.text = "${DigitConverter.toBanglaDigit(model.count.toString())}টি"

            //model.count = 0
            if (model.count == 0) {
                // I already change this design 5 times, Do you think I care about view ID name any more?
                // If you judge me, Go fuck yourself!
                binding?.collectionLayout?.visibility = View.GONE
                binding?.collectorLayout?.visibility = View.VISIBLE

                if (SessionManager.collectorAttendanceDateOfYear != dayOfYear) {
                    SessionManager.isCollectorAttendance = false
                    SessionManager.collectorAttendanceDateOfYear = 0
                }
                if (SessionManager.isCollectorAttendance) {
                    binding?.switchCollector?.isChecked = true
                    binding?.switchCollector?.isEnabled = false
                }

                binding?.switchCollector?.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.updateStatusLocation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer {
                            if (it) {
                                context?.toast("সফলভাবে আপডেট হয়েছে")
                                SessionManager.isCollectorAttendance = true
                                SessionManager.collectorAttendanceDateOfYear = dayOfYear
                                binding?.switchCollector?.isEnabled = false
                            }
                        })
                    }
                }
            } else {
                SessionManager.isCollectorAttendance = false
                SessionManager.collectorAttendanceDateOfYear = 0
            }
        })
    }

    private fun fetchAccountsData() {

        viewModel.fetchAccountsData(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->

            /*if (!model.paymentDate.isNullOrEmpty()) {
                val banglaDate = DigitConverter.toBanglaDate(model.paymentDate!!,"MM/dd/yyyy")
                binding?.msg2?.text = "($banglaDate)"
                freezeDate = DigitConverter.formatDate(model.freezeDate!!, "MM/dd/yyyy", "yyyy-MM-dd")
            }
            binding?.amount1?.text = "৳ ${DigitConverter.toBanglaDigit(model.totalAmount.toInt(), true)}"
            binding?.msg1?.text = "${model.name}"*/

            /*binding?.paymentInfoLayout?.setOnClickListener {
                if (model.totalAmount.toInt() > 0) {
                    goToPaymentDetails()
                } else {
                    context?.toast("পর্যাপ্ত তথ্য নেই")
                }
            }*/

            paymentDashboardModel.apply {
                this.name = model.name
                this.paymentDate = model.paymentDate ?: ""
                this.totalAmount = model.totalAmount
            }
            if (dataList.isNotEmpty()) {
                dataList.last().apply {
                    this.name = model.name
                    this.paymentDate = model.paymentDate ?: ""
                    this.totalAmount = model.totalAmount
                }
                dashboardAdapter.notifyItemChanged(dataList.lastIndex)
            }
        })
    }

    private fun getDashBoardData(selectedMonth: Int, selectedYear: Int) {

        val dashBoardReqBody = DashBoardReqBody(selectedMonth, selectedYear, selectedStartDate, selectedEndDate, SessionManager.courierUserId)
        Timber.d("DashboardTag r ", dashBoardReqBody.toString())

        viewModel.getDashboardStatusGroup(dashBoardReqBody).observe(viewLifecycleOwner, Observer { list ->

            binding?.swipeRefresh?.isRefreshing = false
            dataList.clear()
            dataList.addAll(list.filter { it.statusGroupId != 12 })
            dataList.add(paymentDashboardModel)
            dashboardAdapter.notifyDataSetChanged()

            if (list.isEmpty()) {
                binding?.retryBtn?.visibility = View.VISIBLE
            } else {
                binding?.retryBtn?.visibility = View.GONE
            }

        })
    }

    private fun orderDialog() {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order_type,null)
        builder.setView(view)
        val button1: MaterialButton = view.findViewById(R.id.button1)
        val button2: MaterialButton = view.findViewById(R.id.button2)
        val dialog = builder.create()
        dialog.show()
        button1.setOnClickListener {
            dialog.dismiss()
            addFragment(QuickOrderFragment.newInstance(), QuickOrderFragment.tag)
        }
        button2.setOnClickListener {
            dialog.dismiss()
            addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
        }
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    private fun goToAllOrder(statusGroupName: String, statusFilter: String, startDate: String, endDate: String) {

        val bundle = Bundle()
        bundle.putString("statusGroup", statusGroupName)
        bundle.putString("fromDate", startDate)
        bundle.putString("toDate", endDate)
        bundle.putString("dashboardStatusFilter", statusFilter)

        val fragment = AllOrdersFragment.newInstance(bundle)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
        ft?.addToBackStack(AllOrdersFragment.tag)
        ft?.commit()
    }

    private fun goToPaymentDetails() {

        val tag = PaymentDetailsFragment.tag
        val fragment = PaymentDetailsFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}
