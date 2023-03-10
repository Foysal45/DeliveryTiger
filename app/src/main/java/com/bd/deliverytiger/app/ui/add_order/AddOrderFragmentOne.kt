package com.bd.deliverytiger.app.ui.add_order

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.SpecialServiceRequestBody
import com.bd.deliverytiger.app.api.model.charge.WeightRangeWiseData
import com.bd.deliverytiger.app.api.model.district.DistrictData
import com.bd.deliverytiger.app.api.model.lead_management.GetLocationInfoRequest
import com.bd.deliverytiger.app.api.model.location.LocationData
import com.bd.deliverytiger.app.api.model.order.OrderPreviewData
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoData
import com.bd.deliverytiger.app.databinding.FragmentAddOrderFragmentOneBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationSelectionBottomSheet
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationType
import com.bd.deliverytiger.app.ui.add_order.order_preview.OrderPreviewBottomSheet
import com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet.ServicesSelectionBottomSheet
import com.bd.deliverytiger.app.ui.add_order.voucher.VoucherBottomSheet
import com.bd.deliverytiger.app.ui.add_order.voucher.VoucherInformationBottomSheet
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.payment_request.InstantPaymentUpdateViewModel
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SetTextI18n")
class AddOrderFragmentOne : Fragment() {

    //Step 1
    private lateinit var etCustomerName: EditText
    private lateinit var etAddOrderMobileNo: EditText
    private lateinit var etAlternativeMobileNo: EditText
    private lateinit var etDistrict: EditText
    private lateinit var etThana: EditText
    private lateinit var etAriaPostOffice: EditText
    private lateinit var etAriaPostOfficeLayout: ConstraintLayout
    private lateinit var etCustomersAddress: EditText
    private lateinit var etAdditionalNote: EditText

    //Step 2
    private val logTag = "AddOrderFragmentLog"
    private lateinit var productNameET: EditText
    private lateinit var collectionAmountET: EditText
    private lateinit var spinnerWeight: AppCompatSpinner
    private lateinit var packagingLayout: ConstraintLayout
    private lateinit var spinnerPackaging: AppCompatSpinner
    private lateinit var checkBoxBreakable: AppCompatCheckBox
    private lateinit var collectionAddressET: EditText
    private lateinit var checkTerms: AppCompatCheckBox
    private lateinit var checkPoh: AppCompatCheckBox
    private lateinit var checkPohTV: TextView
    private lateinit var checkTermsTV: TextView
    private lateinit var deliveryTypeRV: RecyclerView
    private lateinit var voucherLayoutButton: LinearLayout
    private lateinit var voucherInfoButton: LinearLayout
    private lateinit var voucherClearButton: LinearLayout
    private lateinit var deliveryButton: LinearLayout
    private lateinit var deliveryTakaButton: LinearLayout
    private lateinit var togglePickupGroup: MaterialButtonToggleGroup
    private lateinit var toggleButtonPickup1: MaterialButton
    private lateinit var pickupAddressLayout: ConstraintLayout

    private lateinit var actualPackageAmountET: EditText
    private lateinit var collectionSlotDatePicker: TextView
    private lateinit var collectionTimeSlotSpinner: AppCompatSpinner


    private lateinit var totalTV: TextView
    private lateinit var totalLayout: LinearLayout
    private lateinit var deliveryDatePicker: TextView
    private lateinit var collectionDatePicker: TextView

    private lateinit var spinnerCollectionLocation: AppCompatSpinner
    private lateinit var orderPlaceBtn: TextView

    private lateinit var deliveryTypeAdapter: DeliveryTypeAdapter
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable = Runnable { }

    // Step 1
    private var customerName = ""
    private var mobileNo = ""
    private var alternativeMobileNo = ""
    private var districtId = 0
    private var thanaId = 0
    private var areaId = 0
    private var customersAddress = ""
    private var additionalNote = ""

    private var serviceId: Int = 0
    private val serviceWiseDeliveryRangeList: MutableList<Int> = mutableListOf()
    private val serviceTypeList: MutableList<ServiceInfoData> = mutableListOf()
    private var filteredDistrictLists: MutableList<DistrictData> = mutableListOf()
    private var filteredThanaLists: MutableList<DistrictData> = mutableListOf()
    private var filteredAreaLists: MutableList<DistrictData> = mutableListOf()

    //special service
    private var specialServiceList: MutableList<WeightRangeWiseData> = mutableListOf()
    private var isSpecialServiceAvailable = false
    private var isAriaAvailable = true
    private var isProfileComplete: Boolean = false
    private var serviceTypeSelected: Boolean = false

    // Step 2
    private val packagingDataList: MutableList<PackagingData> = mutableListOf()
    private val timeSlotList : MutableList<QuickOrderTimeSlotData> = mutableListOf()

    private var codChargePercentage: Double = 0.0
    private var codChargeMin: Int = 0
    private var breakableChargeApi: Double = 0.0
    private var bigProductCharge: Double = 0.0
    private var collectionChargeApi: Double = 0.0
    private var collectionChargeExtraWeightWiseApi: Double = 0.0
    private var isCheckBigProduct: Boolean = false
    private var codChargeInsideDhaka: Double = 0.0
    private var codChargeOutsideDhaka: Double = 0.0
    private var codChargePercentageInsideDhaka: Double = 0.0
    private var codChargePercentageOutsideDhaka: Double = 0.0

    private var isCollection: Boolean = false
    private var isBreakable: Boolean = false
    private var isHeavyWeight: Boolean = false
    private var isEligibleForSpecialService: Boolean = false
    private var isAgreeTerms: Boolean = false
    private var isAgreePOH: Boolean = false
    private var isWeightSelected: Boolean = false
    private var isPackagingSelected: Boolean = true
    private var payCollectionAmount: Double = 0.0
    private var payDeliveryCharge: Double = 0.0
    private var payShipmentCharge: Double = 0.0
    private var deliveryCharge: Double = 0.0
    private var extraDeliveryCharge: Double = 0.0
    private var cityDeliveryCharge: Double = 0.0
    private var payCODCharge: Double = 0.0
    private var payBreakableCharge: Double = 0.0
    private var payCollectionCharge: Double = 0.0
    private var payPackagingCharge: Double = 0.0
    private var payActualPackagePrice: Double = 0.0
    private var isOpenBoxCheck: Boolean = false
    private var isOfficeDrop: Boolean = true
    private var isNextDay: Boolean = false
    private var isNextDayActive: Boolean = false
    private var isMsgShown: Boolean = false
    private var isCollectionLocationSelected: Boolean = false
    private var removeCollectionTimeSlotId: Int = 0
    private var isCollectionTypeSelected: Boolean = false
    private var collectionAmountLimit: Double = 0.0
    private var actualPackagePriceLimit: Double = 0.0

    private var total: Double = 0.0

    private var isShipmentChargeFree: Boolean = false
    private var offerType: String = ""
    private var relationType: String = ""

    //Voucher
    private var voucherDiscount = 0.0
    private var isVoucherApplied: Boolean = false
    private var voucherCode: String = ""
    private var voucherDeliveryRangeId: Int = 0

    //POH
    private var pohCharge: Double = 0.0
    private var isPohApplicable: Int = -1
    private var isMerchantPoHEligibility: Int = 0

    private var applicablePOHCharge = 0.0
    private var applicablePOHType = 0

    private var deliveryType: String = ""
    private var orderType: String = "Only Delivery"
    private var productType: String = "small"
    private var weight: String = ""
    private var collectionName: String = ""
    private var packingName: String = "No Extra Packing"
    private var collectionAddress: String = ""
    private var isOrderTypeSelected: Boolean = false

    private var deliveryRangeId: Int = 0
    private var weightRangeId: Int = 0
    private var boroProductCheck: Boolean = false

    private var deliveryTypeFlag: Int = 0
    private var alertMsg: String = ""
    private var alertMsg2: String = ""
    private var logicExpression: String = ""
    private var dayAdvance: String = ""
    private var isSameDay: Boolean = false
    private var deliveryDate: String = ""
    private var collectionDate: String = ""
    private var collectionDistrictId: Int = 0
    private var collectionThanaId: Int = 0
    private var collectionSlotDate: String = ""
    private var selectedCollectionSlotDate: String = ""
    private var selectedDate: String = ""
    private var timeSlotId: Int = 0
    private var isTodaySelected: Boolean = false

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private var isPickupLocationListAvailable: Boolean = false
    private var isPickupLocationFirstLoad: Boolean = false

    private var merchantCredit: Int = 0
    private var merchantCalculatedCollectionAmount: Int = 0
    private var merchantServiceCharge: Int = 0
    private var adjustBalance: Int = 0

    private var merchantDistrict: Int = 0
    private var selectedDeliveryType = ""
    private var serviceType: String = "alltoall"
    private var selectedServiceType: Int = 0
    private var isCity: Boolean = false

    private var isLocationLoading: Boolean = false
    private var isShowServiceType: Boolean = false

    private var binding: FragmentAddOrderFragmentOneBinding? = null
    private var timeSlotDataAdapter: AddOrderTimeSlotAdapter = AddOrderTimeSlotAdapter()
    private val viewModel: AddOrderViewModel by inject()
    private val homeViewModel: HomeViewModel by inject()
    private val currentTimeViewModel: InstantPaymentUpdateViewModel by inject()

    private var progressDialog: ProgressDialog? = null

    private var currentTime = ""

    //#region Life cycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentAddOrderFragmentOneBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Step 1
        etCustomerName = view.findViewById(R.id.etCustomerName)
        etAddOrderMobileNo = view.findViewById(R.id.etAddOrderMobileNo)
        etAlternativeMobileNo = view.findViewById(R.id.etAlternativeMobileNo)
        etDistrict = view.findViewById(R.id.etDistrict)
        etAriaPostOfficeLayout = view.findViewById(R.id.etAriaPostOfficeLayout)
        etThana = view.findViewById(R.id.etThana)
        etAriaPostOffice = view.findViewById(R.id.etAriaPostOffice)
        etCustomersAddress = view.findViewById(R.id.etCustomersAddress)
        etAdditionalNote = view.findViewById(R.id.etAdditionalNote)
        etAddOrderMobileNo.addTextChangedListener(textWatcher)

