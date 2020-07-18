package com.bd.deliverytiger.app.ui.add_order

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.WeightRangeWiseData
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.ThanaPayLoad
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.district.v2.CustomModel
import com.bd.deliverytiger.app.ui.district.v2.DistrictThanaAriaSelectFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.button.MaterialButtonToggleGroup
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class AddOrderFragmentOne : Fragment(), View.OnClickListener {

    //Step 1
    private lateinit var etCustomerName: EditText
    private lateinit var etAddOrderMobileNo: EditText
    private lateinit var etAlternativeMobileNo: EditText
    private lateinit var etDistrict: EditText
    private lateinit var etThana: EditText
    private lateinit var etAriaPostOffice: EditText
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
    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup


    private lateinit var totalTV: TextView
    private lateinit var totalLayout: LinearLayout
    private lateinit var deliveryDatePicker: TextView
    private lateinit var collectionDatePicker: TextView
    private lateinit var checkOfficeDrop: AppCompatCheckBox
    private lateinit var spinnerCollectionLocation: AppCompatSpinner
    private lateinit var orderPlaceBtn: TextView

    private lateinit var deliveryTypeAdapter: DeliveryTypeAdapter
    private var handler: Handler = Handler()
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
    private val districtList: ArrayList<DistrictDeliveryChargePayLoad> = ArrayList()
    private val thanaOrAriaList: ArrayList<ThanaPayLoad> = ArrayList()
    private var isAriaAvailable = true
    private var isMerchantCreditAvailable: Boolean = false

    // Step 2
    private val packagingDataList: MutableList<PackagingData> = mutableListOf()
    private val deliveryTypeList: MutableList<WeightRangeWiseData> = mutableListOf()

    private var codChargePercentage: Double = 0.0
    private var codChargeMin: Int = 0
    private var breakableChargeApi: Double = 0.0
    private var bigProductCharge: Double = 0.0
    private var isCheckBigProduct: Boolean = false

    private var isCollection: Boolean = false
    private var isBreakable: Boolean = false
    private var isAgreeTerms: Boolean = false
    private var isWeightSelected: Boolean = false
    private var isPackagingSelected: Boolean = false
    private var payCollectionAmount: Double = 0.0
    private var payShipmentCharge: Double = 0.0
    private var payCODCharge: Double = 0.0
    private var payBreakableCharge: Double = 0.0
    private var payCollectionCharge: Double = 0.0
    private var payPackagingCharge: Double = 0.0
    private var isOpenBoxCheck: Boolean = false
    private var isOfficeDrop: Boolean = false
    private var isCollectionLocationSelected: Boolean = false

    private var total: Double = 0.0

    private var deliveryType: String = ""
    private var orderType: String = "Only Delivery"
    private var productType: String = "small"
    private var weight: String = ""
    private var collectionName: String = ""
    private var packingName: String = ""
    private var collectionAddress: String = ""

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

    private val viewModel: AddOrderViewModel by inject()

    companion object {
        fun newInstance(): AddOrderFragmentOne = AddOrderFragmentOne()
        val tag: String = AddOrderFragmentOne::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_order_fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Step 1
        etCustomerName = view.findViewById(R.id.etCustomerName)
        etAddOrderMobileNo = view.findViewById(R.id.etAddOrderMobileNo)
        etAlternativeMobileNo = view.findViewById(R.id.etAlternativeMobileNo)
        etDistrict = view.findViewById(R.id.etDistrict)
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
        toggleButtonGroup = view.findViewById(R.id.toggle_button_group)


        totalTV = view.findViewById(R.id.tvAddOrderTotalOrder)
        totalLayout = view.findViewById(R.id.payment_details)
        deliveryDatePicker = view.findViewById(R.id.deliveryDatePicker)
        collectionDatePicker = view.findViewById(R.id.collectionDatePicker)
        checkOfficeDrop = view.findViewById(R.id.checkOfficeDrop)
        spinnerCollectionLocation = view.findViewById(R.id.spinnerCollectionLocation)
        orderPlaceBtn = view.findViewById(R.id.orderPlaceBtn)

        etDistrict.setOnClickListener(this)
        etThana.setOnClickListener(this)
        etAriaPostOffice.setOnClickListener(this)
        orderPlaceBtn.setOnClickListener(this)

        getBreakableCharge()
        getPackagingCharge()
        getPickupLocation()


        deliveryTypeAdapter = DeliveryTypeAdapter(requireContext(), deliveryTypeList)
        with(deliveryTypeRV) {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            layoutAnimation = null
            adapter = deliveryTypeAdapter
        }
        deliveryTypeAdapter.onItemClick = { position, model ->
            //deliveryTypeRV.requestFocus()
            payShipmentCharge = model.chargeAmount
            deliveryType = "${model.deliveryType} ${model.days}"
            deliveryRangeId = model.deliveryRangeId
            weightRangeId = model.weightRangeId

            alertMsg = model.deliveryAlertMessage ?: ""
            logicExpression = model.loginHours ?: ""
            dayAdvance = model.dateAdvance ?: ""
            val showHide = model.showHide

            when (showHide) {
                // Hide All fields
                0 -> {
                    deliveryDatePicker.visibility = View.GONE
                    collectionDatePicker.visibility = View.GONE
                    checkOfficeDrop.visibility = View.GONE
                    checkOfficeDrop.isChecked = false
                    deliveryDate = ""
                    deliveryDatePicker.text = ""
                    collectionDate = ""
                    collectionDatePicker.text = ""
                }
                // Show delivery date collection date
                1 -> {
                    deliveryDatePicker.visibility = View.VISIBLE
                    collectionDatePicker.visibility = View.VISIBLE
                    checkOfficeDrop.visibility = View.GONE
                    checkOfficeDrop.isChecked = false
                    deliveryDate = ""
                    deliveryDatePicker.text = ""
                    collectionDate = ""
                    collectionDatePicker.text = ""
                }
                // Show office drop
                2 -> {
                    deliveryDatePicker.visibility = View.GONE
                    collectionDatePicker.visibility = View.GONE
                    checkOfficeDrop.visibility = View.VISIBLE
                    deliveryDate = ""
                    deliveryDatePicker.text = ""
                    collectionDate = ""
                    collectionDatePicker.text = ""
                }
            }

            calculateTotalPrice()

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
                    Timber.d(logTag, "$p0")
                }
                handler.postDelayed(runnable, 400L)
            }

        })

        toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.toggle_button_1 -> {
                        collectionAmountET.visibility = View.GONE
                        isCollection = false
                        orderType = "Only Delivery"
                    }
                    R.id.toggle_button_2 -> {
                        collectionAmountET.visibility = View.VISIBLE
                        isCollection = true
                        orderType = "Delivery Taka Collection"
                        calculateTotalPrice()
                    }
                }
            }
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

        checkOfficeDrop.setOnCheckedChangeListener { buttonView, isChecked ->
            isOfficeDrop = isChecked
            calculateTotalPrice()
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
                putDouble("payShipmentCharge", payShipmentCharge)
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getMerchantCredit().observe(viewLifecycleOwner, Observer {
            isMerchantCreditAvailable = it
            if (!isMerchantCreditAvailable) {
                showCreditLimitAlert()
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
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("নতুন অর্ডার")
    }

    override fun onClick(p0: View?) {
        when (p0) {
            // same action tvDetails n tvAddOrderTotalOrder
            etDistrict -> {
                if (districtList.isEmpty()) {
                    getDistrictThanaOrAria(0, 1)
                } else {
                    goToDistrict()
                }
            }
            etThana -> {
                if (districtId != 0) {
                    getDistrictThanaOrAria(districtId, 2)
                } else {
                    context?.toast(getString(R.string.select_dist))
                }
            }
            etAriaPostOffice -> {
                if (isAriaAvailable) {
                    if (thanaId != 0) {
                        getDistrictThanaOrAria(thanaId, 3)
                    } else {
                        context?.toast(getString(R.string.select_thana))
                    }
                } else {
                    context?.toast(getString(R.string.no_aria))
                }
            }

            orderPlaceBtn -> {
                if (isMerchantCreditAvailable) {
                    submitOrder()
                } else {
                    showCreditLimitAlert()
                }
            }

        }
    }

    private fun validate(): Boolean {
        var go = true
        getAllViewData()
        if (customerName.isEmpty()) {
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
        else if (districtId == 0) {
            go = false
            context?.toast(getString(R.string.select_dist))
        } else if (thanaId == 0) {
            go = false
            context?.toast(getString(R.string.select_thana))
        } else if (isAriaAvailable && areaId == 0) {
            go = false
            context?.toast(getString(R.string.select_aria))
        } else if (customersAddress.isEmpty() || customersAddress.trim().length < 15) {
            go = false
            context?.toast(getString(R.string.write_yr_address))
            etCustomersAddress.requestFocus()
        }
        hideKeyboard()

        //mockUserData()
        //go = true

        return go
    }

    private fun getAllViewData() {
        customerName = etCustomerName.text.toString()
        mobileNo = etAddOrderMobileNo.text.toString()
        alternativeMobileNo = etAlternativeMobileNo.text.toString()
        customersAddress = etCustomersAddress.text.toString()
        additionalNote = etAdditionalNote.text.toString()
    }

    private fun getDistrictThanaOrAria(id: Int, track: Int) {
        hideKeyboard()
        //track = 1 district , track = 2 thana, track = 3 aria
        val dialog = progressDialog()
        dialog.show()
        viewModel.getAllDistrictFromApi(id).observe(viewLifecycleOwner, Observer {
            dialog.dismiss()
            if (track == 1) {
                districtList.addAll(it)
                goToDistrict()
            } else if (track == 2) {
                thanaOrAriaList.clear()
                thanaOrAriaList.addAll(it[0].thanaHome!!)
                if (thanaOrAriaList.isNotEmpty()) {
                    //customAlertDialog(thanaOrAriaList, 1)
                    val mList: ArrayList<CustomModel> = ArrayList()
                    for ((index, model) in thanaOrAriaList.withIndex()) {
                        mList.add(CustomModel(model.thanaId, model.thanaBng + "", model.thana + "", index))
                    }
                    thanaAriaSelect(thanaOrAriaList, 2, mList, "থানা নির্বাচন করুন")
                }
            } else if (track == 3) {
                thanaOrAriaList.clear()
                thanaOrAriaList.addAll(it[0].thanaHome!!)
                if (thanaOrAriaList.isNotEmpty()) {
                    // customAlertDialog(thanaOrAriaList, 2)
                    val mList: ArrayList<CustomModel> = ArrayList()
                    var temp = 0
                    for ((index, model) in thanaOrAriaList.withIndex()) {
                        temp = 0
                        if (model.postalCode != null && model.postalCode?.isNotEmpty()!!) {
                            temp = model.postalCode?.toInt()!!
                        }
                        mList.add(CustomModel(temp, model.thanaBng + "", model.thana + "", index))
                    }
                    thanaAriaSelect(thanaOrAriaList, 3, mList, "এরিয়া/পোস্ট অফিস নির্বাচন করুন")
                }
            }
        })
    }

    private fun goToDistrict() {

        val distFrag = DistrictSelectFragment.newInstance(requireContext(), districtList)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        ft?.add(R.id.mainActivityContainer, distFrag, DistrictSelectFragment.tag)
        ft?.addToBackStack(DistrictSelectFragment.tag)
        ft?.commit()

        distFrag.setOnClick(object : DistrictSelectFragment.DistrictClick {
            override fun onClick(position: Int, name: String, clickedID: Int) {
                Timber.e("etDistrictSearch 6 - ", name + " " + clickedID.toString())
                etDistrict.setText(name)
                districtId = clickedID

                thanaId = 0
                areaId = 0
                etAriaPostOffice.setText("")
                etThana.setText("")
                etAriaPostOffice.visibility = View.GONE
            }
        })
    }

    private fun thanaAriaSelect(thanaOrAriaList: ArrayList<ThanaPayLoad>, track: Int, list: ArrayList<CustomModel>, title: String) {
        //track = 1 district , track = 2 thana, track = 3 aria
        val distFrag = DistrictThanaAriaSelectFragment.newInstance(requireContext(), list, title)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        ft?.add(R.id.mainActivityContainer, distFrag, DistrictSelectFragment.tag)
        ft?.addToBackStack(DistrictSelectFragment.tag)
        ft?.commit()

        distFrag.onItemClick = { adapterPosition: Int, name: String, id: Int, listPostion ->
            Timber.e("distFrag1", adapterPosition.toString() + " " + listPostion.toString() + " " + name + " " + id + " " + thanaOrAriaList[listPostion].postalCode + " s")

            if (track == 1) {

            } else if (track == 2) {
                isAriaAvailable = thanaOrAriaList[listPostion].hasArea == 1
                etThana.setText(thanaOrAriaList[listPostion].thanaBng)
                thanaId = thanaOrAriaList[listPostion].thanaId
                areaId = 0
                etAriaPostOffice.setText("")
                if (isAriaAvailable) {
                    etAriaPostOffice.visibility = View.VISIBLE
                } else {
                    etAriaPostOffice.visibility = View.GONE
                }
                getDeliveryCharge()
            } else if (track == 3) {
                if (thanaOrAriaList[listPostion].postalCode != null) {
                    if (thanaOrAriaList[listPostion].postalCode!!.isNotEmpty()) {
                        areaId = thanaOrAriaList[listPostion].thanaId
                        etAriaPostOffice.setText(thanaOrAriaList[listPostion].thanaBng + " (" + thanaOrAriaList[listPostion].postalCode + ")")
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
                Timber.d("error", "Msg: ${e.message}")
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

        val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

            if (dateTypeFlag == 1) {
                deliveryDate = "${month + 1}/$dayOfMonth/$year"
                deliveryDatePicker.text = DigitConverter.toBanglaDigit(deliveryDate)
                collectionDatePicker.text = DigitConverter.toBanglaDigit(deliveryDate)
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
            codChargePercentage = model.codChargePercentage
            codChargeMin = model.codChargeMin
            bigProductCharge = model.bigProductCharge
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

    private fun getDeliveryCharge() {

        viewModel.getDeliveryCharge(DeliveryChargeRequest(districtId, thanaId)).observe(viewLifecycleOwner, Observer { list ->
            val weightList: MutableList<String> = mutableListOf()
            weightList.add("ওজন (কেজি)")
            for (model1 in list) {
                weightList.add(model1.weight)
            }

            val weightAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, weightList)
            spinnerWeight.adapter = weightAdapter
            spinnerWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    if (p2 != 0) {

                        val model2 = list[p2 - 1]
                        weight = model2.weight
                        deliveryTypeAdapter.clearSelectedItemPosition()
                        deliveryTypeList.clear()
                        deliveryTypeList.addAll(model2.weightRangeWiseData)
                        deliveryTypeAdapter.notifyDataSetChanged()
                        isWeightSelected = true
                    } else {
                        isWeightSelected = false
                        deliveryTypeAdapter.clearSelectedItemPosition()
                        deliveryTypeList.clear()
                        deliveryTypeAdapter.notifyDataSetChanged()
                    }
                }
            }
        })


    }

    private fun getPickupLocation() {

        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                setUpCollectionSpinner(list, null, 1)
                collectionAddressET.visibility = View.GONE
            } else {
                getDistrictThanaOrAria(14)
                collectionAddressET.visibility = View.VISIBLE
            }
        })
    }

    private fun getDistrictThanaOrAria(districtId: Int) {

        viewModel.getAllDistrictFromApi(districtId).observe(viewLifecycleOwner, Observer { list ->
            setUpCollectionSpinner(null, list.first().thanaHome, 2)
        })
    }

    private fun setUpCollectionSpinner(pickupParentList: List<PickupLocation>?, thanaOrAriaList: List<ThanaPayLoad>?, optionFlag: Int) {

        val pickupList: MutableList<String> = mutableListOf()
        pickupList.add("কালেকশন লোকেশন")
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
                    isCollectionLocationSelected = true
                } else {
                    isCollectionLocationSelected = false
                }
            }
        }
    }

    private fun calculateTotalPrice() {

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
            payCollectionCharge = SessionManager.collectionCharge
        }

        //val payReturnCharge = SessionManager.returnCharge
        if (isCheckBigProduct) {
            total = payShipmentCharge + payCODCharge + payCollectionCharge + payPackagingCharge + bigProductCharge
        } else {
            total = payShipmentCharge + payCODCharge + payCollectionCharge + payPackagingCharge
        }


        //   total = payShipmentCharge + payCODCharge + payCollectionCharge + payPackagingCharge

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

        if (productType.equals("small")) {
            payShipmentCharge
        } else {
            payShipmentCharge = payShipmentCharge + bigProductCharge
        }


        val requestBody = OrderRequest(
            customerName, mobileNo, alternativeMobileNo, customersAddress, districtId, thanaId, areaId,
            deliveryType, orderType, weight, collectionName,
            payCollectionAmount, payShipmentCharge, SessionManager.courierUserId,
            payBreakableCharge, additionalNote, payCODCharge, payCollectionCharge, SessionManager.returnCharge, packingName,
            payPackagingCharge, collectionAddress, productType, deliveryRangeId, weightRangeId, isOpenBoxCheck,
            "android-${SessionManager.versionName}", true, collectionDistrictId, collectionThanaId, deliveryDate, collectionDate, isOfficeDrop
        )


        viewModel.placeOrder(requestBody).observe(viewLifecycleOwner, Observer { model ->
            dialog?.hide()
            addOrderSuccessFragment(model)
        })

    }

    private fun validateFormData(): Boolean {

        collectionName = productNameET.text.toString()
        collectionAddress = collectionAddressET.text.toString()
        if (collectionName.isEmpty()) {
            context?.showToast("প্রোডাক্টের নাম লিখুন")
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
        }
        if (!isWeightSelected) {
            context?.showToast("প্যাকেজ এর ওজন নির্বাচন করুন")
            return false
        }
        if (!isPackagingSelected) {
            context?.showToast("প্যাকেজিং নির্বাচন করুন")
            return false
        }
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
        if (deliveryType.isEmpty()) {
            context?.showToast("ডেলিভারি টাইপ নির্বাচন করুন")
            return false
        }
        if (deliveryDatePicker.visibility == View.VISIBLE && deliveryDate.isEmpty()) {
            context?.showToast("ডেলিভারি তারিখ নির্বাচন করুন")
            return false
        }
        if (!isAgreeTerms) {
            context?.showToast("শর্তাবলী মেনে অর্ডার দিন")
            return false
        }

        return true
    }

    private fun addOrderSuccessFragment(orderResponse: OrderResponse?) {
        activity?.onBackPressed()
        activity?.onBackPressed()
        val fragment = OrderSuccessFragment.newInstance(orderResponse)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, OrderSuccessFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    private fun showCreditLimitAlert() {
        alert("নির্দেশনা", "আপনার ক্রেডিট লিমিট শেষ হয়ে গিয়েছে। অনুগ্রহপূর্বক সাপোর্ট এর সাথে যোগাযোগ করুন।", false).show()
    }

    private fun mockUserData() {
        customerName = "Test Customer Name"
        mobileNo = "01555555555"
        alternativeMobileNo = "01555555556"
        districtId = 14
        thanaId = 10026
        areaId = 0
        customersAddress = "Test Customer Address from IT"
        additionalNote = "This is test order from IT"
    }

}
