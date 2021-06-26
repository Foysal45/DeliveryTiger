package com.bd.deliverytiger.app.ui.dashboard

import android.annotation.SuppressLint
import android.app.ActionBar
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.HubInfo
import com.bd.deliverytiger.app.api.model.collector_info.CollectorInfoRequest
import com.bd.deliverytiger.app.api.model.config.BannerModel
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnCountResponseItem
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnedCountRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsRequest
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.databinding.FragmentDashboardBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationType
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.banner.SliderAdapter
import com.bd.deliverytiger.app.ui.bill_pay.ServiceBillPayFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.collection_history.CollectionHistoryFragment
import com.bd.deliverytiger.app.ui.collector_tracking.MapFragment
import com.bd.deliverytiger.app.ui.complain.ComplainFragment
import com.bd.deliverytiger.app.ui.delivery_details.DeliveryDetailsFragment
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.payment_details.PaymentDetailsFragment
import com.bd.deliverytiger.app.ui.quick_order.QuickBookingBottomSheet
import com.bd.deliverytiger.app.ui.referral.ReferralFragment
import com.bd.deliverytiger.app.ui.service_charge.ServiceChargeFragment
import com.bd.deliverytiger.app.ui.shipment_charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.ui.unpaid_cod.UnpaidCODFragment
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null

    private lateinit var dashboardAdapter: DashboardAdapter
    private val dataList: MutableList<DashboardData> = mutableListOf()
    private val returnDataList: MutableList<DashboardData> = mutableListOf()
    private var dateRangeFilterList: MutableList<DeliveredReturnCountResponseItem> = mutableListOf()
    private val viewModel: DashboardViewModel by inject()
    private val homeViewModel: HomeViewModel by inject()
    //private lateinit var monthSpinnerAdapter: CustomSpinnerAdapter

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
    private var defaultFromDate = "2001-01-01"
    private var defaultToDate = "2001-01-01"
    private var fromDate = "2001-01-01"
    private var toDate = "2001-01-01"

    private var showOrderPopup: Boolean = false
    private var instantPaymentOTPLimit: Int = 0
    private var instantPaymentHourLimit: String = "12-24"

    private var isOTPRequested: Boolean = false
    private var netAmount: Int = 0
    private var availability: Boolean = false
    private var availabilityMessage: String = ""

    private var collectionToday: Int = 0
    private var isQuickBookingEnable: Boolean = false

    private var isBannerEnable: Boolean = false
    private var worker: Runnable? = null
    private var handler = Handler(Looper.getMainLooper())
    private var paymentDashboardModel: DashboardData = DashboardData(dashboardSpanCount = 2, viewType = 1)

    companion object {
        fun newInstance(): DashboardFragment = DashboardFragment().apply {}
        val tag: String = DashboardFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDashboardBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDashboard()
        getCourierUsersInformation()
        fetchBannerData()
        fetchCODData()
        fetchCollection()
        //fetchCurrentBalance()
        initClickLister()
        //showDeliveryChargeCalculator()
        //fetchAccountsData()
        manageDeliveryReturnDashboard()
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

    private fun manageDeliveryReturnDashboard(){

        //Fetch 7 days data
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        toDate = simpleDateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        fromDate = simpleDateFormat.format(calendar.time)
        setDateRangePickerTitle()
        val requestBody = DeliveredReturnedCountRequest(fromDate, toDate, SessionManager.courierUserId)
        fetchDeliveredReturnCount(requestBody)

        binding?.dateRangePicker?.setOnClickListener {
            dateRangePicker()
        }

        binding?.clearDateRangeImage?.setOnClickListener {
            fromDate = ""
            toDate = ""
            dateRangeFilterList.clear()
            binding?.filterCountDelivery?.text = "${DigitConverter.toBanglaDigit(0)} টি"
            binding?.filterCountReturn?.text = "${DigitConverter.toBanglaDigit(0)} টি"
            binding?.dateRangePicker?.text = ""
            binding?.clearDateRangeImage?.visibility = View.GONE
        }

        binding?.deliveryFilterLayout?.setOnClickListener {
            if(dateRangeFilterList.isEmpty()){
                Toast.makeText(requireContext(), "কোনো তথ্য পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            }else{
                if (dateRangeFilterList.first().delivered != 0){
                    val reqBody = DeliveryDetailsRequest(fromDate, toDate, SessionManager.courierUserId,"delivery")
                    addFragment(DeliveryDetailsFragment.newInstance(reqBody, dateRangeFilterList.first().delivered), DeliveryDetailsFragment.tag)
                }else{
                    Toast.makeText(requireContext(), "কোনো তথ্য পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                }
            }

        }
        binding?.deliveredReturnLayout?.setOnClickListener {
            if(dateRangeFilterList.isEmpty()){
                Toast.makeText(requireContext(), "কোনো তথ্য পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            }else{
                if (dateRangeFilterList.first().returned != 0) {
                    val reqBody = DeliveryDetailsRequest(fromDate, toDate, SessionManager.courierUserId, "return")
                    addFragment(DeliveryDetailsFragment.newInstance(reqBody, dateRangeFilterList.first().returned), DeliveryDetailsFragment.tag)
                }else{
                    Toast.makeText(requireContext(), "কোনো তথ্য পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchBannerData() {
        var flag = false
        homeViewModel.bannerInfo.observe(viewLifecycleOwner, Observer { model ->
            if (model != null) {
                if (flag) return@Observer
                flag = true
            }
            Timber.d("showPopupDialog fetchBannerData called")
            //showOrderPopup = model.showOrderPopup
            instantPaymentOTPLimit = model.instantPaymentOTPLimit
            instantPaymentHourLimit = model.instantPaymentHourLimitRange ?: "12-24"

            val bannerModel = model.bannerModel
            showBanner(bannerModel)
            setSpinner(model.dashboardDataDuration)

            if (!model.referBanner.isNullOrEmpty()) {
                binding?.referBtn?.let { view ->
                    Glide.with(requireContext())
                        .load(model.referBanner)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(view)
                }
                binding?.referBtn?.visibility = View.VISIBLE
            }
        })
    }

    private fun initClickLister() {
        binding?.swipeRefresh?.setOnRefreshListener {
            getDashBoardData(selectedMonth, selectedYear)
            fetchCODData()
            fetchCollection()
            //fetchCurrentBalance()
        }
        binding?.retryBtn?.setOnClickListener {
            getDashBoardData(selectedMonth, selectedYear)
        }
        binding?.collectorTrackBtn?.setOnClickListener {
            addFragment(MapFragment.newInstance(null), MapFragment.tag)
        }
        binding?.orderTrackingBtn?.setOnClickListener {
            addFragment(OrderTrackingFragment.newInstance(""), OrderTrackingFragment.tag)
        }
        binding?.complainBtn?.setOnClickListener {
            addFragment(ComplainFragment.newInstance(), ComplainFragment.tag)
        }
        binding?.balanceLoadLayout?.setOnClickListener {
            showQuickOrderBottomSheet()
            /*showQuickOrderDialog()
            if (netAmount >= 0) {
                addFragment(BalanceLoadFragment.newInstance(), BalanceLoadFragment.tag)
            } else {
                serviceChargeDialog()
            }*/
        }
        binding?.orderBtn?.setOnClickListener {
            binding?.progressBar?.visibility = View.VISIBLE
            if (showOrderPopup) {
                orderDialog()
            } else {
                if (netAmount >= 0) {
                    binding?.progressBar?.visibility = View.GONE
                    addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
                    UserLogger.logGenie("AddOrder")
                } else {
                    serviceChargeDialog()
                }
            }
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
                    "return" -> {
                        returnDialog()
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
        dashboardAdapter.onCODCollectionClick = { position, model ->
            if (netAmount == 0) {
                context?.toast("পর্যাপ্ত তথ্য নেই")
            } else {
                addFragment(UnpaidCODFragment.newInstance(), UnpaidCODFragment.tag)
            }
        }
        dashboardAdapter.onPaymentRequestClick = { position, model ->

            if (availability && netAmount > 0) {
                if (netAmount > instantPaymentOTPLimit) {
                    if (!isOTPRequested) {
                        sendOTP()
                    }
                } else {
                    requestPayment()
                }
            } else {
                if (availabilityMessage.isEmpty()) {
                    availabilityMessage = "পর্যাপ্ত তথ্য নেই"
                }
                alert("নির্দেশনা", availabilityMessage, true, "ঠিক আছে", "ক্যানসেল") {
                }.show()
                //binding?.swipeRefresh?.snackbar(availabilityMessage, Snackbar.LENGTH_INDEFINITE, "ঠিক আছে"){}?.show()
            }
        }

        binding?.collectionLayout?.setOnClickListener {
            if (collectionToday > 0) {
                addFragment(CollectionHistoryFragment.newInstance(), CollectionHistoryFragment.tag)
            } else {
                context?.toast("পর্যাপ্ত তথ্য নেই")
            }
        }

        binding?.referBtn?.setOnClickListener {
            addFragment(ReferralFragment.newInstance(), ReferralFragment.tag)
        }

        binding?.callCollectorBtn?.setOnClickListener {
            getRidersOfficeInfo()
        }

       /* binding?.dateRangePicker?.setOnClickListener {
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
        }*/

        /*binding?.paymentDoneLayout?.setOnClickListener {
            addFragment(PaymentStatementFragment.newInstance(), PaymentStatementFragment.tag)
        }*/
        /*binding?.unpaidLayout?.setOnClickListener {
            //goToAllOrder("ডেলিভারি হয়েছে", "ডেলিভারি হয়েছে", selectedStartDate, selectedEndDate)
            addFragment(UnpaidCODFragment.newInstance(), UnpaidCODFragment.tag)
        }*/

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

    private fun showBanner(bannerModel: BannerModel) {

        isBannerEnable = bannerModel.showBanner
        if (isBannerEnable) {
            binding?.sliderView?.visibility = View.VISIBLE

            val bannerList = bannerModel.bannerData.filter { it.isActive }
            val bannerUrlList = bannerList.map { it.bannerUrl ?: "" }

            val sliderAdapter = SliderAdapter()
            sliderAdapter.initList(bannerUrlList)
            binding?.sliderView?.setSliderAdapter(sliderAdapter)
            sliderAdapter.onItemClick = { data, position ->
                val model = bannerList[position]
                Timber.d("sliderViewDebug $model")
                //goToWebView(model.webUrl ?: "")
                if (model.isWebLinkActive) {
                    goToWebView(model.webUrl ?: "")
                }
            }

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
                handler.postDelayed(this, 5000L)
            }
        }
        handler.postDelayed(worker!!, 5000L)
    }

    private fun initDashboard() {

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
    }

    private fun initRetentionManagerData(retentionManagerName: String, retentionManagerNumber: String){

        binding?.retentionManagerImage?.let { view ->
            Glide.with(view)
                .load("https://static.ajkerdeal.com/images/admin_users/${SessionManager.profileImgUri}.jpg")
                .apply(RequestOptions().placeholder(R.drawable.ic_avater_demo).error(R.drawable.ic_avater_demo).circleCrop())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view)
        }

        binding?.retentionManagerName?.text = retentionManagerName
        binding?.retentionManagerNumber?.text = retentionManagerNumber
        binding?.callBtn?.setOnClickListener {
            if (retentionManagerNumber.isNotEmpty()) {
                callHelplineNumber(retentionManagerNumber)
            } else {
                context?.toast("কোনো মোবাইল নম্বর অ্যাড করা হয়নি")
            }
        }
    }

    private fun setSpinner(monthDuration: Int) {
        //val calender = Calendar.getInstance()
        currentYear = calenderNow.get(Calendar.YEAR)
        dayOfYear = calenderNow.get(Calendar.DAY_OF_YEAR)
        today = calenderNow.get(Calendar.DAY_OF_MONTH)
        currentMonth = calenderNow.get(Calendar.MONTH)
        val lastActualDay = calenderNow.getActualMaximum(Calendar.DAY_OF_MONTH)
        currentDate = sdf.format(calenderNow.timeInMillis)

        selectedYear = currentYear
        selectedMonth = currentMonth + 1

        selectedEndDate = "$selectedYear-$selectedMonth-$today"

        val calendarStart =  Calendar.getInstance()
        calendarStart.add(Calendar.MONTH, monthDuration * -1)
        val startYear = calendarStart.get(Calendar.YEAR)
        val startMonth = calendarStart.get(Calendar.MONTH) + 1
        selectedStartDate = "$startYear-$startMonth-01"

        getDashBoardData(selectedMonth, selectedYear)
        Timber.d( "fetchDashBoard $selectedMonth $selectedYear")

        /*val list: MutableList<MonthDataModel> = mutableListOf()
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
        }*/

        /*
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
        */
    }

    private fun getCourierUsersInformation() {
        viewModel.getCourierUsersInformation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            SessionManager.collectionCharge = model.collectionCharge.toInt()
            SessionManager.merchantDistrict = model.districtId
            isQuickBookingEnable = model.isQuickOrderActive
            initRetentionManagerData(model?.adminUsers?.fullName ?: "", model?.adminUsers?.mobile ?: "")
            if (isQuickBookingEnable) {
                binding?.balanceLoadLayout?.visibility = View.VISIBLE
                binding?.orderBtn?.layoutParams?.width = 0
            } else {
                binding?.orderBtn?.layoutParams?.width = LinearLayout.LayoutParams.MATCH_PARENT
                binding?.balanceLoadLayout?.visibility = View.GONE
            }

        })
    }

    private fun getRidersOfficeInfo() {
        viewModel.getRidersOfficeInfo(CollectorInfoRequest(SessionManager.courierUserId)).observe(viewLifecycleOwner, Observer { model ->

            if (model == null) {
                context?.toast("কোনো তথ্য নেই")
            } else {
                if (model.mobile.isNullOrEmpty()){
                    callHelplineNumber(model.officeInfoViewModel?.customerCareMobile ?: "")
                }else{
                    callHelplineNumber(model.mobile ?: "")
                }
            }

        })
    }

    private fun fetchCollection() {

        viewModel.fetchCollection(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->

            collectionToday = model.count
            val currentDate = "${DigitConverter.toBanglaDigit(today)} ${DigitConverter.banglaMonth[currentMonth]}"
            binding?.msg3?.text = "আজকে ($currentDate) পার্সেল দিয়েছি"
            binding?.amount3?.text = "${DigitConverter.toBanglaDigit(collectionToday.toString())}টি"

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

    private fun fetchCODData() {
        viewModel.fetchUnpaidCOD(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->

            netAmount = model.netAdjustedAmount
            SessionManager.netAmount = netAmount
            availability = model.availability
            availabilityMessage = model.availabilityMessage
            //netAmount = 6000
            //availability = true

            paymentDashboardModel.apply {
                this.name = "COD কালেকশন"
                this.totalAmount = model.netAdjustedAmount.toDouble()
                this.availability = model.availability
                this.availabilityMessage = model.availabilityMessage
                this.paymentProcessingTime = instantPaymentHourLimit
            }

            viewModel.fetchDTMerchantInstantPaymentStatus(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model1 ->
                paymentDashboardModel.apply {
                    this.name = "COD কালেকশন"
                    this.totalAmount = model.netAdjustedAmount.toDouble()
                    this.availability = model.availability
                    this.availabilityMessage = model.availabilityMessage
                    this.paymentProcessingTime = instantPaymentHourLimit

                    this.currentRequestDate = model1.currentRequestDate ?: ""
                    this.currentPaymentAmount = model1.currentPaymentAmount
                    this.currentPaymentStatus = model1.currentPaymentStatus
                }
                if (dataList.isNotEmpty()) {
                    dataList.last().apply {
                        this.name = "COD কালেকশন"
                        this.totalAmount = model.netAdjustedAmount.toDouble()
                        this.availability = model.availability
                        this.availabilityMessage = model.availabilityMessage
                        this.paymentProcessingTime = instantPaymentHourLimit

                        this.currentRequestDate = model1.currentRequestDate ?: ""
                        this.currentPaymentAmount = model1.currentPaymentAmount
                        this.currentPaymentStatus = model1.currentPaymentStatus
                    }
                    dashboardAdapter.notifyItemChanged(dataList.lastIndex)
                }
            })


        })
    }

    /*private fun fetchCurrentBalance() {
        viewModel.fetchMerchantCurrentAdvanceBalance(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { accoutBalance ->
            val balance = accoutBalance.balance
            viewModel.fetchMerchantBalanceInfo(SessionManager.courierUserId, balance).observe(viewLifecycleOwner, Observer { balanceinfo ->
                //val adjustBalance = balanceinfo.serviceCharge + balanceinfo.credit + balanceinfo.staticVal + balanceinfo.calculatedCollectionAmount
                val adjustBalance = balanceinfo.adjustBalance
                binding?.accountBalance?.text = "(৳ ${DigitConverter.toBanglaDigit(adjustBalance, true)})"
            })
        })
    }*/

    private fun getDashBoardData(selectedMonth: Int, selectedYear: Int) {

        val dashBoardReqBody = DashBoardReqBody(0, 0, selectedStartDate, selectedEndDate, SessionManager.courierUserId)


        viewModel.getDashboardStatusGroup(dashBoardReqBody).observe(viewLifecycleOwner, Observer { list ->

            binding?.swipeRefresh?.isRefreshing = false
            returnDataList.clear()
            val dashboardList: MutableList<DashboardData> = mutableListOf()
            var returnCount = 0
            list.forEach() {model ->
                if (model.statusGroupId == 4) {
                    dashboardList.add(model)
                }
                if (model.statusGroupId == 9 || model.statusGroupId == 10 || model.statusGroupId == 11) {
                    returnCount += model.count ?: 0
                    returnDataList.add(model)
                }
            }
            val returnData = DashboardData(
                name = "রিটার্নে আছে",
                dashboardSpanCount = 1,
                count = returnCount,
                dashboardViewColorType = "negative",
                dashboardRouteUrl = "return",
                dashboardCountSumView = "count"
            )
            dashboardList.add(returnData)

            dataList.clear()
            dataList.addAll(dashboardList)
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
            addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
            UserLogger.logGenie("NormalOrder")
        }
        button2.setOnClickListener {
            dialog.dismiss()
            addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
            UserLogger.logGenie("DetailOrder")
        }
    }

    private fun returnDialog() {

        val tag = StatusBreakDownBottomSheet.tag
        val dialog = StatusBreakDownBottomSheet.newInstance(returnDataList)
        dialog.show(childFragmentManager, tag)
        dialog.onItemClicked = { model, position ->
            dialog.dismiss()
            if (model.count > 0) {
                goToAllOrder(model.name ?: "", model.dashboardStatusFilter, selectedStartDate, selectedEndDate)
            } else {
                context?.toast("পর্যাপ্ত তথ্য নেই")
            }
        }
        dialog.onMapClick = { model, position ->
            if (model.statusGroupId == 11) {
                dialog.dismiss()
                goToNearByHubMap()
            } else if (model.statusGroupId == 10) {
                dialog.dismiss()
                val hubModel = HubInfo(10, "সেন্ট্রাল হাব", "lalmatia-hub", true, "7/7 Block C Lalmatia Mohammadpur","90.3678406", "23.7544619", "01521427957")
                goToHubMap(hubModel)
            }
        }
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commitAllowingStateLoss()
    }

    private fun goToAllOrder(statusGroupName: String, statusFilter: String, startDate: String, endDate: String) {

        val bundle = Bundle()
        bundle.putString("statusGroup", statusGroupName)
        bundle.putString("fromDate", startDate)
        bundle.putString("toDate", endDate)
        bundle.putString("dashboardStatusFilter", statusFilter)

        val fragment = AllOrdersFragment.newInstance(bundle, true)
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

    private fun requestPayment() {
        //Timber.d("appLog", "InstantPaymentRequest called")
        viewModel.updateInstantPaymentRequest(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { flag ->
            if (flag) {
                fetchCollection()
                context?.toast("আপনার ইন্সট্যান্ট পেমেন্ট রিকোয়েস্টটি গ্রহণ করা হয়েছে")
                //val msg = "আপনার ইন্সট্যান্ট পেমেন্ট রিকোয়েস্টটি গ্রহণ করা হয়েছে। আগামী ${DigitConverter.toBanglaDigit(instantPaymentHourLimit)} ঘন্টার মধ্যে আপনার bKash নাম্বারে (${DigitConverter.toBanglaDigit(SessionManager.bkashNumber)}) ${DigitConverter.toBanglaDigit(netAmount, true)} টাকা পেমেন্ট করা হবে।"
                //alert("নির্দেশনা", msg).show()
            }
        })
    }

    private fun sendOTP() {
        isOTPRequested = true
        val mobileNumber = SessionManager.mobile
        //val mobileNumber = "01728959986"
        viewModel.sendOTP(OTPRequestModel(mobileNumber, mobileNumber)).observe(viewLifecycleOwner, Observer { msg ->
            isOTPRequested = false
            val message = "আপনার অ্যাকাউন্ট ভেরিফিকেশন কোড ${SessionManager.mobile} এই মোবাইল নম্বরে পাঠানো হয়েছে"
            alert("নির্দেশনা", message, true, "ভেরিফাই", "ক্যানসেল"){
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    showOTPVerify()
                }
            }.show()
        })
    }

    private fun verifyOTP(otpCode: String) {

        val mobileNumber = SessionManager.mobile
        //val mobileNumber = "01728959986"
        viewModel.checkOTP(mobileNumber, otpCode).observe(viewLifecycleOwner, Observer { flag ->
            if (flag) {
                context?.toast("OTP কোড ভেরিফাইড")
                requestPayment()
            } else {
                context?.toast("OTP কোড সঠিক নয়")
            }
        })
    }

    private fun showOTPVerify() {

        val dialog = OTPBottomSheet.newInstance()
        val tag = OTPBottomSheet.tag
        dialog.show(childFragmentManager, tag)
        dialog.onItemClicked = { message ->
            dialog.dismiss()
            timber.log.Timber.d(message)
            hideKeyboard()
            verifyOTP(message)
        }
        dialog.onCancel = {
            hideKeyboard()
        }
    }

    private fun dateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTheme(R.style.CustomMaterialCalendarTheme)
        builder.setTitleText("ডেট রেঞ্জ সিলেক্ট করুন")
        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {

            fromDate = sdf.format(it.first)
            toDate = sdf.format(it.second)
            setDateRangePickerTitle()
            val requestBody = DeliveredReturnedCountRequest(fromDate, toDate, SessionManager.courierUserId)
            fetchDeliveredReturnCount(requestBody)
        }
    }

    private fun setDateRangePickerTitle(){
        val msg = "${DigitConverter.toBanglaDate(fromDate, "yyyy-MM-dd")} - ${DigitConverter.toBanglaDate(toDate, "yyyy-MM-dd")}"
        binding?.dateRangePicker?.text = msg
        //binding?.clearDateRangeImage?.visibility = View.VISIBLE
    }

    private fun fetchDeliveredReturnCount(requestBody: DeliveredReturnedCountRequest) {

        viewModel.fetchDeliveredCount(requestBody).observe(viewLifecycleOwner, Observer { list->
            if (list.isNotEmpty()) {
                dateRangeFilterList.clear()
                dateRangeFilterList.addAll(list)
                val model = dateRangeFilterList.first()

                binding?.filterCountDeliveryPercent?.text = DigitConverter.toBanglaDigit(model.deliveredPercentage)
                binding?.filterCountDelivery?.text = "${DigitConverter.toBanglaDigit(model.delivered)} টি"
                binding?.filterCountReturnPercent?.text = DigitConverter.toBanglaDigit(model.returnPercentagee)
                binding?.filterCountReturn?.text = "${DigitConverter.toBanglaDigit(model.returned)} টি"
            }
        })
    }

    /*private fun showDeliveryChargeCalculator() {
        val tag = DeliveryChargeCalculatorFragment.tag
        val fragment = DeliveryChargeCalculatorFragment.newInstance()
        binding?.container?.let { container ->
            childFragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit()
        }
    }*/

    private fun goToNearByHubMap() {
        val bundle = bundleOf(
            "isNearByHubView" to true
        )
        addFragment(MapFragment.newInstance(bundle), MapFragment.tag)
    }

    private fun goToHubMap(hubModel: HubInfo) {

        val bundle = bundleOf(
            "hubView" to true,
            "hubModel" to hubModel
        )

        val fragment = MapFragment.newInstance(bundle)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, MapFragment.tag)
        ft?.addToBackStack(MapFragment.tag)
        ft?.commit()
    }

    private fun goToWebView(url: String) {

        val fragment = WebViewFragment.newInstance(url, "ডেলিভারি টাইগার")
        val tag = WebViewFragment.tag
        addFragment(fragment, tag)
    }

    private fun showQuickOrderBottomSheet() {
        val tag: String = QuickBookingBottomSheet.tag
        val dialog: QuickBookingBottomSheet = QuickBookingBottomSheet.newInstance()
        dialog.show(childFragmentManager, tag)
    }

    private fun serviceChargeDialog() {
        alert("নির্দেশনা", "আপনার সার্ভিস চার্জ (প্রি-পেইড) ৳${DigitConverter.toBanglaDigit(netAmount)} বকেয়া রয়েছে। সার্ভিস চার্জ পে করুন।", false, "সার্ভিস চার্জ পে","") {
            if (it == AlertDialog.BUTTON_POSITIVE) {
                addFragment(ServiceBillPayFragment.newInstance(), ServiceBillPayFragment.tag)
            }
        }.show()
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}