        //Step 2
        productNameET = view.findViewById(R.id.productName)
        collectionAmountET = view.findViewById(R.id.collectionAmount)
        spinnerWeight = view.findViewById(R.id.spinner_weight_selection)
        packagingLayout = view.findViewById(R.id.packagingLayout)
        spinnerPackaging = view.findViewById(R.id.spinner_packaging_selection)
        checkBoxBreakable = view.findViewById(R.id.check_breakable)
        collectionAddressET = view.findViewById(R.id.collectionAddress)
        checkPoh = view.findViewById(R.id.check_poh)
        checkPohTV = view.findViewById(R.id.check_poh_text)
        checkTerms = view.findViewById(R.id.check_terms_condition)
        checkTermsTV = view.findViewById(R.id.check_terms_condition_text)
        deliveryTypeRV = view.findViewById(R.id.delivery_type_selection_rV)
        voucherLayoutButton = view.findViewById(R.id.voucherLayout)
        voucherInfoButton = view.findViewById(R.id.voucherInfo)
        voucherClearButton = view.findViewById(R.id.voucherClear)
        deliveryButton = view.findViewById(R.id.deliveryPrepaidTypeLayout)
        deliveryTakaButton = view.findViewById(R.id.deliveryTakaCollectionTypeLayout)
        togglePickupGroup = view.findViewById(R.id.toggleButtonPickupGroup)
        toggleButtonPickup1 = view.findViewById(R.id.toggleButtonPickup1)
        pickupAddressLayout = view.findViewById(R.id.pickupAddressLayout)
        actualPackageAmountET = view.findViewById(R.id.actualPackageAmount)

        collectionSlotDatePicker = view.findViewById(R.id.collectionSlotDatePicker)
        collectionTimeSlotSpinner = view.findViewById(R.id.collectionTimeSlotSpinner)

        totalTV = view.findViewById(R.id.tvAddOrderTotalOrder)
        totalLayout = view.findViewById(R.id.payment_details)
        deliveryDatePicker = view.findViewById(R.id.deliveryDatePicker)
        collectionDatePicker = view.findViewById(R.id.collectionDatePicker)
        spinnerCollectionLocation = view.findViewById(R.id.spinnerCollectionLocation)
        orderPlaceBtn = view.findViewById(R.id.orderPlaceBtn)

