package com.bd.deliverytiger.app.ui.add_order

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.district.AllDistrictListsModel
import com.bd.deliverytiger.app.api.model.district.ThanaPayLoad
import com.bd.deliverytiger.app.api.model.location.LocationData
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoData
import com.bd.deliverytiger.app.api.model.time_slot.TimeSlotData
import com.bd.deliverytiger.app.databinding.FragmentAddOrderFragmentOneBinding
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationSelectionDialog
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationType
import com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet.ServicesSelectionBottomSheet
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.profile.ProfileFragment
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class AddOrderFragmentOne : Fragment(), View.OnClickListener {

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
    private lateinit var checkTermsTV: TextView
    private lateinit var deliveryTypeRV: RecyclerView
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

    private val serviceTypeList: MutableList<ServiceInfoData> = mutableListOf()
    private var filteredDistrictLists: MutableList<AllDistrictListsModel> = mutableListOf()
    private var filteredThanaLists: MutableList<AllDistrictListsModel> = mutableListOf()
    private var filteredAreaLists: MutableList<AllDistrictListsModel> = mutableListOf()

    private var isAriaAvailable = true
    private var isProfileComplete: Boolean = false
    private var serviceTypeSelected: Boolean = false

    // Step 2
    private val packagingDataList: MutableList<PackagingData> = mutableListOf()

    private var codChargePercentage: Double = 0.0
    private var codChargeMin: Int = 0
    private var breakableChargeApi: Double = 0.0
    private var bigProductCharge: Double = 0.0
    private var collectionChargeApi: Double = 0.0
    private var isCheckBigProduct: Boolean = false
    private var codChargePercentageInsideDhaka: Double = 0.0
    private var codChargePercentageOutsideDhaka: Double = 0.0

    private var isCollection: Boolean = false
    private var isBreakable: Boolean = false
    private var isAgreeTerms: Boolean = false
    private var isWeightSelected: Boolean = false
    private var isPackagingSelected: Boolean = false
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
    private var isCollectionLocationSelected: Boolean = false
    private var isCollectionTypeSelected: Boolean = false
    private var collectionAmountLimit: Double = 0.0
    private var actualPackagePriceLimit: Double = 0.0

    private var total: Double = 0.0

    private var isShipmentChargeFree: Boolean = false
    private var offerType: String = ""
    private var relationType: String = ""

    private var deliveryType: String = ""
    private var orderType: String = "Only Delivery"
    private var productType: String = "small"
    private var weight: String = ""
    private var collectionName: String = ""
    private var packingName: String = ""
    private var collectionAddress: String = ""
    private var isOrderTypeSelected: Boolean = false

    private var deliveryRangeId: Int = 0
    private var weightRangeId: Int = 0
    private var boroProductCheck: Boolean = false

    private var deliveryTypeFlag: Int = 0
    private var alertMsg: String = ""
    private var logicExpression: String = ""
    private var dayAdvance: String = ""
    private var isSameDay: Boolean = false
    private var deliveryDate: String = ""
    private var collectionDate: String = ""
    private var collectionDistrictId: Int = 0
    private var collectionThanaId: Int = 0
    private var collectionSlotDate: String = ""
    private var selectedCollectionSlotDate: String = ""
    private var timeSlotId: Int = 0

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

    private var isLocationLoading: Boolean = false

    private var binding: FragmentAddOrderFragmentOneBinding? = null
    private val viewModel: AddOrderViewModel by inject()
    private val homeViewModel: HomeViewModel by inject()

    companion object {
        fun newInstance(): AddOrderFragmentOne = AddOrderFragmentOne()
        val tag: String = AddOrderFragmentOne::class.java.name
    }

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

        //Step 2
        productNameET = view.findViewById(R.id.productName)
        collectionAmountET = view.findViewById(R.id.collectionAmount)
        spinnerWeight = view.findViewById(R.id.spinner_weight_selection)
        packagingLayout = view.findViewById(R.id.packagingLayout)
        spinnerPackaging = view.findViewById(R.id.spinner_packaging_selection)
        checkBoxBreakable = view.findViewById(R.id.check_breakable)
        collectionAddressET = view.findViewById(R.id.collectionAddress)
        checkTerms = view.findViewById(R.id.check_terms_condition)
        checkTermsTV = view.findViewById(R.id.check_terms_condition_text)
        deliveryTypeRV = view.findViewById(R.id.delivery_type_selection_rV)
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

        orderPlaceBtn.setOnClickListener(this)

        // Order is important
        getCourierUsersInformation()
        getBreakableCharge()
        fetchOfferCharge()
        getPackagingCharge()
        fetchDTOrderGenericLimit()
        getPickupLocation()
        //fetchCollectionTimeSlot()


        deliveryTypeAdapter = DeliveryTypeAdapter()
        with(deliveryTypeRV) {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            layoutAnimation = null
            adapter = deliveryTypeAdapter
        }
        deliveryTypeAdapter.onItemClick = { position, model ->

            deliveryType = "${model.deliveryType} ${model.days}"
            deliveryRangeId = model.deliveryRangeId
            weightRangeId = model.weightRangeId
            alertMsg = model.deliveryAlertMessage ?: ""
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
                }
                // Delivery alert msg show
                3 -> {
                    if (selectedDeliveryType != deliveryType) {
                        selectedDeliveryType = deliveryType
                        if (logicExpression.isNotEmpty()) {
                            val calendar = Calendar.getInstance()
                            val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
                            val timeValidity = executeExpression("$hour24 $logicExpression")
                            if (timeValidity) {
                                alert("নির্দেশনা", alertMsg) {
                                }.show()
                            }
                        } else {
                            alert("নির্দেশনা", alertMsg) {
                            }.show()
                        }
                    }
                }
            }

            calculateTotalPrice()

            /*if (model.deliveryType.contains("Postal", true)) {
                alert("নির্দেশনা", alertMsg) {
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
            calculateTotalPrice()
            collectionAmountET.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        /* togglePickupGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
             if (isChecked) {
                 when (checkedId) {
                     R.id.toggleButtonPickup1 ->
                     {
                         isOfficeDrop = true
                         calculateTotalPrice()
                         pickupAddressLayout.visibility = View.GONE
                     }
                     R.id.toggleButtonPickup2 -> {
                         isOfficeDrop = false
                         pickupAddressLayout.visibility = View.VISIBLE
                         calculateTotalPrice()
                     }
                 }
             }
         }*/

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

        checkTermsTV.text = HtmlCompat.fromHtml("আমি <font color='#00844A'>শর্তাবলী</font> মেনে নিলাম", HtmlCompat.FROM_HTML_MODE_LEGACY)
        checkTerms.setOnCheckedChangeListener { compoundButton, b ->
            isAgreeTerms = b
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

        SessionManager.orderSource = "DetailOrder"

        if (BuildConfig.DEBUG) {
            mockUserData()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
                    }
                }
            }
        })
    }

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
        var missingValues = "নতুন অর্ডার করার আগে প্রোফাইল-এ "
        var isMissing = false
        if (model.companyName.isNullOrEmpty()) {
            if (!isMissing) isMissing = true
            missingValues += "মার্চেন্ট/কোম্পানি নাম, "
        }
        if (model.mobile.isNullOrEmpty()) {
            if (!isMissing) isMissing = true
            missingValues += "মোবাইল নাম্বার, "
        }
        if (model.bkashNumber.isNullOrEmpty()) {
            if (!isMissing) isMissing = true
            missingValues += "বিকাশ নম্বর (পেমেন্ট গ্রহনের জন্য), "
        }
        /*if (model.address.isNullOrEmpty()) {
            if(!isMissing) isMissing = true
            missingValues += "বিস্তারিত কালেকশন ঠিকানা (বাড়ি/রোড/হোল্ডিং), "
        }*/
        if (model.emailAddress.isNullOrEmpty()) {
            if (!isMissing) isMissing = true
            missingValues += "ইমেইল "
        }
        if (!SessionManager.isPickupLocationAdded) {
            if (!isMissing) isMissing = true
            missingValues += "পিকআপ লোকেশন "
        }

        missingValues += "যোগ করুন"
        if (isMissing) {
            alert("নির্দেশনা", missingValues, false) {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    addFragment(ProfileFragment.newInstance(false, true), ProfileFragment.tag)
                }
            }.show()
            timber.log.Timber.d("missingValues: $missingValues")
        }

        if (!isPickupLocationListAvailable) {
            getPickupLocation()
        }

        return !isMissing
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("নতুন অর্ডার")
        //isProfileComplete = checkProfileData()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            orderPlaceBtn -> {
                orderPlaceProcess()
            }
        }
    }

    private fun pickupBottomSheet() {
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
            } else {
                isOfficeDrop = true
                isCollectionLocationSelected = false
            }
            calculateTotalPrice()
            isCollectionTypeSelected = true
            orderPlaceBtn.performClick()
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

    /*private fun goToDistrictSelectDialogue(list: MutableList<AllDistrictListsModel>, parentId: Int, operationFlag: Int) {

        val locationList: MutableList<LocationData> = mutableListOf()
        list.forEach { model ->
            locationList.add(LocationData.from(model))
        }

        val dialog = LocationSelectionDialog.newInstance(locationList)
        dialog.show(childFragmentManager, LocationSelectionDialog.tag)
        dialog.onLocationPicked = { _, model ->
            when (operationFlag) {
                1 -> {
                    updateUIAfterDistrict(model)
                }
                2 -> {
                    etThana.setText(model.displayNameBangla)
                    thanaId = model.id
                    areaId = 0
                    etAriaPostOffice.setText("")

                    val filterArea = allLocationList.filter { it.parentId == thanaId } as MutableList<AllDistrictListsModel>
                    Timber.d("filterArea $filterArea")
                    isAriaAvailable = filterArea.isNotEmpty()
                    if (isAriaAvailable) {
                        filteredAreaLists.clear()
                        filteredAreaLists.addAll(filterArea.sortedBy { it.districtPriority })
                        etAriaPostOfficeLayout.visibility = View.VISIBLE
                    } else {
                        etAriaPostOfficeLayout.visibility = View.GONE
                    }

                    getDeliveryCharge(districtId, thanaId, 0, serviceType)
                    Timber.d("filterArea $filteredAreaLists")
                }
                3 -> {
                    areaId = model.id
                    if (!model.displayPostalCode.isNullOrEmpty()) {
                        etAriaPostOffice.setText("${model.displayNameBangla} ${"${model.displayPostalCode}"}")
                        getDeliveryCharge(districtId, thanaId, areaId, serviceType)
                    } else
                        etAriaPostOffice.setText(model.displayNameBangla)
                }
            }
        }

    }*/

    /*private fun goToDistrict() {

        val distFrag = DistrictSelectFragment.newInstance(requireContext(), districtList)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        ft?.add(R.id.mainActivityContainer, distFrag, DistrictSelectFragment.tag)
        ft?.addToBackStack(DistrictSelectFragment.tag)
        ft?.commit()

        distFrag.setOnClick(object : DistrictSelectFragment.DistrictClick {
            override fun onClick(position: Int, name: String, clickedID: Int) {
                //Timber.e("etDistrictSearch 6 - ", "$name $clickedID")
                etDistrict.setText(name)
                districtId = clickedID

                thanaId = 0
                areaId = 0
                etAriaPostOffice.setText("")
                etThana.setText("")
                etAriaPostOfficeLayout.visibility = View.GONE

                if (districtId == 14) {
                    getDeliveryCharge(districtId, 10026, 0) // Fetch data if any district selected
                    codChargePercentage = codChargePercentageInsideDhaka
                } else {
                    getDeliveryCharge(1, 10137, 0)
                    codChargePercentage = codChargePercentageOutsideDhaka
                }
                calculateTotalPrice()
            }
        })
    }*/

    /*private fun thanaAriaSelect(thanaOrAriaList: ArrayList<ThanaPayLoad>, track: Int, list: ArrayList<CustomModel>, title: String) {
        //track = 1 district , track = 2 thana, track = 3 aria
        val distFrag = DistrictThanaAriaSelectFragment.newInstance(requireContext(), list, title)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        ft?.add(R.id.mainActivityContainer, distFrag, DistrictSelectFragment.tag)
        ft?.addToBackStack(DistrictSelectFragment.tag)
        ft?.commit()

        distFrag.onItemClick = { adapterPosition: Int, name: String, id: Int, listPostion ->
            //Timber.e("distFrag1", adapterPosition.toString() + " " + listPostion.toString() + " " + name + " " + id + " " + thanaOrAriaList[listPostion].postalCode + " s")

            if (track == 1) {

            } else if (track == 2) {
                isAriaAvailable = thanaOrAriaList[listPostion].hasArea == 1
                etThana.setText(thanaOrAriaList[listPostion].thanaBng)
                thanaId = thanaOrAriaList[listPostion].thanaId
                areaId = 0
                etAriaPostOffice.setText("")
                if (isAriaAvailable) {
                    etAriaPostOfficeLayout.visibility = View.VISIBLE
                } else {
                    etAriaPostOfficeLayout.visibility = View.GONE
                }
                if (districtId == merchantDistrict) {
                    getDeliveryCharge(14, 10026, 0)
                } else {
                    getDeliveryCharge(districtId, thanaId, 0)
                }
            } else if (track == 3) {
                if (thanaOrAriaList[listPostion].postalCode != null) {
                    if (thanaOrAriaList[listPostion].postalCode!!.isNotEmpty()) {
                        areaId = thanaOrAriaList[listPostion].thanaId
                        etAriaPostOffice.setText(thanaOrAriaList[listPostion].thanaBng + " (" + thanaOrAriaList[listPostion].postalCode + ")")
                        getDeliveryCharge(districtId, thanaId, areaId)
                    } else {
                        areaId = 0
                        // isAriaAvailable = false
                        etAriaPostOffice.setText(thanaOrAriaList[listPostion].thanaBng)
                    }
                } else {
                    areaId = 0
                    etAriaPostOffice.setText(thanaOrAriaList[listPostion].thanaBng)
                }
            }
        }
    }*/

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
                    alert("নির্দেশনা", alertMsg, false).show()
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
            codChargePercentageInsideDhaka = model.codChargeDhakaPercentage
            codChargePercentageOutsideDhaka = model.codChargePercentage
        })

    }

    private fun getCourierUsersInformation() {
        viewModel.getCourierUsersInformation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            collectionChargeApi = model.collectionCharge
            merchantDistrict = model.districtId
            SessionManager.retentionManagerName = model?.adminUsers?.fullName ?: ""
            SessionManager.retentionManagerNumber = model?.adminUsers?.mobile ?: ""
            if (merchantDistrict == 14) {
                loadServiceType()
            } else {
                fetchLocationById(0, LocationType.DISTRICT, false)
            }
            initLocationListener()
        })
    }

    private fun getPackagingCharge() {

        viewModel.getPackagingCharge().observe(viewLifecycleOwner, Observer { list ->
            packagingDataList.clear()
            packagingDataList.addAll(list)

            val packageNameList: MutableList<String> = mutableListOf()
            packageNameList.add("প্যাকেজিং")
            for (model1 in packagingDataList) {
                packageNameList.add(model1.packagingName)
            }

            val packagingAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, packageNameList)
            spinnerPackaging.adapter = packagingAdapter
            spinnerPackaging.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

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
            spinnerPackaging.setSelection(1, false)
        })

    }

    private fun getDeliveryCharge(districtId: Int, thanaId: Int, areaId: Int, serviceType: String) {

        viewModel.getDeliveryCharge(DeliveryChargeRequest(districtId, thanaId, areaId, serviceType)).observe(viewLifecycleOwner, Observer { list ->

            if (serviceType == "citytocity" && list.isEmpty()) {
                this.serviceType = "alltoall"
                getDeliveryCharge(districtId, thanaId, areaId, this.serviceType)
                return@Observer
            }

            val weightList: MutableList<String> = mutableListOf()
            weightList.add("ওজন (কেজি)")
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
                        deliveryTypeAdapter.initLoad(filterDeliveryTypeList)
                        deliveryTypeAdapter.selectPreSelection()
                    } else {
                        isWeightSelected = false
                        deliveryType = ""
                        if (list.isNotEmpty()) {
                            val model2 = list.first()
                            var filterDeliveryTypeList = model2.weightRangeWiseData
                            if (merchantDistrict != 14) {
                                filterDeliveryTypeList = model2.weightRangeWiseData.filterNot { it.type == "express" }
                            }
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
                            }
                        } else {
                            deliveryTypeAdapter.clearList()
                        }
                    }
                }
            }
        })
    }

    //#region Service Type Selection (for Dhaka only)
    private fun loadServiceType() {
        viewModel.fetchServiceInfo().observe(viewLifecycleOwner, Observer { list ->
            serviceTypeList.clear()
            serviceTypeList.addAll(list)
            goToServiceSelectionBottomSheet(list)
        })
    }

    private fun goToServiceSelectionBottomSheet(dataLists: List<ServiceInfoData>) {
        val tag = ServicesSelectionBottomSheet.tag
        val dialog = ServicesSelectionBottomSheet.newInstance(dataLists)
        dialog.show(childFragmentManager, tag)
        dialog.onServiceSelected = { _, service, district ->

            val districtList = service.districtList
            serviceTypeSelected = true
            filteredDistrictLists.clear()
            filteredDistrictLists.addAll(districtList)
            filteredThanaLists.clear()
            filteredAreaLists.clear()

            if (service.deliveryRangeId.isNotEmpty()) {
                if (district.id == 14) { // first dhaka
                    selectedServiceType = service.deliveryRangeId.first()
                } else { // last outside dhaka
                    selectedServiceType = service.deliveryRangeId.last()
                }
                deliveryTypeRV.isVisible = false
            } else {
                deliveryTypeRV.isVisible = true
                selectedServiceType = 0 // default
            }
            updateUIAfterDistrict(district)
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
            context?.toast("লোকেশন লোড হচ্ছে, অপেক্ষা করুন")
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
                            etThana.setText(sadarThana.districtBng)
                            fetchLocationById(thanaId, LocationType.AREA, true)
                            getDeliveryCharge(districtId, thanaId, 0, serviceType)
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
                            etAriaPostOffice.setText(sadarArea.districtBng)
                            getDeliveryCharge(districtId, thanaId, areaId, serviceType)
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

    private fun goToLocationSelectionDialog(list: MutableList<AllDistrictListsModel>, locationType: LocationType) {

        val locationList: MutableList<LocationData> = mutableListOf()
        list.forEach { model ->
            locationList.add(LocationData.from(model))
        }

        val dialog = LocationSelectionDialog.newInstance(locationList)
        dialog.show(childFragmentManager, LocationSelectionDialog.tag)
        dialog.onLocationPicked = { position, model ->
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

                    val locationModel = list[position]
                    showLocationAlert(locationModel, LocationType.DISTRICT)
                }
                LocationType.THANA -> {
                    thanaId = model.id
                    etThana.setText(model.displayNameBangla)
                    areaId = 0
                    etAriaPostOffice.setText("")

                    val locationModel = list[position]
                    showLocationAlert(locationModel, LocationType.THANA)
                    getDeliveryCharge(districtId, thanaId, 0, serviceType)
                    if (filteredAreaLists.isEmpty()) {
                        fetchLocationById(thanaId, LocationType.AREA, true)
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

                    val locationModel = list[position]
                    showLocationAlert(locationModel, LocationType.AREA)

                    getDeliveryCharge(districtId, thanaId, areaId, serviceType)
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

        val selectedDistrict = filteredDistrictLists.find { it.districtId == districtId }
        selectedDistrict?.let { district ->
            showLocationAlert(district, LocationType.DISTRICT)
        }

        serviceType = if (merchantDistrict == districtId) {
            "citytocity"
        } else "alltoall"
        codChargePercentage = if (districtId == 14) {
            codChargePercentageInsideDhaka
        } else {
            codChargePercentageOutsideDhaka
        }
        calculateTotalPrice()
        fetchLocationById(districtId, LocationType.THANA, true)

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

    private fun showLocationAlert(model: AllDistrictListsModel, locationType: LocationType) {
        if (model.isActiveForCorona) {
            val msg = when (locationType) {
                LocationType.DISTRICT -> "${model.districtBng} জেলায় ডেলিভারি সার্ভিস সাময়িকভাবে বন্ধ রয়েছে।"
                LocationType.THANA -> "${model.districtBng} থানায় ডেলিভারি সার্ভিস সাময়িকভাবে বন্ধ রয়েছে।"
                LocationType.AREA -> "${model.districtBng} এরিয়ায় ডেলিভারি সার্ভিস সাময়িকভাবে বন্ধ রয়েছে।"
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

    private fun getPickupLocation() {

        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                //setUpCollectionSpinner(list, null, 1)
                //collectionAddressET.visibility = View.GONE
                isPickupLocationListAvailable = true
                SessionManager.isPickupLocationAdded = true
            } else {
                //getDistrictThanaOrAria(14)
                //collectionAddressET.visibility = View.VISIBLE
                isPickupLocationListAvailable = false
                SessionManager.isPickupLocationAdded = false
            }
            if (!isPickupLocationFirstLoad) {
                isPickupLocationFirstLoad = true
                isProfileComplete = checkProfileData()
            }
        })
    }

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

    private fun fetchCollectionTimeSlot() {
        viewModel.fetchCollectionTimeSlot().observe(viewLifecycleOwner, Observer { list ->
            setUpCollectionSlotSpinner(list)
        })
    }

    private fun setUpCollectionSlotSpinner(list: List<TimeSlotData>) {

        val slotList: MutableList<String> = mutableListOf()
        slotList.add("সিলেক্ট কালেকশন টাইম")
        val sdf24 = SimpleDateFormat("HH:mm:ss", Locale.US)
        val sdf12 = SimpleDateFormat("hh:mm a", Locale.US)
        list.forEach { data ->
            try {
                val startTime = sdf12.format(sdf24.parse(data.startTime))
                val endTime = sdf12.format(sdf24.parse(data.endTime))
                slotList.add("$startTime - $endTime")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val collectionSlotAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, slotList)
        collectionTimeSlotSpinner.adapter = collectionSlotAdapter
        collectionTimeSlotSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0) {
                    val model = list[p2 - 1]
                    timeSlotId = model.collectionTimeSlotId
                    selectedCollectionSlotDate = "$collectionSlotDate ${model.endTime}"
                }
            }
        }
    }

    private fun setUpCollectionSpinner(pickupParentList: List<PickupLocation>?, thanaOrAriaList: List<ThanaPayLoad>?, optionFlag: Int) {

        val pickupList: MutableList<String> = mutableListOf()
        pickupList.add("পিক আপ লোকেশন")
        if (optionFlag == 1) {
            pickupParentList?.forEach {
                pickupList.add(it.thanaName ?: "")
            }
        } else if (optionFlag == 2) {
            thanaOrAriaList?.forEach {
                pickupList.add(it.thanaBng ?: "")
            }
        }

        val pickupAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupList)
        spinnerCollectionLocation.adapter = pickupAdapter
        spinnerCollectionLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (position != 0) {
                    if (optionFlag == 1) {
                        val model = pickupParentList!![position - 1]
                        collectionAddress = model.pickupAddress ?: ""
                        collectionAddressET.setText(collectionAddress)
                        collectionDistrictId = model.districtId
                        collectionThanaId = model.thanaId
                    } else if (optionFlag == 2) {
                        val model = thanaOrAriaList!![position - 1]
                        collectionAddress = ""
                        collectionAddressET.setText(collectionAddress)
                        collectionDistrictId = 14
                        collectionThanaId = model.thanaId
                    }
                    if (districtId == collectionDistrictId) {
                        getDeliveryCharge(14, 10026, 0, serviceType)
                    }
                    /*deliveryTypeAdapter.clearSelectedItemPosition()
                    deliveryTypeAdapter.notifyDataSetChanged()
                    deliveryType = ""
                    payShipmentCharge = 0.0*/

                    isCollectionLocationSelected = true
                    calculateTotalPrice()
                } else {
                    isCollectionLocationSelected = false
                }
            }
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
            payCODCharge = (payCollectionAmount / 100.0) * codChargePercentage
            if (payCODCharge < codChargeMin) {
                payCODCharge = codChargeMin.toDouble()
            }
            if (SessionManager.maxCodCharge != 0.0) {
                if (payCODCharge > SessionManager.maxCodCharge) {
                    payCODCharge = SessionManager.maxCodCharge
                }
            }

        } else {
            payCollectionAmount = 0.0
            payCODCharge = 0.0
        }

        if (isOfficeDrop) {
            payCollectionCharge = 0.0
        } else {
            payCollectionCharge = collectionChargeApi
        }

        //val payReturnCharge = SessionManager.returnCharge
        if (isCheckBigProduct) {
            total = payDeliveryCharge + payCODCharge + payCollectionCharge + payPackagingCharge + bigProductCharge
        } else {
            total = payDeliveryCharge + payCODCharge + payCollectionCharge + payPackagingCharge
        }


        //   total = payDeliveryCharge + payCODCharge + payCollectionCharge + payPackagingCharge

        if (isBreakable) {
            payBreakableCharge = breakableChargeApi
            total += payBreakableCharge
        } else {
            payBreakableCharge = 0.0
        }

        totalTV.text = "${DigitConverter.toBanglaDigit(total.toInt(), true)} ৳"

    }

    private fun submitOrder() {

        if (!validate() || !validateFormData()) {
            return
        }
        calculateTotalPrice()

        val dialog = ProgressDialog(context)
        dialog.setMessage("অপেক্ষা করুন")
        dialog.setCancelable(false)
        dialog.show()

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
            deliveryDate, collectionDate, isOfficeDrop, payActualPackagePrice, timeSlotId, selectedCollectionSlotDate, offerType, relationType, serviceType
        )


        viewModel.placeOrder(requestBody).observe(viewLifecycleOwner, Observer { model ->
            dialog?.hide()
            SessionManager.totalAmount = total.toInt()
            addOrderSuccessFragment(model)
        })

    }

    private fun validate(): Boolean {
        var go = true
        getAllViewData()
        if (districtId == 0) {
            go = false
            context?.toast(getString(R.string.select_dist))
        } else if (thanaId == 0) {
            go = false
            context?.toast(getString(R.string.select_thana))
        } else if (isAriaAvailable && areaId == 0) {
            go = false
            context?.toast(getString(R.string.select_aria))
        } else if (customerName.isEmpty()) {
            context?.toast(getString(R.string.write_yr_name))
            go = false
            etCustomerName.requestFocus()
        } else if (mobileNo.isEmpty()) {
            context?.toast(getString(R.string.write_phone_number))
            go = false
            etAddOrderMobileNo.requestFocus()
        } else if (!Validator.isValidMobileNumber(mobileNo) || mobileNo.length < 11) {
            context?.toast(getString(R.string.write_proper_phone_number_recharge))
            go = false
            etAddOrderMobileNo.requestFocus()
        }/* else if(alternativeMobileNo.isEmpty()){
            go = false
            context?.toast(context!!, getString(R.string.write_alt_phone_number))
            etAlternativeMobileNo.requestFocus()
        }*/
          else if (customersAddress.isEmpty() || customersAddress.trim().length < 15) {
            go = false
            context?.toast(getString(R.string.write_yr_address))
            etCustomersAddress.requestFocus()
        }
        hideKeyboard()

        //go = true

        return go
    }

    private fun validateFormData(): Boolean {

        collectionName = productNameET.text.toString()
        collectionAddress = collectionAddressET.text.toString()

        if (!isOrderTypeSelected) {
            context?.showToast("অর্ডার টাইপ সিলেক্ট করুন")
            return false
        }

        if (!isWeightSelected) {
            context?.showToast("প্যাকেজ এর ওজন নির্বাচন করুন")
            return false
        }

        if (isCollection) {
            val collectionAmount = collectionAmountET.text.toString()
            if (collectionAmount.isEmpty()) {
                context?.showToast("কালেকশন অ্যামাউন্ট লিখুন")
                return false
            }
            try {
                payCollectionAmount = collectionAmount.toDouble()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                context?.showToast("কালেকশন অ্যামাউন্ট লিখুন")
                return false
            }
            if (payCollectionAmount > collectionAmountLimit) {
                context?.showToast("কালেকশন অ্যামাউন্ট ${DigitConverter.toBanglaDigit(collectionAmountLimit.toInt())} টাকার থেকে বেশি হতে পারবে না")
                return false
            }
            actualPackageAmountET.setText(payCollectionAmount.toInt().toString())
        }


        val payActualPackagePriceText = actualPackageAmountET.text.toString().trim()
        if (payActualPackagePriceText.isEmpty()) {
            context?.showToast("প্যাকেজের দাম লিখুন")
            return false
        } else {
            try {
                payActualPackagePrice = payActualPackagePriceText.toDouble()
                if (!isCollection) {
                    if (payActualPackagePrice > actualPackagePriceLimit) {
                        context?.showToast("অ্যাকচুয়াল প্যাকেজ প্রাইস ${DigitConverter.toBanglaDigit(actualPackagePriceLimit.toInt())} টাকার থেকে বেশি হতে পারবে না")
                        return false
                    }
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                context?.showToast("অ্যাকচুয়াল প্যাকেজ প্রাইস লিখুন")
                return false
            }
        }

        if (collectionName.isEmpty()) {
            context?.showToast("নিজস্ব রেফারেন্স নম্বর / ইনভয়েস লিখুন")
            return false
        }
        if (!isPackagingSelected) {
            context?.showToast("প্যাকেজিং নির্বাচন করুন")
            return false
        }

        if (deliveryType.isEmpty()) {
            context?.showToast("ডেলিভারি টাইপ নির্বাচন করুন")
            return false
        }
        /*if (collectionSlotDate.isEmpty()) {
            context?.showToast("কালেকশন তারিখ নির্বাচন করুন")
            return false
        }
        if (timeSlotId == 0) {
            context?.showToast("কালেকশন টাইম স্লট নির্বাচন করুন")
            return false
        }*/
        if (deliveryDatePicker.visibility == View.VISIBLE && deliveryDate.isEmpty()) {
            context?.showToast("ডেলিভারি তারিখ নির্বাচন করুন")
            return false
        }
        if (isCollection && payCollectionAmount <= total) {
            context?.showToast("কালেকশন অ্যামাউন্ট সার্ভিস চার্জ থেকে বেশি হতে হবে")
            return false
        }

        if (!isAgreeTerms) {
            context?.showToast("শর্তাবলী মেনে অর্ডার দিন")
            return false
        }

        if (!isCollectionTypeSelected) {
            pickupBottomSheet()
            return false
        }

        // Hub drop is must for weight > 5kg
        /*if (weightRangeId > 6 && !isOfficeDrop) {
            context?.showToast("পার্সেলের ওজন ৫ কেজির উপরে হলে কালেকশন হাবে ড্রপ করতে হবে")
            isCollectionTypeSelected = false
            return false
        }*/

        if (!isOfficeDrop) {
            if (!isCollectionLocationSelected) {
                context?.showToast("কালেকশন লোকেশন নির্বাচন করুন")
                return false
            }
            if (collectionAddress.trim().isEmpty()) {
                context?.showToast("কালেকশন ঠিকানা লিখুন")
                return false
            }
            if (collectionAddress.trim().length < 15) {
                context?.showToast("বিস্তারিত কালেকশন ঠিকানা লিখুন, ন্যূনতম ১৫ ডিজিট")
                return false
            }
        }

        return true
    }

    private fun addOrderSuccessFragment(orderResponse: OrderResponse?) {

        val bundle = bundleOf(
            "isCollection" to isCollection,
            "orderResponse" to orderResponse
        )

        activity?.onBackPressed()
        activity?.onBackPressed()
        val fragment = OrderSuccessFragment.newInstance(bundle)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, OrderSuccessFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    private fun showCreditLimitAlert() {
        val msg = "আপনার প্রি-পেইড অর্ডার করার জন্য পর্যাপ্ত ব্যালান্স নেই। আপনার বর্তমান ব্যালান্স <font color='#00844A'>${
            DigitConverter.toBanglaDigit(
                adjustBalance,
                true
            )
        }</font> টাকা। অনুগ্রহপূর্বক ব্যালান্স লোড করুন।"
        alert("নির্দেশনা", HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY), false, "ব্যালান্স লোড", "") {
            if (it == AlertDialog.BUTTON_POSITIVE) {
                if (activity != null) {
                    (activity as HomeActivity).goToBalanceLoad()
                }
            }
        }.show()
    }

    private fun showCreditLimitReachAlert() {
        alert(
            "নির্দেশনা",
            "আপনার ব্যালান্স (৳${DigitConverter.toBanglaDigit(adjustBalance)}) প্রায় শেষ। আপনি আর অল্প কিছু সংখ্যক প্রি-পেইড অর্ডার করতে পারবেন। অনুগ্রহপূর্বক ব্যালান্স রিচার্জ করুন।",
            false,
            "ঠিক আছে",
            ""
        ) {
            if (it == AlertDialog.BUTTON_POSITIVE) {
                submitOrder()
            }
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

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

}
