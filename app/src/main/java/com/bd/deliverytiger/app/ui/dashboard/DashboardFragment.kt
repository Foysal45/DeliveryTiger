package com.bd.deliverytiger.app.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.accepted_orders.AcceptedOrder
import com.bd.deliverytiger.app.api.model.chat.ChatUserData
import com.bd.deliverytiger.app.api.model.chat.FirebaseCredential
import com.bd.deliverytiger.app.api.model.cod_collection.HubInfo
import com.bd.deliverytiger.app.api.model.collector_info.CollectorInfoRequest
import com.bd.deliverytiger.app.api.model.collector_info.CollectorInformation
import com.bd.deliverytiger.app.api.model.config.BannerModel
import com.bd.deliverytiger.app.api.model.courier_info.AdminUser
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnCountResponseItem
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnedCountRequest
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsRequest
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantInstantPaymentRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceiveableDetailRequest
import com.bd.deliverytiger.app.databinding.FragmentDashboardBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.banner.SliderAdapter
import com.bd.deliverytiger.app.ui.chat.ChatConfigure
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.live.live_schedule.LiveScheduleActivity
import com.bd.deliverytiger.app.ui.live.live_schedule.LiveScheduleBottomSheet
import com.bd.deliverytiger.app.ui.payment_request.InstantPaymentRequestBottomSheet
import com.bd.deliverytiger.app.ui.payment_request.InstantPaymentUpdateViewModel
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.datepicker.MaterialDatePicker
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SetTextI18n")
class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null

    private lateinit var dashboardAdapter: DashboardAdapter
    private val dataList: MutableList<DashboardData> = mutableListOf()
    private val returnDataList: MutableList<DashboardData> = mutableListOf()
    private val customerNotFoundDataList: MutableList<DashboardData> = mutableListOf()
    private val shipmentDataList: MutableList<DashboardData> = mutableListOf()
    private var dateRangeFilterList: MutableList<DeliveredReturnCountResponseItem> = mutableListOf()
    private val viewModel: DashboardViewModel by inject()
    private val homeViewModel: HomeViewModel by inject()
    private val instantPaymentViewModel: InstantPaymentUpdateViewModel by inject()
    //private lateinit var monthSpinnerAdapter: CustomSpinnerAdapter

    private val calenderNow = Calendar.getInstance()
    private val viewList: MutableList<String> = mutableListOf()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
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

    //POH
    private var isPhoneVisible = false
    private var pohAmount: Int = -1
    private var bkashStatus: Int = 0
    private var bkashNumber: String = ""

    private var isBannerEnable: Boolean = false
    private var worker: Runnable? = null
    private var handler = Handler(Looper.getMainLooper())
    private var paymentDashboardModel: DashboardData = DashboardData(dashboardSpanCount = 3, viewType = 1)
    private var collectorInformation: CollectorInformation = CollectorInformation()
    private var countDownTimer: CountDownTimer? = null
    private var adminUser: AdminUser? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding?.root ?: FragmentDashboardBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDashboard()
        initPoh()
        getCourierUsersInformation()
        fetchBannerData()
        fetchCODData()
        fetchCollection()
        fetchAcceptedOrder()
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
        (activity as HomeActivity).setToolbarTitle("??????????????????????????????")
    }

    override fun onPause() {
        super.onPause()
        worker?.let {
            handler.removeCallbacks(it)
        }
        countDownTimer?.cancel()
    }

    private fun manageDeliveryReturnDashboard() {

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
            binding?.filterCountDelivery?.text = "${DigitConverter.toBanglaDigit(0)} ??????"
            binding?.filterCountReturn?.text = "${DigitConverter.toBanglaDigit(0)} ??????"
            binding?.dateRangePicker?.text = ""
            binding?.clearDateRangeImage?.visibility = View.GONE
        }

        binding?.deliveryFilterLayout?.setOnClickListener {
            if (dateRangeFilterList.isEmpty()) {
                Toast.makeText(requireContext(), "???????????? ???????????? ??????????????? ???????????????", Toast.LENGTH_SHORT).show()
            } else {
                if (dateRangeFilterList.first().delivered != 0) {
                    val reqBody = DeliveryDetailsRequest(fromDate, toDate, SessionManager.courierUserId, "delivery")
                    val bundle = bundleOf(
                        "dataRequestBody" to reqBody,
                        "totalCount" to dateRangeFilterList.first().delivered
                    )
                    findNavController().navigate(R.id.nav_delivery_details, bundle)
                } else {
                    Toast.makeText(requireContext(), "???????????? ???????????? ??????????????? ???????????????", Toast.LENGTH_SHORT).show()
                }
            }

        }
        binding?.deliveredReturnLayout?.setOnClickListener {
            if (dateRangeFilterList.isEmpty()) {
                Toast.makeText(requireContext(), "???????????? ???????????? ??????????????? ???????????????", Toast.LENGTH_SHORT).show()
            } else {
                if (dateRangeFilterList.first().returned != 0) {
                    val reqBody = DeliveryDetailsRequest(fromDate, toDate, SessionManager.courierUserId, "return")
                    val bundle = bundleOf(
                        "dataRequestBody" to reqBody,
                        "totalCount" to dateRangeFilterList.first().returned
                    )
                    findNavController().navigate(R.id.nav_delivery_details, bundle)
                } else {
                    Toast.makeText(requireContext(), "???????????? ???????????? ??????????????? ???????????????", Toast.LENGTH_SHORT).show()
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
            if (!model.loanSurveyBanner.isNullOrEmpty()) {
                binding?.loanSurveyBtn?.let { view ->
                    Glide.with(requireContext())
                        .load(model.loanSurveyBanner)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(view)
                }
                binding?.loanSurveyBtn?.visibility = View.VISIBLE
            }
        })
    }

    private fun initClickLister() {
        binding?.swipeRefresh?.setOnRefreshListener {
            getDashBoardData(selectedMonth, selectedYear)
            fetchCODData()
            initPoh()
            fetchCollection()
            fetchAcceptedOrder()
            //fetchCurrentBalance()
        }
        binding?.retryBtn?.setOnClickListener {
            getDashBoardData(selectedMonth, selectedYear)
        }
        binding?.collectorTrackBtn?.setOnClickListener {
            findNavController().navigate(R.id.nav_dashboard_map)
            UserLogger.logGenie("Dashboard_Collector_Track")
        }
        binding?.callCollectorBtn?.setOnClickListener {
            getRidersOfficeInfo()
            if (collectorInformation.mobile.isNullOrEmpty()) {
                context?.toast("???????????????????????? ?????????????????? ????????????????????? ???????????? ?????????")
            } else {
                callHelplineNumber(collectorInformation.mobile ?: "")
                UserLogger.logGenie("Dashboard_CollectorCall_${collectorInformation.mobile}")
            }
            UserLogger.logGenie("Dashboard_CollectorCall")
        }
        binding?.chatWithRiderBtn?.setOnClickListener {
            getRidersOfficeInfo()
            goToChatActivityRider(collectorInformation)
            UserLogger.logGenie("Dashboard_CollectorChat_${collectorInformation.name}")
        }
        binding?.orderTrackingBtn?.setOnClickListener {
            //addFragment(OrderTrackingFragment.newInstance(""), OrderTrackingFragment.tag)
            findNavController().navigate(R.id.nav_dashboard_orderTracking)
            UserLogger.logGenie("Dashboard_Order_Track")
        }
        binding?.complainBtn?.setOnClickListener {
            //addFragment(ComplainFragment.newInstance(), ComplainFragment.tag)
            findNavController().navigate(R.id.nav_dashboard_complain)
            UserLogger.logGenie("Dashboard_Complain")
        }
        /*binding?.balanceLoadLayout?.setOnClickListener {
            showQuickOrderBottomSheet()
            UserLogger.logGenie("Dashboard_Quick_Order")
            *//*showQuickOrderDialog()
            if (netAmount >= 0) {
                addFragment(BalanceLoadFragment.newInstance(), BalanceLoadFragment.tag)
            } else {
                serviceChargeDialog()
            }*//*
        }*/
        /*binding?.orderBtn?.setOnClickListener {
            binding?.progressBar?.visibility = View.VISIBLE
            if (showOrderPopup) {
                orderDialog()
            } else {
                if (netAmount >= 0) {
                    binding?.progressBar?.visibility = View.GONE
                    addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
                    UserLogger.logGenie("Dashboard_AddOrder")
                } else {
                    serviceChargeDialog()
                }
            }
        }*/
        dashboardAdapter.onItemClick = { _, model ->
            //dashBoardClickEvent(model?.dashboardRouteUrl!!)
            if (model.count != 0) {
                when (model.dashboardRouteUrl) {
                    "add-order" -> {
                        //addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
                        findNavController().navigate(R.id.nav_dashboard_newOrder)
                        UserLogger.logGenie("Dashboard_AllOrder_${model.statusGroupId}")
                    }
                    "billing-service" -> {
                        //addFragment(ServiceChargeFragment.newInstance(), ServiceChargeFragment.tag)
                        findNavController().navigate(R.id.nav_dashboard_Bill)
                        UserLogger.logGenie("Dashboard_AllOrder_${model.statusGroupId}")
                    }
                    "order-tracking" -> {
                        //addFragment(OrderTrackingFragment.newInstance(""), OrderTrackingFragment.tag)
                        findNavController().navigate(R.id.nav_dashboard_orderTracking)
                        UserLogger.logGenie("Dashboard_AllOrder_${model.statusGroupId}")
                    }
                    "shipment-charge" -> {
                        //addFragment(ShipmentChargeFragment.newInstance(), ShipmentChargeFragment.tag)
                        findNavController().navigate(R.id.nav_dashboard_shipmentCharge)
                        UserLogger.logGenie("Dashboard_AllOrder_${model.statusGroupId}")
                    }
                    "all-order" -> {
                        goToAllOrder(model.name ?: "", model.dashboardStatusFilter, selectedStartDate, selectedEndDate)
                        UserLogger.logGenie("Dashboard_AllOrder_${model.statusGroupId}")
                    }
                    "cod-collection" -> {
                        findNavController().navigate(R.id.nav_dashboard_CODCollection)
                        UserLogger.logGenie("Dashboard_AllOrder_${model.statusGroupId}")
                    }
                    "return" -> {
                        showStatusSubGroupBottomSheet(model.dashboardRouteUrl)
                        UserLogger.logGenie("Dashboard_ReturnDialog")
                    }
                    "shipment" -> {
                        showStatusSubGroupBottomSheet(model.dashboardRouteUrl)
                        UserLogger.logGenie("Dashboard_ShipmentDialog")
                    }
                    "customer_not_found" -> {
                        goToAllOrder(model.name ?: "", model.dashboardStatusFilter, selectedStartDate, selectedEndDate)
                        UserLogger.logGenie("Dashboard_CustomerNotFound_${model.statusGroupId}")
                    }
                    else -> {
                        //addFragment(AllOrdersFragment.newInstance(), AllOrdersFragment.tag)
                        findNavController().navigate(R.id.nav_dashboard_allOrder)
                        UserLogger.logGenie("Dashboard_AllOrder")
                    }
                }
            } else {
                VariousTask.showShortToast(context, "???????????????????????? ???????????? ?????????")
            }
        }
        dashboardAdapter.onPayDetailsClick = { position, model ->
            if (model.totalAmount.toInt() > 0) {
                goToPaymentDetails()
            } else {
                context?.toast("???????????????????????? ???????????? ?????????")
            }
            UserLogger.logGenie("Dashboard_PaymentDetails")
        }
        dashboardAdapter.onCODCollectionClick = { position, model ->
            if (netAmount == 0) {
                context?.toast("???????????????????????? ???????????? ?????????")
            } else {
                //addFragment(UnpaidCODFragment.newInstance(), UnpaidCODFragment.tag)
                findNavController().navigate(R.id.nav_dashboard_unpaidCod)
            }
            UserLogger.logGenie("Dashboard_UnpaidCOD")
        }
        dashboardAdapter.onPaymentRequestClick = { position, model ->

                if (availability && netAmount > 0) {
                    if (netAmount > instantPaymentOTPLimit) {
                        if (!isOTPRequested) {
                            sendOTP()
                        }
                    } else {
                        val tag = InstantPaymentRequestBottomSheet.tag
                        val dialog = InstantPaymentRequestBottomSheet.newInstance()
                        dialog.show(childFragmentManager, tag)

                        dialog.onCloseBottomSheet = {
                            fetchCODData()
                            fetchCollection()
                            fetchAcceptedOrder()
                        }
                    }
                } else {
                    if (availabilityMessage.isEmpty()) {
                        availabilityMessage = "???????????????????????? ???????????? ?????????"
                    }
                    alert("???????????????????????????", availabilityMessage, true, "????????? ?????????", "????????????????????????") {
                    }.show()
                    //binding?.swipeRefresh?.snackbar(availabilityMessage, Snackbar.LENGTH_INDEFINITE, "????????? ?????????"){}?.show()
                }
        }

        dashboardAdapter.onPreviousPaymentHistoryClick = {
            goToPreviousPaymentHistory()
        }

        binding?.callTextImageLayout?.setOnClickListener {
            if(isPhoneVisible){
                binding?.phoneNumberLayout?.visibility = View.GONE
                isPhoneVisible = false
            } else{
                binding?.phoneNumberLayout?.visibility = View.VISIBLE
                isPhoneVisible = true
            }
        }

        binding?.collectionLayout?.setOnClickListener {
            if (collectionToday > 0) {
                findNavController().navigate(R.id.nav_dashboard_collectionHistory)
                //addFragment(CollectionHistoryFragment.newInstance(), CollectionHistoryFragment.tag)
            } else {
                context?.toast("???????????????????????? ???????????? ?????????")
            }
            UserLogger.logGenie("Dashboard_Collection_History")
        }

        binding?.referBtn?.setOnClickListener {
            //addFragment(ReferralFragment.newInstance(), ReferralFragment.tag)
            findNavController().navigate(R.id.nav_dashboard_referral)
            UserLogger.logGenie("Dashboard_Referral")
        }

        binding?.loanSurveyBtn?.setOnClickListener {
            //addFragment(ReferralFragment.newInstance(), ReferralFragment.tag)
            findNavController().navigate(R.id.nav_loanSurvey)
            UserLogger.logGenie("Dashboard_Loan_Survey")
        }

        //poh transfer click event
        /*binding?.actionLayout?.setOnClickListener {
            if (pohAmount > 0){
                if (bkashStatus == 1){
                    alert("", HtmlCompat.fromHtml("<font><b>???????????? ?????? ???????????? ??????????????? ??????????????? ???????????????????????? ($bkashNumber) ????????????????????? ???????????? ??????????</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY),true, "???????????????", "??????") {
                        if (it == AlertDialog.BUTTON_POSITIVE) {
                            UserLogger.logGenie("Instant_poh_payment_transfer_clicked")
                            transferPoh() // for transfer poh balance 2
                        }
                    }.show()
                }else{
                    context?.toast("??????????????? ??????????????? ??????????????????????????? ???????????? ??????????????? ????????? ???????????? ???????????? ??????????????? ???????????? ???????????? ???????????????????????????????????????????????? ?????? ???????????? ????????????????????? ???????????????")
                }
            } else {
                context?.toast("??????????????? ???????????????????????? POH ??????????????????????????? ?????????")
            }

        }*/

        /* binding?.dateRangePicker?.setOnClickListener {
             val builder = MaterialDatePicker.Builder.dateRangePicker()
             builder.setTheme(R.style.CustomMaterialCalendarTheme)
             builder.setTitleText("????????? ??????????????? ????????????????????? ????????????")
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
            //goToAllOrder("???????????????????????? ???????????????", "???????????????????????? ???????????????", selectedStartDate, selectedEndDate)
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

    private fun transferPoh() {
        val progressDialog = progressDialog("??????????????? ??????????????????????????? ???????????????????????? ?????????????????? ????????????????????? ????????? ????????????????????? ????????????...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val requestBody = MerchantInstantPaymentRequest(0, SessionManager.courierUserId, pohAmount, 2, 1)
        Timber.d("transferDebug $requestBody")
        instantPaymentViewModel.instantOr24hourPayment(requestBody).observe(viewLifecycleOwner, Observer { response ->
            progressDialog.dismiss()
            if (response.message == 1 && !response.transactionId.isNullOrEmpty()){
                binding?.actionLayout?.visibility = View.GONE
                alert("", "??????????????? ???????????????????????? ????????????????????? ????????????????????? ??????????????????", false, "????????? ?????????") {}.show()
            }else if (response.message == 1 && response.transactionId.isNullOrEmpty()){
                alert("",  "????????????????????? ?????????????????? ???????????????????????? ?????????????????? ?????? ???????????????????????? ????????????????????????????????? ?????? ???????????? ????????????????????? ???????????????", false, "????????? ?????????") {}.show()
            }else{
                alert ( "", "??????????????? ???????????? ?????????????????? ???????????????, ???????????? ?????????????????? ???????????????", false, "????????? ?????????" ).show()
            }
        })
    }

    private fun goToPreviousPaymentHistory(){
        findNavController().navigate(R.id.nav_payment_history)
        UserLogger.logGenie("Dashboard_PaymentStatement")
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
                if (model.isWebLinkActive && !model.webUrl.isNullOrEmpty()) {
                    bannerNavigation(model.webUrl!!)
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
        val gridLayoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (dataList[position].dashboardSpanCount!! == 0 || dataList[position].dashboardSpanCount!! > 3) {
                    3
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

    private fun initPoh() {
        val requestBody = MerchantPayableReceiveableDetailRequest(SessionManager.courierUserId, 0)
        viewModel.getMerchantPayableDetailForInstantPayment(requestBody).observe(viewLifecycleOwner, Observer { data->
            if (data != null){
                binding?.countTV?.text = "??? ${DigitConverter.toBanglaDigit(data.pohPaybleAmount)}"
                pohAmount = data.pohPaybleAmount
                bkashStatus = data.bKashStatus
                bkashNumber = data.bKashNo
                /*if (data.pohPaybleAmount < 1){
                    binding?.actionLayout?.visibility = View.GONE
                } else {
                    binding?.actionLayout?.visibility = View.VISIBLE
                }*/
            }
        })
    }

    private fun initRetentionManagerData(userId: Int, retentionManagerName: String, retentionManagerNumber: String) {

        binding?.retentionManagerImage?.let { view ->
            Glide.with(view)
                .load("https://static.ajkerdeal.com/images/admin_users/dt/$userId.jpg")
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
                context?.toast("???????????? ?????????????????? ??????????????? ??????????????? ????????? ????????????")
            }
        }
        binding?.chatBtn?.setOnClickListener {
            goToChatActivity()
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

        val calendarStart = Calendar.getInstance()
        calendarStart.add(Calendar.MONTH, monthDuration * -1)
        val startYear = calendarStart.get(Calendar.YEAR)
        val startMonth = calendarStart.get(Calendar.MONTH) + 1
        selectedStartDate = "$startYear-$startMonth-01"

        getDashBoardData(selectedMonth, selectedYear)
        Timber.d("fetchDashBoard $selectedMonth $selectedYear")

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
            if (model.paymentServiceType > 0){
                binding?.pohMainLayout?.visibility = View.VISIBLE
            }else{
                binding?.pohMainLayout?.visibility = View.GONE
            }
            homeViewModel.paymentServiceType = model.paymentServiceType
            homeViewModel.paymentServiceCharge = model.paymentServiceCharge
            homeViewModel.collectionAmountLimt = model.collectionAmountLimt
            homeViewModel.pohBalance = model.pohBalance
            adminUser = model?.adminUsers
            initRetentionManagerData(
                model?.adminUsers?.userId ?: 0,
                model?.adminUsers?.fullName ?: "",
                model?.adminUsers?.mobile ?: ""
            )
        })
    }

    private fun getRidersOfficeInfo() {
        viewModel.getRidersOfficeInfo(CollectorInfoRequest(SessionManager.courierUserId)).observe(viewLifecycleOwner, Observer { model ->

            if (model == null) {
                context?.toast("???????????? ???????????? ?????????")
            } else {
                collectorInformation = model
            }

        })
    }

    private fun fetchCollection() {

        viewModel.fetchCollection(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->

            collectionToday = model.count
            val currentDate = "${DigitConverter.toBanglaDigit(today)} ${DigitConverter.banglaMonth[currentMonth]}"
            binding?.msg3?.text = "???????????? ($currentDate) ????????????????????? ??????????????????"
            binding?.amount3?.text = "${DigitConverter.toBanglaDigit(collectionToday.toString())}??????"

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
                                context?.toast("????????????????????? ??????????????? ???????????????")
                                SessionManager.isCollectorAttendance = true
                                SessionManager.collectorAttendanceDateOfYear = dayOfYear
                                binding?.switchCollector?.isEnabled = false
                            }
                        })
                        UserLogger.logGenie("Dashboard_CollectorAbsent")
                    }
                }
            } else {
                SessionManager.isCollectorAttendance = false
                SessionManager.collectorAttendanceDateOfYear = 0
            }
        })
    }

    private fun fetchAcceptedOrder() {
        viewModel.fetchAcceptedCourierOrders(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            showTimer(model)
        })
    }

    private fun showTimer(model: AcceptedOrder) {

        countDownTimer?.cancel()
        val endTime = model.collectionTimeSlot?.endTime ?: "00:00:00"
        if (endTime == "00:00:00") return
        try {
            val endDate = DigitConverter.formatDate(model.riderAcceptDate, "yyyy-MM-dd", "yyyy-MM-dd")
            val endTimeStamp = sdf1.parse("$endDate $endTime")
            Timber.d("timeDebug $endDate $endTime")
            if (endTimeStamp == null) return
            val timeDifference = endTimeStamp.time - Date().time
            Timber.d("timeDebug timeDifference $timeDifference ${endTimeStamp.time}")
            if (timeDifference > 0) {
                binding?.collectorTimerLayout?.isVisible = true
                binding?.clock?.let { image ->
                    Glide.with(image).load(R.raw.gif_watch).into(image)
                }
                countDownTimer = object : CountDownTimer(timeDifference, 1000L * 60 * 10) { // 10 min
                    override fun onTick(millisUntilFinished: Long) {
                        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished).toInt() % 24
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished).toInt() % 60
                        //val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toInt() % 60
                        //val message = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                        val fraction = minutes % 10
                        val roundMinute = if (fraction == 0) minutes else minutes + (10 - fraction) // 45 -> 50
                        Timber.d("timeDebug onTick hour $hours minute $minutes fraction $fraction roundMinute $roundMinute")
                        val message = if (hours == 0) {
                            String.format("%d ?????????????????????", roundMinute)
                        } else {
                            if (roundMinute == 0) {
                                String.format("%d ??????????????????", hours)
                            } else {
                                String.format("%d ??????????????? %d ?????????????????????", hours, roundMinute)
                            }
                        }
                        binding?.timeCounter?.text = DigitConverter.toBanglaDigit(message)
                        /*if (hours < 1) {
                            holder.binding.timeText.setTextColor(ContextCompat.getColor(holder.binding.timeText.context, R.color.crimson))
                        } else {
                            holder.binding.timeText.setTextColor(ContextCompat.getColor(holder.binding.timeText.context, R.color.colorPrimary))
                        }*/
                    }

                    override fun onFinish() {
                        binding?.timeCounter?.text = "??? ??????????????? ??? ?????????????????????"
                        binding?.collectorTimerLayout?.isVisible = false
                    }
                }.start()
            } else {
                binding?.collectorTimerLayout?.isVisible = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchCODData() {
        viewModel.fetchUnpaidCOD(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->

            netAmount = model.netAdjustedAmount
            SessionManager.netAmount = netAmount
            availability = model.availability
            availabilityMessage = model.availabilityMessage
            //netAmount = 6000
            //availability = true
            homeViewModel.netAmount.value = netAmount

            paymentDashboardModel.apply {
                this.name = "COD ?????????????????????"
                this.totalAmount = model.netAdjustedAmount.toDouble()
                this.availability = model.availability
                this.availabilityMessage = model.availabilityMessage
                this.paymentProcessingTime = instantPaymentHourLimit
            }

            viewModel.fetchDTMerchantInstantPaymentStatus(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model1 ->
                paymentDashboardModel.apply {
                    this.name = "COD ?????????????????????"
                    this.totalAmount = model.netAdjustedAmount.toDouble()
                    this.availability = model.availability
                    this.availabilityMessage = model.availabilityMessage

                    this.paymentProcessingTime = model1.bkashRime ?: ""
                    this.bankPaymentProcessingTime = model1.bankTime ?: ""
                    this.currentRequestDate = model1.currentRequestDate ?: ""
                    this.currentPaymentAmount = model1.currentPaymentAmount
                    this.currentPaymentStatus = model1.currentPaymentStatus
                    this.currentPaymentType = model1.paymentType
                    this.currentPaymentMethod = model1.paymentMethod
                    this.isPaymentProcessing = model1.processing
                    this.failedTransferMsg = model1.failedTransferMsg
                    this.successBkashTransferMsg = model1.successBkashTransferMsg
                    this.successExpressTransferMsg = model1.successExpressTransferMsg
                    this.successSuperExpressTransferMsg = model1.successSuperExpressTransferMsg
                }
                if (dataList.isNotEmpty()) {
                    dataList.last().apply {
                        this.name = "COD ?????????????????????"
                        this.totalAmount = model.netAdjustedAmount.toDouble()
                        this.availability = model.availability
                        this.availabilityMessage = model.availabilityMessage
                        this.paymentProcessingTime = instantPaymentHourLimit

                        this.currentRequestDate = model1.currentRequestDate ?: ""
                        this.currentPaymentAmount = model1.currentPaymentAmount
                        this.currentPaymentStatus = model1.currentPaymentStatus
                        this.currentPaymentType = model1.paymentType
                        this.currentPaymentMethod = model1.paymentMethod
                        this.isPaymentProcessing = model1.processing
                        this.failedTransferMsg = model1.failedTransferMsg
                        this.successBkashTransferMsg = model1.successBkashTransferMsg
                        this.successExpressTransferMsg = model1.successExpressTransferMsg
                        this.successSuperExpressTransferMsg = model1.successSuperExpressTransferMsg
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
                binding?.accountBalance?.text = "(??? ${DigitConverter.toBanglaDigit(adjustBalance, true)})"
            })
        })
    }*/

    private fun getDashBoardData(selectedMonth: Int, selectedYear: Int) {

        val dashBoardReqBody = DashBoardReqBody(0, 0, selectedStartDate, selectedEndDate, SessionManager.courierUserId)

        viewModel.getDashboardStatusGroup(dashBoardReqBody).observe(viewLifecycleOwner, Observer { list ->

            binding?.swipeRefresh?.isRefreshing = false
            returnDataList.clear()
            customerNotFoundDataList.clear()
            shipmentDataList.clear()
            val dashboardList: MutableList<DashboardData> = mutableListOf()
            var returnCount = 0
            var customerNotFoundCount = 0
            var shipmentCount = 0

            list.forEach { model ->
                when (model.statusGroupId) {
                    // "??????????????????????????? ?????????", "????????????????????? ??????????????? ?????????????????? ???????????????????????? ?????????", "????????????????????? ????????????"
                    4, 14, 16 -> {
                        shipmentCount += model.count ?: 0
                        shipmentDataList.add(model)
                    }
                    //  "?????????????????????????????? ????????????????????? ????????? ?????????????????? ??????",
                    15 -> {
                        Timber.d("requestBody DataFilter $model")
                        customerNotFoundCount += model.count ?: 0
                        customerNotFoundDataList.add(model)
                        // also add to shipment
                        shipmentCount += model.count ?: 0
                        shipmentDataList.add(model)
                    }
                    //"????????????????????? ????????????????????? ???????????? ??????????????? ?????????????????????", "????????????????????? ????????????????????? ??????????????????????????? ????????????????????? ??????????????? ?????????", "????????????????????? ????????????????????? ??????????????? ????????????????????? ???????????? ?????????"
                    9, 10, 11 -> {
                        returnCount += model.count ?: 0
                        returnDataList.add(model)
                    }
                }
            }
            val shipmentData = DashboardData(
                name = "??????????????????????????? ?????????",
                dashboardSpanCount = 1,
                statusGroupId = 4,
                count = shipmentCount,
                dashboardViewColorType = "waiting",
                dashboardRouteUrl = "shipment",
                dashboardCountSumView = "count"
            )
            dashboardList.add(shipmentData)
            val customerNotFoundData = DashboardData(
                name = "?????????????????????????????? ??????????????? ?????????????????? ??????",
                dashboardSpanCount = 1,
                statusGroupId = 15,
                count = customerNotFoundCount,
                dashboardViewColorType = "negative",
                dashboardRouteUrl = "customer_not_found",
                dashboardCountSumView = "count",
                dashboardStatusFilter = "?????????????????????????????? ????????????????????? ????????? ??????????????? ?????????????????? ??????"
            )
            dashboardList.add(customerNotFoundData)
            val returnData = DashboardData(
                name = "???????????????????????? ?????????",
                dashboardSpanCount = 1,
                statusGroupId = 9,
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

    /*private fun orderDialog() {

        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order_type, null)
        builder.setView(view)
        val button1: MaterialButton = view.findViewById(R.id.button1)
        val button2: MaterialButton = view.findViewById(R.id.button2)
        val dialog = builder.create()
        dialog.show()
        button1.setOnClickListener {
            dialog.dismiss()
            addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
            UserLogger.logGenie("Dashboard_AddOrder")
        }
        button2.setOnClickListener {
            dialog.dismiss()
            showQuickOrderBottomSheet()
            UserLogger.logGenie("Dashboard_Quick_Order")
        }
    }*/

    private fun showStatusSubGroupBottomSheet(dashboardRouteUrl: String?) {

        val dataList: MutableList<DashboardData>
        var title = ""
        var flag = 0
        when (dashboardRouteUrl) {
            "shipment" -> {
                dataList = shipmentDataList
                title = "??????????????????????????? ?????????"
                flag = 1
            }
            "customer_not_found" -> {
                dataList = customerNotFoundDataList
                title = "?????????????????????????????? ??????????????? ?????????????????? ??????"
                flag = 2
            }
            "return" -> {
                dataList = returnDataList
                title = "???????????????????????? ?????????"
                flag = 3
            }
            else -> return
        }

        val tag = StatusBreakDownBottomSheet.tag
        val dialog = StatusBreakDownBottomSheet.newInstance(title, dataList, flag)
        dialog.show(childFragmentManager, tag)
        dialog.onItemClicked = { model, position ->
            dialog.dismiss()
            if (model.count > 0) {
                goToAllOrder(model.name ?: "", model.dashboardStatusFilter, selectedStartDate, selectedEndDate)
                UserLogger.logGenie("Dashboard_AllOrder_${model.statusGroupId}")
            } else {
                context?.toast("???????????????????????? ???????????? ?????????")
            }
        }
        dialog.onMapClick = { model, position ->
            if (model.statusGroupId == 11) {
                dialog.dismiss()
                goToNearByHubMap()
            } else if (model.statusGroupId == 10) {
                dialog.dismiss()
                val hubModel = HubInfo(10, "??????????????????????????? ?????????", "lalmatia-hub", true, "7/7 Block C Lalmatia Mohammadpur", "90.3678406", "23.7544619", "01521427957")
                goToHubMap(hubModel)
            }
        }
    }

    /*private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commitAllowingStateLoss()
    }*/

    private fun goToAllOrder(statusGroupName: String, statusFilter: String, startDate: String, endDate: String) {

        val bundle = bundleOf(
            "statusGroup" to statusGroupName,
            "fromDate" to startDate,
            "toDate" to endDate,
            "dashboardStatusFilter" to statusFilter,
            "isFromDashBoard" to true
        )

        findNavController().navigate(R.id.nav_dashboard_allOrder, bundle)

        /*val fragment = AllOrdersFragment.newInstance(bundle, true)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
        ft?.addToBackStack(AllOrdersFragment.tag)
        ft?.commit()*/
    }

    private fun goToPaymentDetails() {

        findNavController().navigate(R.id.nav_dashboard_paymentDetails)

        /*val tag = PaymentDetailsFragment.tag
        val fragment = PaymentDetailsFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()*/
    }

    private fun fetchAccountsData() {

        viewModel.fetchAccountsData(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->

            /*if (!model.paymentDate.isNullOrEmpty()) {
                val banglaDate = DigitConverter.toBanglaDate(model.paymentDate!!,"MM/dd/yyyy")
                binding?.msg2?.text = "($banglaDate)"
                freezeDate = DigitConverter.formatDate(model.freezeDate!!, "MM/dd/yyyy", "yyyy-MM-dd")
            }
            binding?.amount1?.text = "??? ${DigitConverter.toBanglaDigit(model.totalAmount.toInt(), true)}"
            binding?.msg1?.text = "${model.name}"*/

            /*binding?.paymentInfoLayout?.setOnClickListener {
                if (model.totalAmount.toInt() > 0) {
                    goToPaymentDetails()
                } else {
                    context?.toast("???????????????????????? ???????????? ?????????")
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
                context?.toast("??????????????? ????????????????????????????????? ????????????????????? ????????????????????????????????? ??????????????? ????????? ???????????????")
                //val msg = "??????????????? ????????????????????????????????? ????????????????????? ????????????????????????????????? ??????????????? ????????? ?????????????????? ??????????????? ${DigitConverter.toBanglaDigit(instantPaymentHourLimit)} ?????????????????? ??????????????? ??????????????? bKash ???????????????????????? (${DigitConverter.toBanglaDigit(SessionManager.bkashNumber)}) ${DigitConverter.toBanglaDigit(netAmount, true)} ???????????? ????????????????????? ????????? ????????????"
                //alert("???????????????????????????", msg).show()
            }
        })
    }

    private fun sendOTP() {
        isOTPRequested = true
        val mobileNumber = SessionManager.mobile
        //val mobileNumber = "01728959986"
        viewModel.sendOTP(OTPRequestModel(mobileNumber, mobileNumber)).observe(viewLifecycleOwner, Observer { msg ->
            isOTPRequested = false
            val message = "??????????????? ?????????????????????????????? ?????????????????????????????? ????????? ${SessionManager.mobile} ?????? ?????????????????? ?????????????????? ?????????????????? ???????????????"
            alert("???????????????????????????", message, true, "?????????????????????", "????????????????????????") {
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
                context?.toast("OTP ????????? ????????????????????????")
                val tag = InstantPaymentRequestBottomSheet.tag
                val dialog = InstantPaymentRequestBottomSheet.newInstance()
                dialog.show(childFragmentManager, tag)
            } else {
                context?.toast("OTP ????????? ???????????? ??????")
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
        builder.setTitleText("????????? ??????????????? ????????????????????? ????????????")
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

    private fun setDateRangePickerTitle() {
        val msg = "${DigitConverter.toBanglaDate(fromDate, "yyyy-MM-dd")} - ${DigitConverter.toBanglaDate(toDate, "yyyy-MM-dd")}"
        binding?.dateRangePicker?.text = msg
        //binding?.clearDateRangeImage?.visibility = View.VISIBLE
    }

    private fun fetchDeliveredReturnCount(requestBody: DeliveredReturnedCountRequest) {

        viewModel.fetchDeliveredCount(requestBody).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                dateRangeFilterList.clear()
                dateRangeFilterList.addAll(list)
                val model = dateRangeFilterList.first()

                binding?.filterCountDeliveryPercent?.text = DigitConverter.toBanglaDigit(model.deliveredPercentage)
                binding?.filterCountDelivery?.text = "${DigitConverter.toBanglaDigit(model.delivered)} ??????"
                binding?.filterCountReturnPercent?.text = DigitConverter.toBanglaDigit(model.returnPercentagee)
                binding?.filterCountReturn?.text = "${DigitConverter.toBanglaDigit(model.returned)} ??????"
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
        findNavController().navigate(R.id.nav_dashboard_map, bundle)
        //addFragment(MapFragment.newInstance(bundle), MapFragment.tag)
    }

    private fun goToHubMap(hubModel: HubInfo) {

        val bundle = bundleOf(
            "hubView" to true,
            "hubModel" to hubModel
        )

        findNavController().navigate(R.id.nav_dashboard_map, bundle)

        /*val fragment = MapFragment.newInstance(bundle)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, MapFragment.tag)
        ft?.addToBackStack(MapFragment.tag)
        ft?.commit()*/
    }

    private fun goToWebView(url: String) {

        val bundle = bundleOf(
            "url" to url,
            "title" to getString(R.string.tiger)
        )
        findNavController().navigate(R.id.nav_web_view, bundle)

        /*val fragment = WebViewFragment.newInstance(url, "???????????????????????? ??????????????????")
        val tag = WebViewFragment.tag
        addFragment(fragment, tag)*/
    }

    /*private fun showQuickOrderBottomSheet() {
        val tag: String = QuickBookingBottomSheet.tag
        val dialog: QuickBookingBottomSheet = QuickBookingBottomSheet.newInstance()
        dialog.show(childFragmentManager, tag)
        dialog.onClose = {
            Handler(Looper.getMainLooper()).postDelayed({
                hideKeyboard()
            }, 200L)
        }
        dialog.onOrderPlace = { msg ->
            alert(getString(R.string.instruction), msg, true, getString(R.string.ok), "????????? ???????????? ???????????????") {
                if (it == AlertDialog.BUTTON_NEGATIVE) {
                    addFragment(QuickOrderListFragment.newInstance(), QuickOrderListFragment.tag)
                }
            }.show()
        }
    }*/

    private fun serviceChargeDialog() {
        alert("???????????????????????????", "??????????????? ????????????????????? ??????????????? (????????????-????????????) ???${DigitConverter.toBanglaDigit(netAmount)} ??????????????? ?????????????????? ????????????????????? ??????????????? ?????? ???????????????", false, "????????????????????? ??????????????? ??????", "") {
            if (it == AlertDialog.BUTTON_POSITIVE) {
                findNavController().navigate(R.id.nav_dashboard_billPay)
            }
        }.show()
    }

    private fun goToChatActivity() {
        val firebaseCredential = FirebaseCredential(
            firebaseWebApiKey = BuildConfig.FirebaseWebApiKey
        )
        val senderData = ChatUserData(SessionManager.courierUserId.toString(), SessionManager.companyName, SessionManager.mobile,
            imageUrl = "https://static.ajkerdeal.com/delivery_tiger/profile/${SessionManager.courierUserId}.jpg",
            role = "dt",
            fcmToken = SessionManager.firebaseToken
        )
        val receiverData = if (adminUser != null) {
            ChatUserData(adminUser!!.userId.toString(), adminUser!!.fullName.toString(), adminUser!!.mobile.toString(),
                imageUrl = "https://static.ajkerdeal.com/images/admin_users/dt/${adminUser!!.userId}.jpg",
                role = "retention"
            )
        } else {
            ChatUserData()
        }
        ChatConfigure(
            "dt-retention",
            senderData,
            firebaseCredential = firebaseCredential,
            receiver = receiverData
        ).config(requireContext())
    }

    private fun goToChatActivityRider(collectorInformation: CollectorInformation) {
        val firebaseCredential = FirebaseCredential(
            firebaseWebApiKey = BuildConfig.FirebaseWebApiKey
        )
        val senderData = ChatUserData(SessionManager.courierUserId.toString(), SessionManager.userName, SessionManager.mobile,
            imageUrl = "https://static.ajkerdeal.com/delivery_tiger/profile/${SessionManager.courierUserId}.jpg",
            role = "dt",
            fcmToken = SessionManager.firebaseToken
        )
        val receiverData = if (collectorInformation.id == 0 || collectorInformation.name == null) {
            ChatUserData("906","Post Shipment Admin", "" ,
                imageUrl = "https://static.ajkerdeal.com/images/bondhuprofileimage/906/profileimage.jpg",
                role = "bondhu"
            )
        } else {
            ChatUserData(collectorInformation.id.toString(),collectorInformation.name.toString(), collectorInformation.mobile.toString() ,
                imageUrl = "https://static.ajkerdeal.com/images/bondhuprofileimage/${collectorInformation.id}/profileimage.jpg",
                role = "bondhu"
            )
        }
        ChatConfigure(
            "dt-bondhu",
            senderData,
            firebaseCredential = firebaseCredential,
            receiver = receiverData
        ).config(requireContext())
    }

    private fun bannerNavigation(url: String) {
        when(url) {
            "survey" -> {
                findNavController().navigate(R.id.nav_dashboard_loanSurvey)
            }
            "chumbok" -> {
                findNavController().navigate(R.id.nav_dashboard_leadManagement)
            }
            "live" -> {
                goToLiveActivity()
            }
            else -> {
                goToWebView(url)
            }
        }
    }

    private fun goToLiveActivity() {
        startActivity(Intent(requireContext(), LiveScheduleActivity::class.java))
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}