        initView()
        getCurrentTime()
        initClickLister()
        // Order is important
        getCourierUsersInformation()
        fetchMerchantBalanceInfo()
        merchantPoHEligibilityCheck()
        getBreakableCharge()
        if (isBreakable) {
            getPackagingCharge()
        }
        fetchOfferCharge()
        fetchDTOrderGenericLimit()
        fetchCollectionTimeSlot()
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.new_order))
        //isProfileComplete = checkProfileData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    //#endregion

    //#region init
    private fun initView(){

        deliveryTypeAdapter = DeliveryTypeAdapter()
        with(deliveryTypeRV) {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            layoutAnimation = null
            adapter = deliveryTypeAdapter
        }

        binding?.recyclerViewTime?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = timeSlotDataAdapter
            }
        }

        checkTermsTV.text = HtmlCompat.fromHtml("????????? <font color='#00844A'>????????????????????????</font> ???????????? ???????????????", HtmlCompat.FROM_HTML_MODE_LEGACY)
        if (!isVoucherApplied){
            binding?.voucherText?.text = "??????????????????????????? ?????????????????? ?????????????????? ????????????"
            binding?.voucherInfo?.visibility = View.VISIBLE
            binding?.voucherClear?.visibility = View.GONE
        }
        if (homeViewModel.paymentServiceType > 0){
            binding?.pohLayout?.visibility = View.VISIBLE
            pohCharge = homeViewModel.paymentServiceCharge
        }else{
            binding?.pohLayout?.visibility = View.GONE
        }

        val calender = Calendar.getInstance()
        val todayDate = calender.timeInMillis
        selectedDate = sdf.format(todayDate)
        isTodaySelected = true
        fetchCollectionTimeSlot()
        SessionManager.orderSource = "DetailOrder"
    }

    private fun initClickLister() {

        deliveryTypeAdapter.onItemClick = { position, model ->
            Timber.d("onItemClick $model")
            deliveryType = "${model.deliveryType} ${model.days}"
            deliveryRangeId = model.deliveryRangeId
            weightRangeId = model.weightRangeId
            //alertMsg = model.deliveryAlertMessage ?: ""
            alertMsg = alertMsg2 ?: ""
            logicExpression = model.loginHours?.trim() ?: ""
            dayAdvance = model.dateAdvance ?: ""
            val showHide = model.showHide
            val deliveryType = model.type ?: ""

            // if free delivery is enable && weight <= 1KG
            if (isShipmentChargeFree && weightRangeId <= 2) {
                payShipmentCharge = 0.0
                deliveryCharge = 0.0
                extraDeliveryCharge = 0.0
                offerType = "freedelivery"
            } else {
                payShipmentCharge = model.chargeAmount // TotalCharge = deliveryCharge + extraDeliveryCharge
                deliveryCharge = model.deliveryCharge
                extraDeliveryCharge = model.extraDeliveryCharge
                collectionChargeExtraWeightWiseApi = model.extraCollectionCharge
                offerType = ""
            }

            when (showHide) {
                // Hide All fields
                0 -> {
                    deliveryDatePicker.visibility = View.GONE
                    collectionDatePicker.visibility = View.GONE
                    //hubDropLayout.visibility = View.GONE
                    deliveryDate = ""
                    deliveryDatePicker.text = ""
                    collectionDate = ""
                    collectionDatePicker.text = ""

                    if (isNextDayActive){
                        isNextDay = false
                    }
                }
                // Show delivery date collection date
                1 -> {
                    deliveryDatePicker.visibility = View.VISIBLE
                    collectionDatePicker.visibility = View.VISIBLE
                    //hubDropLayout.visibility = View.GONE
                    deliveryDate = ""
                    deliveryDatePicker.text = ""
                    collectionDate = ""
                    collectionDatePicker.text = ""

                    if (isNextDayActive){
                        isNextDay = false
                    }
                }
                // Show office drop
                2 -> {
                    deliveryDatePicker.visibility = View.GONE
                    collectionDatePicker.visibility = View.GONE
                    //hubDropLayout.visibility = View.VISIBLE
                    deliveryDate = ""
                    deliveryDatePicker.text = ""
                    collectionDate = ""
                    collectionDatePicker.text = ""

                    if (isNextDayActive){
                        isNextDay = false
                    }

                }
                // Delivery alert msg show
                3 -> {

                    if (isNextDayActive){
                        isNextDay = true
                    }

                    if (selectedDeliveryType != deliveryType) {
                        selectedDeliveryType = deliveryType
                        if (logicExpression.isNotEmpty()) {
                            val calendar = Calendar.getInstance()
                            val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
                            val timeValidity = executeExpression("$hour24 $logicExpression")
                            if (timeValidity) {
                                if (!alertMsg.isNullOrEmpty()){
                                    isMsgShown = true
                                    /*alert("???????????????????????????", alertMsg) {
                                    }.show()*/
                                }
                            }
                        } else {
                            if (!alertMsg.isNullOrEmpty()){
                                    isMsgShown = true
                                    /*alert("???????????????????????????", alertMsg) {
                                    }.show()*/
                                }
                        }
                    } else {
                        if (!isMsgShown){
                            if(!alertMsg.isNullOrEmpty()){
                                isMsgShown = true
                                /*alert("???????????????????????????", alertMsg) {
                                }.show()*/
                            }
                        }
                    }
                }
                else -> {
                    if (isNextDayActive){
                        isNextDay = false
                    }
                }
            }

            /*if (weightRangeId > 6 && (deliveryRangeId == 14 || deliveryRangeId == 17)) {
                alert("???????????????????????????", "??????????????????????????? ????????? ??? ??????????????? ???????????? ????????? ?????????????????? ?????? ???????????????????????? ??????????????? ????????? ??????", false).show()
            }*/

            /*if (isBreakable && (deliveryRangeId == 14 || deliveryRangeId == 17)) {
                alert("???????????????????????????", "??????????????????/????????? ??????????????????????????? ???????????????????????? ?????????????????? ?????? ??????????????? ????????? ??????", false).show()
            }*/

            clearVoucher()
            calculateTotalPrice()
            fetchCollectionTimeSlot()

            /*if (model.deliveryType.contains("Postal", true)) {
                alert("???????????????????????????", alertMsg) {
                }.show()
            }*/

        }

        collectionAmountET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                handler.removeCallbacks(runnable)
                runnable = Runnable {
                    calculateTotalPrice()
                    //Timber.d(logTag, "$p0")
                }
                handler.postDelayed(runnable, 400L)
                clearVoucher()

                if (!p0.isNullOrEmpty()){
                    checkPohApplicableCODAmount()
                }else{
                    checkPoh.isChecked = false
                }

            }

        })

        deliveryButton.setOnClickListener {
            isOrderTypeSelected = true
            deliveryButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.dotted_selected)
            deliveryTakaButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.dotted)
            collectionAmountET.visibility = View.GONE
            actualPackageAmountET.visibility = View.VISIBLE
            isCollection = false
            orderType = "Only Delivery"
            clearVoucher()
            calculateTotalPrice()
            actualPackageAmountET.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        deliveryTakaButton.setOnClickListener {
            isOrderTypeSelected = true
            deliveryTakaButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.dotted_selected)
            deliveryButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.dotted)
            collectionAmountET.visibility = View.VISIBLE
            actualPackageAmountET.visibility = View.GONE
            collectionAmountET.requestFocus()
            isCollection = true
            orderType = "Delivery Taka Collection"
            clearVoucher()
            calculateTotalPrice()
            collectionAmountET.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        checkBoxBreakable.setOnCheckedChangeListener { compoundButton, isChecked ->
            isBreakable = isChecked
            calculateTotalPrice()
            if (isChecked) {
                packagingLayout.visibility = View.VISIBLE
            } else {
                packagingLayout.visibility = View.GONE
                if (isPackagingSelected) {
                    spinnerPackaging.setSelection(1, false)
                }
            }
        }
        checkTerms.setOnCheckedChangeListener { compoundButton, b ->
            isAgreeTerms = b
        }

        checkPoh.setOnCheckedChangeListener { compoundButton, b ->
            isAgreePOH = b
            checkISPohApplicable()
        }
        checkTermsTV.setOnClickListener {

            val termsSheet = TermsConditionBottomSheet.newInstance()
            termsSheet.show(childFragmentManager, TermsConditionBottomSheet.tag)
            termsSheet.onTermsAgreed = {
                checkTerms.isChecked = it
            }
        }

        totalLayout.setOnClickListener {

            val bundle = Bundle()
            with(bundle) {
                putDouble("payShipmentCharge", payDeliveryCharge)
                putDouble("deliveryCharge", deliveryCharge)
                putDouble("extraDeliveryCharge", extraDeliveryCharge)
                putDouble("payCODCharge", payCODCharge)
                putDouble("payBreakableCharge", payBreakableCharge)
                putDouble("payCollectionCharge", payCollectionCharge)
                putDouble("payPackagingCharge", payPackagingCharge)
                putDouble("codChargePercentage", codChargePercentage)
                putDouble("bigProductCharge", bigProductCharge)
                putBoolean("boroProductCheck", boroProductCheck)
                putString("productType", productType)
                putDouble("total", total)
                putBoolean("isBreakable", isBreakable)
                putDouble("voucherDiscount", voucherDiscount)
                putString("voucherCode", voucherCode)
            }

            val detailsSheet = DetailsBottomSheet.newInstance(bundle)
            detailsSheet.show(childFragmentManager, DetailsBottomSheet.tag)

        }

        deliveryDatePicker.setOnClickListener {
            datePicker(1)
        }
        collectionDatePicker.setOnClickListener {
            datePicker(2)
        }
        collectionSlotDatePicker.setOnClickListener {
            datePicker(3)
        }

        orderPlaceBtn.setOnClickListener {
            orderPlaceProcess()
        }

        binding?.orderRequestDatePicker?.setOnClickListener {
            timeSlotDataAdapter.setSelectedPositions(-1)
            timeSlotId = 0
            selectedDate = ""
            datePicker()
        }

        binding?.collectionToday?.setOnClickListener {
            timeSlotDataAdapter.setSelectedPositions(-1)
            timeSlotId = 0
            binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
            binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            val calender = Calendar.getInstance()
            val todayDate = calender.timeInMillis
            selectedDate = sdf.format(todayDate)
            Timber.d("selectedDate $selectedDate")
            isTodaySelected = true
            fetchCollectionTimeSlot()
            getDeliveryCharge(districtId, thanaId, areaId, serviceType)
        }

        voucherLayoutButton?.setOnClickListener {
            if (deliveryType.isEmpty()) {
                context?.showToast("???????????????????????? ???????????? ???????????????????????? ????????????")
            }else{
                if (isVoucherApplied){
                    context?.showToast("?????????????????? ?????????????????? ????????? ?????????")
                }else{
                    UserLogger.logGenie("Voucher_apply_open")
                    goToVoucherBottomSheet()
                }
            }
        }

        voucherInfoButton?.setOnClickListener {
            goToVoucherInformationBottomSheet()
        }
        voucherClearButton?.setOnClickListener {
            clearVoucher()
        }

        binding?.collectionTomorrow?.setOnClickListener {
            timeSlotDataAdapter.setSelectedPositions(-1)
            timeSlotId = 0
            binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
            binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            val calender = Calendar.getInstance()
            calender.add(Calendar.DAY_OF_MONTH, 1)
            val tomorrowDate = calender.timeInMillis
            selectedDate = sdf.format(tomorrowDate)
            Timber.d("selectedDate $selectedDate")
            isTodaySelected = false
            fetchCollectionTimeSlot()
            getDeliveryCharge(districtId,thanaId,areaId,serviceType)
        }

        timeSlotDataAdapter.onItemClick = { model, position  ->
            timeSlotId = model.collectionTimeSlotId
            selectedCollectionSlotDate = selectedDate
            if (isTodaySelected && !model.cutOffTime.isNullOrEmpty()) {

                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    val cutOffTimeStamp = sdf.parse("$selectedDate ${model.cutOffTime}")
                    //val cutOffTimeStamp = sdf.parse("$selectedDate 12:00:00")
                    val endTimeStamp = sdf.parse("$selectedDate ${model.endTime}")
                    val currentTimeStamp = Date()
                    if (currentTimeStamp.after(cutOffTimeStamp)) {

                        val timeDiff = endTimeStamp!!.time - currentTimeStamp.time
                        val minute = TimeUnit.MILLISECONDS.toMinutes(timeDiff)

                        val msg = "?????? ???????????? ??????????????? ?????? ?????? ?????????????????? ???????????? ?????????????????? ????????? ????????????????????? ????????? ????????????????????? ???????????? ???????????? ????????????????????? ????????? ?????????????????? ???????????????"
                        alert(getString(R.string.instruction), msg, false, getString(R.string.ok), getString(R.string.cancel)) {
                            if (it == AlertDialog.BUTTON_POSITIVE) {
                                timeSlotId = 0
                                timeSlotDataAdapter.setSelectedPositions(-1)
                            }
                        }.apply {
                            setCancelable(false)
                            show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        homeViewModel.refreshEvent.observe(viewLifecycleOwner, Observer { tag ->
            if (tag == "OrderPlace") {
                homeViewModel.refreshEvent.value = ""
                isProfileComplete = checkProfileData()
            }
        })

        homeViewModel.keyboardVisibility.observe(viewLifecycleOwner, Observer { isShown ->
            Timber.d("isShown $isShown")
            if (isShown) {
                binding?.paymentOverViewLayout?.isVisible = false
            } else {
                binding?.paymentOverViewLayout?.isVisible = true
            }
        })

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
                        //binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        //binding?.progressBar?.visibility = View.GONE
                        progressDialog?.hide()
                    }
                }
            }
        })

        if (BuildConfig.DEBUG) {
            //mockUserData()
        }
    }

    private fun checkISPohApplicable() {
        if (isAgreePOH){
            if (validationPoh(1)){
                checkPoh.isChecked = false
            } else{
                if (homeViewModel.paymentServiceType == 1 && isPohApplicable == 1 && payCollectionAmount > 0){
                    if (payCollectionAmount <= homeViewModel.collectionAmountLimt && isMerchantPoHEligibility >= payCollectionAmount) {
                        checkPoh.isChecked = true
                        applicablePOHCharge = homeViewModel.paymentServiceCharge
                        applicablePOHType =  homeViewModel.paymentServiceType
                    }else{
                        context?.toast("POH ??????????????? ????????????????????? ?????? ???????????????????????? POH ?????? ???????????? ???????????????????????? ????????????")
                        checkPoh.isChecked = false
                        applicablePOHCharge = 0.0
                        applicablePOHType =  0
                    }
                }else{
                    context?.toast("POH ?????? ???????????????????????? ???????????? ???????????????????????? ????????????")
                    checkPoh.isChecked = false
                    applicablePOHCharge = 0.0
                    applicablePOHType =  0
                }
            }
        }else{
            applicablePOHCharge = 0.0
            applicablePOHType =  0
        }
    }

    private fun checkPohApplicableCODAmount() {
        if (isAgreePOH){
            if (validationPoh(0)){
                checkPoh.isChecked = false
            } else{
                if (homeViewModel.paymentServiceType == 1 && isPohApplicable == 1 && payCollectionAmount > 0){
                    if (payCollectionAmount <= homeViewModel.collectionAmountLimt && isMerchantPoHEligibility >= payCollectionAmount) {
                        applicablePOHCharge = homeViewModel.paymentServiceCharge
                        applicablePOHType =  homeViewModel.paymentServiceType
                    }else{
                        checkPoh.isChecked = false
                    }
                }else{
                    checkPoh.isChecked = false
                }
            }
        }
    }

    private fun getCurrentTime() {
        currentTimeViewModel.getMessageAlertForIP().observe(viewLifecycleOwner, Observer { model ->
            if (model != null){
                currentTime = model.currentTime
            }
        })
    }
    //#endregion

    private fun isMerchantCreditAvailable(): Boolean {
        val totalAdjustBalance = merchantCredit + merchantCalculatedCollectionAmount + adjustBalance
        val shipmentCharge = payShipmentCharge.toInt()
        val isMerchantCreditAvailable = totalAdjustBalance > shipmentCharge
        Timber.tag("adjustBalance")
            .d("(credit:) $merchantCredit + (calculatedCollectionAmount:) $merchantCalculatedCollectionAmount + (adjustBalance:) $adjustBalance = (totalAdjustBalance:) $totalAdjustBalance")
        Timber.tag("adjustBalance").d("service charge: $merchantServiceCharge")
        Timber.tag("adjustBalance").d("shipment charge: $shipmentCharge")
        Timber.tag("adjustBalance").d("isMerchantCreditAvailable: $totalAdjustBalance > $shipmentCharge $isMerchantCreditAvailable")
        return isMerchantCreditAvailable
    }

    private fun checkProfileData(): Boolean {
        val model = SessionManager.getSessionData()
        var missingValues = "???????????? ?????????????????? ???????????? ????????? ????????????????????????-??? "
        var isMissing = false
        if (model.companyName.isNullOrEmpty()) {
            if (!isMissing) isMissing = true
            missingValues += "???????????????????????????/???????????????????????? ?????????, "
        }
        if (model.mobile.isNullOrEmpty()) {
            if (!isMissing) isMissing = true
            missingValues += "?????????????????? ?????????????????????, "
        }
        if (model.bkashNumber.isNullOrEmpty()) {
            if (!isMissing) isMissing = true
            missingValues += "??????????????? ??????????????? (????????????????????? ????????????????????? ????????????), "
        }
        /*if (model.address.isNullOrEmpty()) {
            if(!isMissing) isMissing = true
            missingValues += "??????????????????????????? ????????????????????? ?????????????????? (????????????/?????????/?????????????????????), "
        }*/
        if (model.emailAddress.isNullOrEmpty()) {
            if (!isMissing) isMissing = true
            missingValues += "??????????????? "
        }
        if (!SessionManager.isPickupLocationAdded) {
            if (!isMissing) isMissing = true
            missingValues += "??????????????? ?????????????????? "
        }

        missingValues += "????????? ????????????"
        if (isMissing) {
            alert("???????????????????????????", missingValues, false) {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    //addFragment(ProfileFragment.newInstance(false, true), ProfileFragment.tag)
                    val bundle = bundleOf(
                        "isPickupLocation" to false,
                        "isFromOrderPlace" to true
                    )
                    if (isAdded) {
                        findNavController().navigate(R.id.nav_profile, bundle)
                    }
                }
            }.show()
            timber.log.Timber.d("missingValues: $missingValues")
        }

        if (!isPickupLocationListAvailable) {
            homeViewModel.getPickupLocations(SessionManager.courierUserId)
        }

        return !isMissing
    }

    /*private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }*/

    private fun showIsBreakableAlert(){

        alert("???????????????????????????", "????????????????????? ???????????? ?????????????????????, ???????????? ?????????????????? ??????????????????????????? ???????????? ????????? ??????????????? ??????????????????????????? ??????????????? ???????????? ???????????????????????? ??????????????? ???????????????????????? ????????????" , false, "????????? ????????? ", ).show()

    }

    private fun fetchPickupLocation() {
        homeViewModel.pickupLocationList.observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                isPickupLocationListAvailable = true
                SessionManager.isPickupLocationAdded = true
                if (list.size == 1 && list.first().districtId == 14) {
                    merchantDistrict = 14
                    isShowServiceType = false
                    loadServiceType()
                } else {
                    isShowServiceType = true
                    showPickupBottomSheet()
                }
            } else {
                isPickupLocationListAvailable = false
                SessionManager.isPickupLocationAdded = false
            }
            if (!isPickupLocationFirstLoad) {
                isPickupLocationFirstLoad = true
                isProfileComplete = checkProfileData()
            }
        })
    }

    private fun showPickupBottomSheet() {
        val tag: String = CollectionInfoBottomSheet.tag
        val dialog: CollectionInfoBottomSheet = CollectionInfoBottomSheet.newInstance(weightRangeId)
        dialog.show(childFragmentManager, tag)
        dialog.onCollectionTypeSelected = { isPickup, pickupLocation ->
            dialog.dismiss()
            if (isPickup) {
                isOfficeDrop = false
                collectionDistrictId = pickupLocation.districtId
                collectionThanaId = pickupLocation.thanaId
                collectionAddress = pickupLocation.pickupAddress ?: ""
                collectionAddressET.setText(collectionAddress)
                isCollectionLocationSelected = true
                removeCollectionTimeSlotId = pickupLocation.collectionTimeSlotId
                merchantDistrict = collectionDistrictId
            } else {
                isOfficeDrop = true
                isCollectionLocationSelected = false
                merchantDistrict = pickupLocation.districtId
            }
            calculateTotalPrice()
            isCollectionTypeSelected = true
            //orderPlaceBtn.performClick()

            if (isShowServiceType) {
                loadServiceType()
            }
            /*if (merchantDistrict == 14) {
                loadServiceType()
            } else {
                fetchLocationById(0, LocationType.DISTRICT, false)
            }*/
        }
    }

    private fun orderPlaceProcess() {
        //timber.log.Timber.tag("orderDebug").d("ThanaId: $thanaId")
        when {
            merchantDistrict == 14 && !serviceTypeSelected -> goToServiceSelectionBottomSheet(serviceTypeList)
            !isCollection && !isMerchantCreditAvailable() -> showCreditLimitAlert()
            !isProfileComplete -> checkProfileData()
            //!isCollection && adjustBalance <= (merchantCredit/2) -> showCreditLimitReachAlert()
            else -> submitOrder()
        }
    }

    private fun getAllViewData() {
        customerName = etCustomerName.text.toString()
        mobileNo = etAddOrderMobileNo.text.toString()
        alternativeMobileNo = etAlternativeMobileNo.text.toString()
        customersAddress = etCustomersAddress.text.toString()
        additionalNote = etAdditionalNote.text.toString()
    }

    private fun datePicker() {

        var calender = Calendar.getInstance()
        val calendarConstraints = CalendarConstraints.Builder().apply {
            //calender.add(Calendar.DAY_OF_MONTH, -1)
            val startDate = calender.timeInMillis
            setStart(startDate)

            calender = Calendar.getInstance()
            calender.add(Calendar.DAY_OF_MONTH, 7)
            val endDate = calender.timeInMillis
            setEnd(endDate)
            setValidator(object: CalendarConstraints.DateValidator {
                override fun describeContents(): Int {
                    return 0
                }

                override fun writeToParcel(p0: Parcel?, p1: Int) {

                }

                override fun isValid(date: Long): Boolean {
                    return date in startDate..endDate
                }

            })
        }

        val builder = MaterialDatePicker.Builder.datePicker().apply {
            setTheme(R.style.CustomMaterialCalendarTheme)
            setTitleText("Select date")
            setCalendarConstraints(calendarConstraints.build())
        }
        binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
        binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)

        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {
            selectedDate = sdf.format(it)
            Timber.d("selectedDate $selectedDate")
            binding?.collectionTomorrow?.text = DigitConverter.toBanglaDate(selectedDate, "yyyy-MM-dd", true)
            binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
            binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            isTodaySelected = false
            fetchCollectionTimeSlot()
            getDeliveryCharge(districtId, thanaId, areaId, serviceType)
        }
    }

    private fun datePicker(dateTypeFlag: Int) {

        val calendar = Calendar.getInstance()
        var minDate = calendar.timeInMillis
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val hour24 = calendar.get(Calendar.HOUR_OF_DAY)

        if (dayAdvance.isNotEmpty()) {
            try {
                val dayArray = dayAdvance.split(",")
                isSameDay = dayArray[1].toInt() == 0

                var flag = false
                if (logicExpression.isNotEmpty()) {
                    flag = executeExpression("$hour24 $logicExpression")
                }
                if (flag) {
                    val advanceBy = dayArray[0].toInt()
                    if (advanceBy != 0) {
                        calendar.add(Calendar.DAY_OF_MONTH, advanceBy)
                        minDate = calendar.timeInMillis
                    }
                } else {
                    val advanceBy = dayArray[1].toInt()
                    if (advanceBy != 0) {
                        calendar.add(Calendar.DAY_OF_MONTH, advanceBy)
                        minDate = calendar.timeInMillis
                    }
                }
            } catch (e: Exception) {
                //Timber.d("error", "Msg: ${e.message}")
            }
        } else {
            isSameDay = false
        }

        /*if (deliveryTypeFlag == 1) {
            if (hour24 >= 11) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                minDate = calendar.timeInMillis
            }
        } else if (deliveryTypeFlag == 2) {
            if (hour24 >= 16) {
                calendar.add(Calendar.DAY_OF_MONTH, 2)
                minDate = calendar.timeInMillis
            } else {
                calendar.add(Calendar.DAY_OF_MONTH,  1)
                minDate = calendar.timeInMillis
            }
        }*/

        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->

            if (dateTypeFlag == 1) {
                deliveryDate = "${month + 1}/$dayOfMonth/$year"
                deliveryDatePicker.text = DigitConverter.toBanglaDigit(deliveryDate)
                collectionDatePicker.text = DigitConverter.toBanglaDigit(deliveryDate)
                collectionDate = deliveryDate
                val monthBangla = DigitConverter.banglaMonth[month]
                //${dayOfMonth-1}
                if (alertMsg.isNotEmpty()) {
                    val day = if (isSameDay) "${DigitConverter.toBanglaDigit(dayOfMonth)} $monthBangla" else "${DigitConverter.toBanglaDigit(dayOfMonth - 1)} $monthBangla"
                    alertMsg = alertMsg.replace("dt-deliverydate", day, true)
                    alert("???????????????????????????", alertMsg, false).show()
                }

            } else if (dateTypeFlag == 2) {
                collectionDate = "${month + 1}/$dayOfMonth/$year"
                collectionDatePicker.text = DigitConverter.toBanglaDigit(collectionDate)
            } else if (dateTypeFlag == 3) {
                collectionSlotDate = "${month + 1}/$dayOfMonth/$year"
                collectionSlotDatePicker.text = DigitConverter.toBanglaDigit(collectionSlotDate)
            }

        }, currentYear, currentMonth, currentDay)
        datePicker.datePicker.minDate = minDate
        //datePicker.datePicker.maxDate = calendar.timeInMillis
        datePicker.show()
    }

    private fun executeExpression(expression: String): Boolean {
        // e.g 10 >= 11
        when {
            expression.contains(">=") -> {
                val digits = expression.split(">=")
                if (digits.isNotEmpty()) {
                    return (digits[0].trim().toInt() >= digits[1].trim().toInt())
                }
            }
            expression.contains("<=") -> {
                val digits = expression.split("<=")
                if (digits.isNotEmpty()) {
                    return (digits[0].trim().toInt() <= digits[1].trim().toInt())
                }
            }
        }
        return false
    }

    private fun getBreakableCharge() {

        viewModel.getBreakableCharge().observe(viewLifecycleOwner, Observer { model ->
            breakableChargeApi = model.breakableCharge
            codChargeMin = model.codChargeMin
            bigProductCharge = model.bigProductCharge
            codChargeInsideDhaka = model.codChargeDhaka
            codChargeOutsideDhaka = model.codChargeOutsideDhaka
            codChargePercentageInsideDhaka = model.codChargeDhakaPercentage
            codChargePercentageOutsideDhaka = model.codChargePercentage
            SessionManager.codChargeInsideDhaka = codChargeInsideDhaka
            SessionManager.codChargeOutsideDhaka = codChargeOutsideDhaka
            SessionManager.codChargePercentageInsideDhaka = codChargePercentageInsideDhaka
            SessionManager.codChargePercentageOutsideDhaka = codChargePercentageOutsideDhaka
            SessionManager.codChargeMin = codChargeMin
        })

    }

    private fun getCourierUsersInformation() {

        collectionChargeApi = SessionManager.collectionCharge
        isBreakable = SessionManager.isBreakAble
        isHeavyWeight = SessionManager.isHeavyWeight
        isEligibleForSpecialService = SessionManager.isEligibleForSpecialService

        //merchantDistrict = SessionManager.merchantDistrict
        fetchPickupLocation()
        initLocationListener()
    }

    private fun fetchMerchantBalanceInfo() {
        viewModel.fetchMerchantCurrentAdvanceBalance(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { advBalance ->
            viewModel.fetchMerchantBalanceInfo(SessionManager.courierUserId, advBalance.balance).observe(viewLifecycleOwner, Observer { model ->
                merchantCredit = model.credit
                merchantCalculatedCollectionAmount = model.calculatedCollectionAmount
                merchantServiceCharge = model.serviceCharge
                adjustBalance = model.adjustBalance

                if (BuildConfig.DEBUG) {
                    isMerchantCreditAvailable()
                }
            })
        })
    }

    private fun getPackagingCharge() {

        viewModel.getPackagingCharge().observe(viewLifecycleOwner, Observer { list ->
            packagingDataList.clear()
            packagingDataList.addAll(list)

            if (list.isNotEmpty()) {
                // Bubble + Poly (Tk 10) id 4
                val first = list.find { it.packagingChargeId == 4 }
                payPackagingCharge = first?.packagingCharge ?: 0.0
                packingName = first?.packagingName ?: ""
                isPackagingSelected = true
            }

            // Temp disabled
            /*val packageNameList: MutableList<String> = mutableListOf()
            packageNameList.add("???????????????????????????")
            for (model1 in packagingDataList) {
                packageNameList.add(model1.packagingName)
            }
            val packagingAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, packageNameList)
            spinnerPackaging.adapter = packagingAdapter
            spinnerPackaging.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 != 0) {
                        val model2 = packagingDataList[p2 - 1]
                        packingName = model2.packagingName
                        payPackagingCharge = model2.packagingCharge
                        isPackagingSelected = true
                        calculateTotalPrice()
                    } else {
                        isPackagingSelected = false
                    }
                }
            }
            spinnerPackaging.setSelection(1, false)*/
        })

    }

    private fun getDeliveryCharge(districtId: Int, thanaId: Int, areaId: Int, serviceType: String) {

        viewModel.getDeliveryCharge(DeliveryChargeRequest(districtId, thanaId, areaId, serviceType)).observe(viewLifecycleOwner, Observer { list ->

            if (serviceType == "citytocity" && list.isEmpty()) {
                this.serviceType = "alltoall"
                getDeliveryCharge(districtId, thanaId, areaId, this.serviceType)
                getSpecialService(districtId, thanaId, areaId)
                return@Observer
            }

            val weightList: MutableList<String> = mutableListOf()
            weightList.add("????????? (????????????)")
            for (model1 in list) {
                weightList.add(model1.weight)
            }

            val weightAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, weightList)
            spinnerWeight.adapter = weightAdapter
            spinnerWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 != 0) {
                        val model2 = list[p2 - 1]
                        weight = model2.weight
                        isWeightSelected = true
                        deliveryType = ""

                        var filterDeliveryTypeList = model2.weightRangeWiseData
                        if (merchantDistrict != 14) {
                            filterDeliveryTypeList = model2.weightRangeWiseData.filterNot { it.type == "express" }
                        }

                        //new condition to remove nextDay Delivery type after 4:30 PM
                        if(isTodaySelected){
                            val time = currentTime.split(":")
                            if (Integer.parseInt(time[0]) == 16){
                                if (Integer.parseInt(time[1]) >= 30){
                                    val data = filterDeliveryTypeList.filter { it.deliveryRangeId != 14 }
                                    filterDeliveryTypeList = data.filter { it.deliveryRangeId != 17 }
                                }
                            } else if (Integer.parseInt(time[0]) > 16){
                                val data = filterDeliveryTypeList.filter { it.deliveryRangeId != 14 }
                                filterDeliveryTypeList = data.filter { it.deliveryRangeId != 17 }
                            }
                        }
                        //new condition ends

                        getSpecialService(districtId,thanaId,areaId)
                        deliveryTypeAdapter.initLoad(filterDeliveryTypeList)
                        deliveryTypeAdapter.selectPreSelection()
                        //deliveryTypeAdapter.selectPreSelectionV2()
                    } else {
                        isWeightSelected = false
                        deliveryType = ""
                        if (list.isNotEmpty()) {
                            val model2 = list.first()
                            var filterDeliveryTypeList = model2.weightRangeWiseData
                            if (merchantDistrict != 14) {
                                filterDeliveryTypeList = model2.weightRangeWiseData.filterNot { it.type == "express" }
                            }

                            //new condition to remove nextDay Delivery type after 4:30 PM
                            if(isTodaySelected){
                                val time = currentTime.split(":")
                                if (Integer.parseInt(time[0]) == 16){
                                    if (Integer.parseInt(time[1]) >= 30){
                                        val data = filterDeliveryTypeList.filter { it.deliveryRangeId != 14 }
                                        filterDeliveryTypeList = data.filter { it.deliveryRangeId != 17 }
                                    }
                                } else if (Integer.parseInt(time[0]) > 16){
                                    val data = filterDeliveryTypeList.filter { it.deliveryRangeId != 14 }
                                    filterDeliveryTypeList = data.filter { it.deliveryRangeId != 17 }
                                }
                            }
                            //new condition ends

                            deliveryTypeAdapter.initLoad(filterDeliveryTypeList)
                            //Reset change
                            payShipmentCharge = 0.0
                            deliveryCharge = 0.0
                            extraDeliveryCharge = 0.0
                            calculateTotalPrice()
                            // select pre selected
                            if (selectedServiceType != 0) {
                                deliveryTypeAdapter.selectByDeliveryRangeId(selectedServiceType)
                            } else {
                                deliveryTypeAdapter.selectPreSelection()
                                //deliveryTypeAdapter.selectPreSelectionV2()
                            }
                        } else {
                            deliveryTypeAdapter.clearList()
                        }
                    }
                }
            }
        })
    }

    //#region Auto Populate Customer Info
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null) {
                if (s.count() == 11) {
                    fetchCustomerInformation(s.toString())
                    fetchIfPohApplicable(s.toString(), SessionManager.courierUserId)
                }
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private fun fetchCustomerInformation(mobile: String){
        viewModel.getCustomerInfoByMobile(mobile).observe(viewLifecycleOwner, Observer { model->
            if (model != null){
                districtId = model.districtId
                thanaId = model.thanaId
                areaId = model.areaId
                etAriaPostOfficeLayout.isVisible = areaId > 0
                isAriaAvailable = areaId > 0
                etCustomerName.setText(model.customerName)
                etCustomersAddress.setText(model.address)
                etAlternativeMobileNo.setText(model.otherMobile)

                selectServiceType()

                clearPoh()
                getDeliveryCharge(districtId, thanaId, areaId, serviceType)
                getSpecialService(districtId, thanaId, areaId)
                fetchSelectedDistrictInfo(model.districtId, model.thanaId, model.areaId)
            }
            Timber.d("customerInfo $model")
        })
    }

    private fun fetchIfPohApplicable(mobile: String, courierUserId: Int){
        viewModel.getIfPohApplicable(mobile, courierUserId).observe(viewLifecycleOwner, Observer { model->
            if (model != null){
                isPohApplicable = model.pohApplicable
            }
        })
    }

    private fun merchantPoHEligibilityCheck(){
        viewModel.merchantPoHEligibilityCheck(SessionManager.courierUserId.toString() ?: "").observe(viewLifecycleOwner, Observer { model->
            if (model != null){
                isMerchantPoHEligibility = model.netPayableAmount
            }
        })
    }

    private fun fetchSelectedDistrictInfo(districtID: Int, thanaID: Int, areaID: Int){
        val requestBody : MutableList<GetLocationInfoRequest> = mutableListOf()
        requestBody.add(GetLocationInfoRequest(districtID))
        requestBody.add(GetLocationInfoRequest(thanaID))
        requestBody.add(GetLocationInfoRequest(areaID))
        viewModel.loadAllDistrictsByIdList(requestBody).observe(viewLifecycleOwner, Observer { list->
            Timber.d("districtDData $list")

            val district = list.find { it.districtId == districtID }
            etDistrict.setText(district?.districtBng)
            val thana = list.find { it.districtId == thanaID }
            etThana.setText(thana?.districtBng)
            if (areaID > 0) {
                val area = list.find { it.districtId == areaID }
                etAriaPostOffice.setText(area?.districtBng)
            }
            //new logic for timeSlot
            alertMsg2 = district?.nextDayAlertMessage ?:""
            if (district?.nextDayAlertMessage.isNullOrEmpty()){
                isNextDayActive = true
                isNextDay = true
            } else{
                isNextDayActive = false
                isNextDay = false
            }
            fetchCollectionTimeSlot()
            Timber.d("alertMsg ${district?.nextDayAlertMessage}")
            //new logic end
        })
    }

    //#endregion

    //#region Service Type Selection (for Dhaka only)
    private fun loadServiceType() {
        homeViewModel.serviceInfoList.observe(viewLifecycleOwner, Observer { list ->
            serviceTypeList.clear()
            serviceTypeList.addAll(list)
            goToServiceSelectionBottomSheet(list)
        })
        /*viewModel.fetchServiceInfo().observe(viewLifecycleOwner, Observer { list ->
            serviceTypeList.clear()
            serviceTypeList.addAll(list)
            goToServiceSelectionBottomSheet(list)
        })*/
    }

    private fun goToServiceSelectionBottomSheet(dataLists: List<ServiceInfoData>) {
        val tag = ServicesSelectionBottomSheet.tag
        val dialog = ServicesSelectionBottomSheet.newInstance(dataLists)
        dialog.show(childFragmentManager, tag)
        dialog.onServiceSelected = { service, district ->

            val districtList = service.districtList
            serviceTypeSelected = true
            filteredDistrictLists.clear()
            filteredDistrictLists.addAll(districtList)
            filteredThanaLists.clear()
            filteredAreaLists.clear()

            serviceId = service.serviceId
            serviceWiseDeliveryRangeList.clear()
            serviceWiseDeliveryRangeList.addAll(service.deliveryRangeId)
            if (serviceWiseDeliveryRangeList.isNotEmpty()) {
                if (district.id == 14) { // first dhaka
                    selectedServiceType = service.deliveryRangeId.first()
                } else { // last outside dhaka
                    selectedServiceType = service.deliveryRangeId.last()
                }
            } else {
                selectedServiceType = 0 // default
            }
            updateUIAfterDistrict(district)
            if (isBreakable){
                showIsBreakableAlert()
            }
        }
        dialog.onClose = { type ->
            Timber.d("dialog.onClose $type")
            activity?.onBackPressed()
        }
    }
    //#endregion

    //#region Location Selection
    private fun initLocationListener() {

        etDistrict.setOnClickListener {
            hideKeyboard()
            if (filteredDistrictLists.isEmpty()) {
                fetchLocationById(0, LocationType.DISTRICT)
            } else {
                goToLocationSelectionDialog(filteredDistrictLists, LocationType.DISTRICT)
            }
        }

        etThana.setOnClickListener {
            hideKeyboard()
            if (districtId != 0) {
                if (filteredThanaLists.isEmpty()) {
                    fetchLocationById(districtId, LocationType.THANA)
                } else {
                    goToLocationSelectionDialog(filteredThanaLists, LocationType.THANA)
                }
            } else {
                context?.toast(getString(R.string.select_dist))
            }
        }

        etAriaPostOffice.setOnClickListener {
            hideKeyboard()
            if (isAriaAvailable) {
                if (thanaId != 0) {
                    if (filteredAreaLists.isEmpty()) {
                        fetchLocationById(thanaId, LocationType.AREA)
                    } else {
                        goToLocationSelectionDialog(filteredAreaLists, LocationType.AREA)
                    }
                } else {
                    context?.toast(getString(R.string.select_thana))
                }
            } else {
                context?.toast(getString(R.string.no_aria))
            }
        }
    }

    private fun fetchLocationById(id: Int, locationType: LocationType, preSelect: Boolean = false) {

        if (isLocationLoading) {
            context?.toast("?????????????????? ????????? ???????????????, ????????????????????? ????????????")
            return
        } else {
            isLocationLoading = true
        }
        when (locationType) {
            LocationType.DISTRICT -> {
                binding?.progressBar1?.isVisible = true
            }
            LocationType.THANA -> {
                binding?.progressBar2?.isVisible = true
            }
            LocationType.AREA -> {
                binding?.progressBar3?.isVisible = true
            }
        }

        viewModel.loadAllDistrictsById(id).observe(viewLifecycleOwner, Observer { list ->
            isLocationLoading = false
            binding?.progressBar1?.isVisible = false
            binding?.progressBar2?.isVisible = false
            binding?.progressBar3?.isVisible = false

            when (locationType) {
                LocationType.DISTRICT -> {
                    filteredDistrictLists.clear()
                    filteredDistrictLists.addAll(list)
                    filteredThanaLists.clear()
                    filteredAreaLists.clear()
                    if (!preSelect) {
                        goToLocationSelectionDialog(filteredDistrictLists, locationType)
                    }
                }
                LocationType.THANA -> {
                    filteredThanaLists.clear()
                    filteredThanaLists.addAll(list)
                    if (!preSelect) {
                        goToLocationSelectionDialog(filteredThanaLists, locationType)
                    } else {
                        if (list.isNotEmpty()) {
                            val sadarThana = list.first()
                            thanaId = sadarThana.districtId
                            if (selectedServiceType == 14 || selectedServiceType == 17 || selectedServiceType == 18) {
                                etThana.setText(sadarThana.districtBng)
                                etThana.isEnabled = isCity
                            }
                            fetchLocationById(thanaId, LocationType.AREA, true)
                            getDeliveryCharge(districtId, thanaId, 0, serviceType)
                            getSpecialService(districtId, thanaId, 0)
                        }
                    }
                }
                LocationType.AREA -> {
                    filteredAreaLists.clear()
                    filteredAreaLists.addAll(list)
                    isAriaAvailable = filteredAreaLists.isNotEmpty()
                    if (isAriaAvailable) {
                        etAriaPostOfficeLayout.visibility = View.VISIBLE
                        if (!preSelect) {
                            goToLocationSelectionDialog(filteredAreaLists, locationType)
                        } else {
                            val sadarArea = list.first()
                            areaId = sadarArea.districtId
                            //etAriaPostOffice.setText(sadarArea.districtBng)
                            getDeliveryCharge(districtId, thanaId, areaId, serviceType)
                            getSpecialService(districtId, thanaId, areaId)
                        }
                    } else {
                        etAriaPostOfficeLayout.visibility = View.GONE
                        areaId = 0
                        etAriaPostOffice.setText("")
                    }
                }
            }
        })
    }

    private fun goToLocationSelectionDialog(list: MutableList<DistrictData>, locationType: LocationType) {

        val locationList: MutableList<LocationData> = mutableListOf()
        list.forEach { model ->
            locationList.add(LocationData.from(model))
        }

        val dialog = LocationSelectionBottomSheet.newInstance(locationList)
        dialog.show(childFragmentManager, LocationSelectionBottomSheet.tag)
        dialog.onLocationPicked = { model ->
            when (locationType) {
                LocationType.DISTRICT -> {
                    districtId = model.id
                    etDistrict.setText(model.displayNameBangla)
                    thanaId = 0
                    etThana.setText("")
                    areaId = 0
                    etAriaPostOffice.setText("")
                    etAriaPostOfficeLayout.visibility = View.GONE
                    filteredThanaLists.clear()
                    filteredAreaLists.clear()

                    //new logic for timeSlot
                    if (!model.alertMsg.isNullOrEmpty()){
                        alertMsg2 = model.alertMsg.toString()
                        isNextDayActive = false
                        isNextDay = false
                    } else{
                        isNextDayActive = true
                        isNextDay = true
                    }
                    //deliveryTypeAdapter.clearSelection()
                    fetchCollectionTimeSlot()
                    //context?.toast("isNextDayActive $isNextDayActive")
                    //new logic end

                    if (list.isNotEmpty()) {
                        val locationModel = list.find { it.districtId == model.id }
                        locationModel?.let {
                            showLocationAlert(it, LocationType.DISTRICT)
                        }
                    }
                    clearPoh()
                    updateUIAfterDistrict(model)
                }
                LocationType.THANA -> {
                    thanaId = model.id
                    etThana.setText(model.displayNameBangla)
                    areaId = 0
                    etAriaPostOffice.setText("")
                    filteredAreaLists.clear()
                    getDeliveryCharge(districtId, thanaId, 0, serviceType)
                    getSpecialService(districtId, thanaId, 0)
                    fetchLocationById(thanaId, LocationType.AREA, true)

                    if (list.isNotEmpty()) {
                        val locationModel = list.find { it.districtId == model.id }
                        locationModel?.let {
                            showLocationAlert(it, LocationType.THANA)
                        }
                    }
                }
                LocationType.AREA -> {
                    areaId = model.id
                    val areaName = if (!model.displayPostalCode.isNullOrEmpty()) {
                        "${model.displayNameBangla} ${"${model.displayPostalCode}"}"
                    } else {
                        model.displayNameBangla
                    }
                    etAriaPostOffice.setText(areaName)

                    getDeliveryCharge(districtId, thanaId, areaId, serviceType)
                    getSpecialService(districtId, thanaId, areaId)

                    if (list.isNotEmpty()) {
                        val locationModel = list.find { it.districtId == model.id }
                        locationModel?.let {
                            showLocationAlert(it, LocationType.AREA)
                        }
                    }
                }
            }
        }
    }

    private fun updateUIAfterDistrict(model: LocationData) {

        districtId = model.id
        etDistrict.setText(model.displayNameBangla)
        thanaId = 0
        etThana.setText("")
        areaId = 0
        etAriaPostOffice.setText("")
        etAriaPostOfficeLayout.visibility = View.GONE

        //new logic for timeSlot
        if (!model.alertMsg.isNullOrEmpty()){
            alertMsg2 = model.alertMsg ?: ""
            isNextDayActive = false
            isNextDay = false
        } else{
            isNextDayActive = true
            isNextDay = true
        }
        Timber.d("Bool $isNextDayActive")
        //new logic end

        val selectedDistrict = filteredDistrictLists.find { it.districtId == districtId }
        selectedDistrict?.let { district ->
            showLocationAlert(district, LocationType.DISTRICT)
            isCity = district.isCity
        }

        selectServiceType()
        /*codChargePercentage = if (districtId == 14) {
            codChargePercentageInsideDhaka
        } else {
            codChargePercentageOutsideDhaka
        }*/
        calculateTotalPrice()
        fetchLocationById(districtId, LocationType.THANA, districtId != 14)

        /*val filterList = allLocationList.filter { it.parentId == districtId }
        filteredThanaLists.clear()
        if (filterList.isNotEmpty()) {
            val sortedList = filterList.sortedBy { it.districtPriority } as MutableList<AllDistrictListsModel>
            filteredThanaLists.addAll(sortedList)
        }
        val sadarThana = filteredThanaLists.first()
        thanaId = sadarThana.districtId
        etThana.setText(sadarThana.districtBng)*/

        /*val filterArea = allLocationList.filter { it.parentId == thanaId } as MutableList<AllDistrictListsModel>
        Timber.d("filterArea $filterArea")
        isAriaAvailable = filterArea.isNotEmpty()
        if (isAriaAvailable) {
            filteredAreaLists.clear()
            filteredAreaLists.addAll(filterArea.sortedBy { it.districtPriority })
            etAriaPostOfficeLayout.visibility = View.VISIBLE
        } else {
            etAriaPostOfficeLayout.visibility = View.GONE
        }*/

        // Check same city logic
        /*serviceType = if (merchantDistrict == districtId) {
            "citytocity"
        } else "alltoall"
        getDeliveryCharge(districtId, sadarThana.districtId, 0, serviceType)
        if (districtId == 14) {
            codChargePercentage = codChargePercentageInsideDhaka
        } else {
            codChargePercentage = codChargePercentageOutsideDhaka
        }
        calculateTotalPrice()*/
    }

    private fun showLocationAlert(model: DistrictData, locationType: LocationType) {
        if (model.isActiveForCorona) {
            val msg = when (locationType) {
                LocationType.DISTRICT -> "${model.districtBng} ?????????????????? ???????????????????????? ????????????????????? ????????????????????????????????? ???????????? ?????????????????????"
                LocationType.THANA -> "${model.districtBng} ?????????????????? ???????????????????????? ????????????????????? ????????????????????????????????? ???????????? ?????????????????????"
                LocationType.AREA -> "${model.districtBng} ????????????????????? ???????????????????????? ????????????????????? ????????????????????????????????? ???????????? ?????????????????????"
            }
            alert(getString(R.string.instruction), msg) {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    when (locationType) {
                        LocationType.DISTRICT -> {
                            districtId = 0
                            etDistrict.setText("")
                            thanaId = 0
                            etThana.setText("")
                            areaId = 0
                            etAriaPostOffice.setText("")
                            etAriaPostOfficeLayout.visibility = View.GONE
                        }
                        LocationType.THANA -> {
                            thanaId = 0
                            etThana.setText("")
                            areaId = 0
                            etAriaPostOffice.setText("")
                            etAriaPostOfficeLayout.visibility = View.GONE
                        }
                        LocationType.AREA -> {
                            areaId = 0
                            etAriaPostOffice.setText("")
                            etAriaPostOfficeLayout.visibility = View.GONE
                        }
                    }
                }
            }.show()
        }
    }

    //#endregion

    //#region Time Slot
    private fun fetchCollectionTimeSlot() {
        if (isTodaySelected) {
            viewModel.currentTimeSlot.observe(viewLifecycleOwner, Observer { list ->
                Timber.d("timeSlotDebug current time slot $list")
                timeSlotList.clear()
                timeSlotList.addAll(list)
                if (timeSlotList.isNotEmpty()) {
                    if (deliveryRangeId == 14 || deliveryRangeId == 17) {
                        timeSlotList.removeLast()
                    }
                    if (isHeavyWeight) {
                        // keep only first one
                        timeSlotList.removeAll { it.collectionTimeSlotId != 1 }
                    }
                    if (isCollectionLocationSelected) {
                        if (removeCollectionTimeSlotId != 0){
                            timeSlotList.removeAll { it.collectionTimeSlotId == removeCollectionTimeSlotId }
                        }
                    }
                    if(isTodaySelected){
                        if (isNextDay){
                            timeSlotList.removeAll{it.collectionTimeSlotId != 1}
                        }
                    }
                }
                timeSlotDataAdapter.initLoad(timeSlotList)
                Timber.d("timeSlotDebug $timeSlotList")
                binding?.emptyView?.isVisible = timeSlotList.isEmpty()
            })
        } else {
            viewModel.upcomingTimeSlot.observe(viewLifecycleOwner, Observer { list ->
                Timber.d("timeSlotDebug upcoming time slot")
                timeSlotList.clear()
                timeSlotList.addAll(list)
                if (timeSlotList.isNotEmpty()) {
                    if (isHeavyWeight) {
                        // keep only first one
                        timeSlotList.removeAll { it.collectionTimeSlotId != 1 }
                    }
                    if (isCollectionLocationSelected) {
                        if (removeCollectionTimeSlotId != 0){
                            timeSlotList.removeAll { it.collectionTimeSlotId == removeCollectionTimeSlotId }
                        }
                    }
                    if (isTodaySelected){
                        if (isNextDay) {
                            timeSlotList.removeAll{it.collectionTimeSlotId != 1}
                        }
                    }

                }
                timeSlotDataAdapter.initLoad(timeSlotList)
                binding?.emptyView?.isVisible = timeSlotList.isEmpty()
            })
        }
    }
    //#endregion

    private fun fetchDTOrderGenericLimit() {
        viewModel.fetchDTOrderGenericLimit().observe(viewLifecycleOwner, Observer { model ->
            collectionAmountLimit = model.collectionAmount
            actualPackagePriceLimit = model.actualPackagePrice
        })
    }

    private fun fetchOfferCharge() {
        viewModel.fetchOfferCharge(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            isShipmentChargeFree = model.isDeliveryCharge
            relationType = model.relationType
        })
    }

    private fun getSpecialService(districtId: Int, thanaId: Int, areaId: Int) {
        if (isEligibleForSpecialService){
            val requestBody = SpecialServiceRequestBody(SessionManager.courierUserId,  districtId, thanaId, areaId)
            viewModel.fetchSpecialService(requestBody).observe(viewLifecycleOwner, Observer { list->
                specialServiceList.clear()
                specialServiceList.addAll(list)
                isSpecialServiceAvailable = list.isNotEmpty()
                deliveryTypeAdapter.addRemoveSpecialService(isSpecialServiceAvailable, specialServiceList)
            })
        }
    }

    private fun calculateTotalPrice() {

        payDeliveryCharge = payShipmentCharge

        // Total = Shipment + cod + breakable + collection + packaging
        if (isCollection) {
            val collectionAmount = collectionAmountET.text.toString()
            if (collectionAmount.isNotEmpty()) {
                try {
                    payCollectionAmount = collectionAmount.toDouble()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
            if (districtId == 14){
                when (homeViewModel.codChargeTypeFlag){
                    1->{
                        payCODCharge = if (homeViewModel.codChargeDhaka < 0.0) {
                            codChargeInsideDhaka
                        }else{
                            homeViewModel.codChargeDhaka
                        }
                        codChargePercentage = 0.0
                    }
                    2->{
                        if (homeViewModel.codChargePercentageDhaka < 0.0) {
                            setCodChargeWithPercentage(codChargePercentageInsideDhaka)
                        }else{
                            setCodChargeWithPercentage(homeViewModel.codChargePercentageDhaka)
                        }
                        if (payCODCharge < codChargeMin) {
                            payCODCharge = codChargeMin.toDouble()
                        }
                    }
                }
            }else{
                when (homeViewModel.codChargeTypeOutsideFlag){
                    1->{
                        payCODCharge = if (homeViewModel.codChargeOutsideDhaka < 0.0) {
                            codChargeOutsideDhaka
                        }else{
                            homeViewModel.codChargeOutsideDhaka
                        }
                        codChargePercentage = 0.0
                    }
                    2->{
                        if (homeViewModel.codChargePercentageOutsideDhaka < 0.0) {
                            setCodChargeWithPercentage(codChargePercentageOutsideDhaka)
                        }else{
                            setCodChargeWithPercentage(homeViewModel.codChargePercentageOutsideDhaka)
                        }
                        if (payCODCharge < codChargeMin) {
                            payCODCharge = codChargeMin.toDouble()
                        }
                    }
                }
            }

            if (SessionManager.maxCodCharge != 0.0) {
                if (payCODCharge > SessionManager.maxCodCharge) {
                    payCODCharge = SessionManager.maxCodCharge
                }
            }

        } else {
            payCollectionAmount = 0.0
            payCODCharge = 0.0
            codChargePercentage = 0.0
        }

        if (isOfficeDrop) {
            payCollectionCharge = 0.0
        } else {
            payCollectionCharge = collectionChargeApi + collectionChargeExtraWeightWiseApi
        }

        //val payReturnCharge = SessionManager.returnCharge
        if (isCheckBigProduct) {
            total = payDeliveryCharge + payCODCharge + payCollectionCharge + payPackagingCharge + bigProductCharge - voucherDiscount
        } else {
            total = payDeliveryCharge + payCODCharge + payCollectionCharge + payPackagingCharge - voucherDiscount
        }

        //   total = payDeliveryCharge + payCODCharge + payCollectionCharge + payPackagingCharge

        if (isBreakable) {
            payBreakableCharge = breakableChargeApi
            total += payBreakableCharge

            binding?.orderAmountMsg?.isVisible = true
            binding?.orderAmountMsg?.text = "* ${DigitConverter.toBanglaDigit(payBreakableCharge.toInt())}??? ?????? (?????????/??????????????????)"
        } else {
            payBreakableCharge = 0.0
            binding?.orderAmountMsg?.isVisible = false
        }

        totalTV.text = "${DigitConverter.toBanglaDigit(total.toInt(), true)} ???"

    }

    private fun setCodChargeWithPercentage( percentage: Double){
        codChargePercentage = if (percentage < 0){
            if (districtId == 14) {
                codChargePercentageInsideDhaka
            } else {
                codChargePercentageOutsideDhaka
            }
        }else{
            percentage
        }
        payCODCharge = (payCollectionAmount / 100.0) * codChargePercentage
    }

    private fun submitOrder() {

        if (!validate() || !validateFormData()) {
            return
        }
        calculateTotalPrice()
        val collectionAmount = collectionAmountET.text.toString()
        val districtName = etDistrict.text.toString()
        val thanaName = etThana.text.toString()
        val orderPreviewData = OrderPreviewData(customerName, mobileNo, districtName, thanaName, collectionAmount, isCollection)
        goToOrderPreviewBottomSheet(orderPreviewData)
    }

    private fun placeOrder(){
        progressDialog = ProgressDialog(context)
        progressDialog?.run {
            setMessage("????????????????????? ????????????")
            setCancelable(false)
            show()
        }

        if (collectionAddress.isEmpty()) {
            collectionAddress = SessionManager.address
        }

        if (productType != "small") {
            payDeliveryCharge += bigProductCharge
        }

        val requestBody = OrderRequest(
            customerName, mobileNo, alternativeMobileNo, customersAddress, districtId, thanaId, areaId,
            deliveryType, orderType, weight, collectionName,
            payCollectionAmount, payDeliveryCharge, SessionManager.courierUserId,
            payBreakableCharge, additionalNote, payCODCharge, payCollectionCharge, SessionManager.returnCharge, packingName,
            payPackagingCharge, collectionAddress, productType, deliveryRangeId, weightRangeId, isOpenBoxCheck,
            "android", SessionManager.versionName, true, collectionDistrictId, collectionThanaId,
            deliveryDate, collectionDate, isOfficeDrop, payActualPackagePrice, timeSlotId, selectedCollectionSlotDate,
            offerType, relationType, serviceType, isHeavyWeight, voucherDiscount.toInt() ?: 0, voucherCode, voucherDeliveryRangeId, applicablePOHType, applicablePOHCharge
        )


        viewModel.placeOrder(requestBody).observe(viewLifecycleOwner, Observer { model ->
            progressDialog?.hide()
            SessionManager.totalAmount = total.toInt()
            addOrderSuccessFragment(model)
        })
    }

    private fun validate(): Boolean {
        hideKeyboard()
        getAllViewData()
        if (mobileNo.isEmpty()) {
            context?.toast(getString(R.string.write_phone_number))
            etAddOrderMobileNo.requestFocus()
            return false
        }
        if (!Validator.isValidMobileNumber(mobileNo) || mobileNo.length < 11) {
            context?.toast(getString(R.string.write_proper_phone_number_recharge))
            etAddOrderMobileNo.requestFocus()
            return false
        }
        if (customerName.isEmpty()) {
            context?.toast(getString(R.string.write_yr_name))
            etCustomerName.requestFocus()
            return false
        }
        if (districtId == 0) {
            context?.toast(getString(R.string.select_dist))
            return false
        }
        if (thanaId == 0 || etThana.text.toString().isEmpty()) {
            context?.toast(getString(R.string.select_thana))
            return false
        }
        if (isAriaAvailable && (areaId == 0 || etAriaPostOffice.text.toString().isEmpty())) {
            context?.toast(getString(R.string.select_aria))
            return false
        }
        if (customersAddress.isEmpty() || customersAddress.trim().length < 15) {
            context?.toast(getString(R.string.write_yr_address))
            etCustomersAddress.requestFocus()
            return false
        }
        /*if(alternativeMobileNo.isEmpty()){
            context?.toast(getString(R.string.write_alt_phone_number))
            etAlternativeMobileNo.requestFocus()
            return false
        }*/

        return true
    }

    private fun validateFormData(): Boolean {

        collectionName = productNameET.text.toString()
        collectionAddress = collectionAddressET.text.toString()

        if (!isOrderTypeSelected) {
            context?.showToast("?????????????????? ???????????? ????????????????????? ????????????")
            return false
        }

        if (!isWeightSelected) {
            context?.showToast("????????????????????? ?????? ????????? ???????????????????????? ????????????")
            return false
        }

        if (isCollection) {
            val collectionAmount = collectionAmountET.text.toString()
            if (collectionAmount.isEmpty()) {
                context?.showToast("????????????????????? ?????????????????????????????? ???????????????")
                return false
            }
            try {
                payCollectionAmount = collectionAmount.toDouble()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                context?.showToast("????????????????????? ?????????????????????????????? ???????????????")
                return false
            }
            if (payCollectionAmount > collectionAmountLimit) {
                context?.showToast("????????????????????? ?????????????????????????????? ${DigitConverter.toBanglaDigit(collectionAmountLimit.toInt())} ??????????????? ???????????? ???????????? ????????? ??????????????? ??????")
                return false
            }
            actualPackageAmountET.setText(payCollectionAmount.toInt().toString())
        }


        val payActualPackagePriceText = actualPackageAmountET.text.toString().trim()
        if (payActualPackagePriceText.isEmpty()) {
            context?.showToast("??????????????????????????? ????????? ???????????????")
            return false
        } else {
            try {
                payActualPackagePrice = payActualPackagePriceText.toDouble()
                if (!isCollection) {
                    if (payActualPackagePrice > actualPackagePriceLimit) {
                        context?.showToast("?????????????????????????????? ????????????????????? ?????????????????? ${DigitConverter.toBanglaDigit(actualPackagePriceLimit.toInt())} ??????????????? ???????????? ???????????? ????????? ??????????????? ??????")
                        return false
                    }
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                context?.showToast("?????????????????????????????? ????????????????????? ?????????????????? ???????????????")
                return false
            }
        }

        if (collectionName.isEmpty()) {
            context?.showToast("?????????????????? ??????????????????????????? ??????????????? / ?????????????????? ???????????????")
            return false
        }
        if (!isPackagingSelected) {
            context?.showToast("??????????????????????????? ???????????????????????? ????????????")
            return false
        }
        if (deliveryType.isEmpty()) {
            context?.showToast("???????????????????????? ???????????? ???????????????????????? ????????????")
            return false
        }
        /*if (collectionSlotDate.isEmpty()) {
            context?.showToast("????????????????????? ??????????????? ???????????????????????? ????????????")
            return false
        }
        if (timeSlotId == 0) {
            context?.showToast("????????????????????? ???????????? ???????????? ???????????????????????? ????????????")
            return false
        }*/
        if (deliveryDatePicker.visibility == View.VISIBLE && deliveryDate.isEmpty()) {
            context?.showToast("???????????????????????? ??????????????? ???????????????????????? ????????????")
            return false
        }
        if (isCollection && payCollectionAmount <= total) {
            context?.showToast("????????????????????? ?????????????????????????????? ????????????????????? ??????????????? ???????????? ???????????? ????????? ?????????")
            return false
        }

        if (selectedDate == "") {
            context?.toast(getString(R.string.select_yr_delivery_date))
            return false
        }

        if (timeSlotId == 0) {
            context?.toast(getString(R.string.select_yr_time_slot))
            return false
        }

        if (!isAgreeTerms) {
            context?.showToast("???????????????????????? ???????????? ?????????????????? ?????????")
            return false
        }

        if (!isCollectionTypeSelected) {
            showPickupBottomSheet()
            return false
        }

        // Hub drop is must for weight > 5kg
        if (!isOfficeDrop && weightRangeId > 6) {
            context?.showToast("??????????????????????????? ????????? ??? ??????????????? ???????????? ????????? ????????????????????? ???????????? ???????????? ???????????? ?????????")
            isCollectionTypeSelected = false
            isShowServiceType = false
            return false
        } else if (!isOfficeDrop) {
            if (!isCollectionLocationSelected) {
                context?.showToast("????????????????????? ?????????????????? ???????????????????????? ????????????")
                return false
            }
            if (collectionAddress.trim().isEmpty()) {
                context?.showToast("????????????????????? ?????????????????? ???????????????")
                return false
            }
            if (collectionAddress.trim().length < 15) {
                context?.showToast("??????????????????????????? ????????????????????? ?????????????????? ???????????????, ????????????????????? ?????? ???????????????")
                return false
            }
        }

        // weightRangeId (6) 4 kg - 5 kg and deliveryRangeId (14/17) ?????????????????? ??????
        /*if (weightRangeId > 6 && (deliveryRangeId == 14 || deliveryRangeId == 17)) {
            context?.showToast("??????????????????????????? ????????? ??? ??????????????? ???????????? ????????? ?????????????????? ?????? ???????????????????????? ??????????????? ????????? ??????")
            return false
        }*/

        // isBreakable true for breakable or liquid parcel and deliveryRangeId (14/17) ?????????????????? ??????
        /*if (isBreakable && (deliveryRangeId == 14 || deliveryRangeId == 17)) {
            context?.showToast("??????????????????/????????? ??????????????????????????? ???????????????????????? ?????????????????? ?????? ??????????????? ????????? ??????")
            return false
        }*/

        return true
    }

    private fun validationPoh(isWatcher: Int): Boolean{

        if(isPohApplicable == -1 ) {
            val msg = "?????????????????? ????????????????????? ???????????????"
            context?.toast(msg)
            return true
        }

        if(isPohApplicable != 1 ) {
            val msg = "POH ?????? ???????????????????????? ???????????? ???????????????????????? ????????????"
            if (isWatcher == 1){
                context?.toast(msg)
            }
            //customAlert(getString(R.string.instruction), msg, true,false, getString(R.string.ok), getString(R.string.cancel)) {}.show()
            return true
        }

        if (merchantDistrict == districtId){
            val msg = "?????? ??????????????? POH ?????? ????????????????????? ???????????????????????? ?????????"
            if (isWatcher == 1){
                context?.toast(msg)
            }
            //customAlert(getString(R.string.instruction), msg, true,false, getString(R.string.ok), getString(R.string.cancel)) {}.show()
            return true
        }

        if (!isCollection){
            if (isWatcher == 1){
                context?.toast("POH ???????????? ?????????, COD ?????????????????? ????????? ?????????")
            }
            return true
        }

        if (isCollection) {
            val collectionAmount = collectionAmountET.text.toString()

            try {
                payCollectionAmount = collectionAmount.toDouble()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

            if (collectionAmountET.text.isNullOrEmpty()){
                context?.toast("????????????????????? ?????????????????????????????? ???????????????")
                return true
            }

            if (payCollectionAmount < 1){
                context?.toast("???????????? ????????????????????? ?????????????????????????????? ???????????????")
                return true
            }
        }
        return false
    }

    private fun goToVoucherBottomSheet() {
        val tag: String = VoucherBottomSheet.tag
        val dialog: VoucherBottomSheet = VoucherBottomSheet.newInstance(deliveryRangeId)
        dialog.show(childFragmentManager, tag)
        dialog.onVoucherApplied = { model, isVoucherApplied->
            this.isVoucherApplied = isVoucherApplied
            if (this.isVoucherApplied){
                voucherLayoutButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.dotted_selected)
                binding?.voucherText?.text = HtmlCompat.fromHtml("${model.voucherCode}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color='#e11f27'>??? -${DigitConverter.toBanglaDigit(model.voucherDiscount.toInt() ?: 0)}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding?.voucherClear?.visibility = View.VISIBLE
                binding?.voucherInfo?.visibility = View.GONE
            }
            voucherDiscount = model.voucherDiscount
            voucherCode = model.voucherCode
            voucherDeliveryRangeId = model.deliveryRangeId
            calculateTotalPrice()
        }
    }

    private fun clearVoucher(){
        this.isVoucherApplied = false
        voucherLayoutButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.dotted_voucher)
        voucherDiscount = 0.0
        voucherCode = ""
        voucherDeliveryRangeId = 0
        binding?.voucherText?.text = "??????????????????????????? ?????????????????? ?????????????????? ????????????"
        binding?.voucherInfo?.visibility = View.VISIBLE
        binding?.voucherClear?.visibility = View.GONE
        calculateTotalPrice()
    }

    private fun clearPoh(){
        checkPoh?.isChecked = false
        applicablePOHCharge = 0.0
        applicablePOHType = 0
    }

    private fun goToVoucherInformationBottomSheet() {
        val tag: String = VoucherInformationBottomSheet.tag
        val dialog: VoucherInformationBottomSheet = VoucherInformationBottomSheet.newInstance()
        dialog.show(childFragmentManager, tag)
    }

    private fun goToOrderPreviewBottomSheet(data: OrderPreviewData) {
        val tag = OrderPreviewBottomSheet.tag
        val dialog = OrderPreviewBottomSheet.newInstance(data)
        dialog.show(childFragmentManager, tag)
        dialog.onConfirmedClick = {
            if (it == 1){
                dialog.dismiss()
                placeOrder()
            }
        }
        dialog.onClose = {
            if (it == 1){
                isCollectionTypeSelected = false
                dialog.dismiss()
            }
        }
    }

    private fun addOrderSuccessFragment(orderResponse: OrderResponse?) {

        val bundle = bundleOf(
            "isCollection" to isCollection,
            "orderResponse" to orderResponse
        )
        activity?.onBackPressed()
        //activity?.onBackPressed()
        Timber.d("requestBody $bundle")
        findNavController().navigate(R.id.nav_orderSuccess, bundle)

        /*val fragment = OrderSuccessFragment.newInstance(bundle)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, OrderSuccessFragment.tag)
        ft?.addToBackStack(OrderSuccessFragment.tag)
        ft?.commit()*/
    }

    private fun showCreditLimitAlert() {
        val msg = "??????????????? ????????????-???????????? ?????????????????? ???????????? ???????????? ???????????????????????? ??????????????????????????? ???????????? ??????????????? ????????????????????? ??????????????????????????? <font color='#00844A'>${
            DigitConverter.toBanglaDigit(
                adjustBalance,
                true
            )
        }</font> ??????????????? ??????????????????????????????????????? ??????????????????????????? ????????? ???????????????"
        alert("???????????????????????????", HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY), false, "??????????????????????????? ?????????", "") {
            if (it == AlertDialog.BUTTON_POSITIVE) {
                if (activity != null) {
                    (activity as HomeActivity).goToBalanceLoad()
                }
            }
        }.show()
    }

    private fun showCreditLimitReachAlert() {
        alert(
            "???????????????????????????",
            "??????????????? ??????????????????????????? (???${DigitConverter.toBanglaDigit(adjustBalance)}) ??????????????? ???????????? ???????????? ?????? ???????????? ???????????? ?????????????????? ????????????-???????????? ?????????????????? ???????????? ????????????????????? ??????????????????????????????????????? ??????????????????????????? ????????????????????? ???????????????",
            false,
            "????????? ?????????",
            ""
        ) {
            if (it == AlertDialog.BUTTON_POSITIVE) {
                submitOrder()
            }
        }.show()
    }

    private fun selectServiceType() {
        serviceType = if (merchantDistrict == districtId) {
            "citytocity"
        } else "alltoall"
    }

    //#region Test
    private fun mockUserData() {
        etCustomerName.setText("Test Customer ABC")
        etAddOrderMobileNo.setText("01555555555")
        etAlternativeMobileNo.setText("01555555556")
        etCustomersAddress.setText("Test Customer Address from IT")
        productNameET.setText("testITAndroid")
        collectionAmountET.setText("500")
        actualPackageAmountET.setText("500")
        checkTerms.isChecked = true
        additionalNote = "Test order from IT"
        //districtId = 14
        //thanaId = 10026
        //areaId = 0
    }
    //#endregion

}
