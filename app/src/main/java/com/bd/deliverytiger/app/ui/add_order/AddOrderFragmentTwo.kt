package com.bd.deliverytiger.app.ui.add_order


import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.DistrictInterface
import com.bd.deliverytiger.app.api.`interface`.PlaceOrderInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.charge.WeightRangeWiseData
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.ThanaPayLoad
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.button.MaterialButtonToggleGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddOrderFragmentTwo : Fragment() {

    private val logTag = "AddOrderFragmentLog"
    private lateinit var productNameET: EditText
    private lateinit var collectionAmountET: EditText
    private lateinit var spinnerWeight: AppCompatSpinner
    private lateinit var spinnerPackaging: AppCompatSpinner
    private lateinit var checkBoxBreakable: AppCompatCheckBox
    private lateinit var collectionAddressET: EditText
    private lateinit var checkTerms: AppCompatCheckBox
    private lateinit var checkTermsTV: TextView
    private lateinit var deliveryTypeRV: RecyclerView
    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup
    private lateinit var toggleButtonGroupSize: MaterialButtonToggleGroup
    private lateinit var submitBtn: LinearLayout
    private lateinit var backBtn: ConstraintLayout
    private lateinit var totalTV: TextView
    private lateinit var totalLayout: ConstraintLayout
    private lateinit var deliveryDatePicker: TextView
    private lateinit var collectionDatePicker: TextView
    private lateinit var checkOfficeDrop: AppCompatCheckBox
    private lateinit var checkOpenBox: AppCompatCheckBox
    private lateinit var spinnerCollectionLocation: AppCompatSpinner


    private lateinit var placeOrderInterface: PlaceOrderInterface
    private lateinit var districtInterface: DistrictInterface
    private lateinit var deliveryTypeAdapter: DeliveryTypeAdapter
    private var handler: Handler = Handler()
    private var runnable: Runnable = Runnable {  }

    // API variable
    private val packagingDataList: MutableList<PackagingData> = mutableListOf()
    private val deliveryTypeList: MutableList<WeightRangeWiseData> = mutableListOf()

    private var codChargePercentage: Double = 0.0
    private var codChargeMin: Int = 0
    private var breakableChargeApi: Double = 0.0
    private var bigProductCharge: Double = 0.0
    private var isCheckBigProduct : Boolean = false

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

    // Bundle
    private var bundle: Bundle? = null
    private var customerName: String = ""
    private var mobileNumber: String = ""
    private var altMobileNumber: String = ""
    private var districtId: Int = 0
    private var thanaId: Int = 0
    private var areaId: Int = 0
    private var address: String = ""
    private var addressNote: String = ""
    private var deliveryType: String = ""
    private var orderType: String = "Only Delivery" // Only Delivery  Delivery Taka Collection
    private var productType: String = "small" // Only Delivery  Delivery Taka Collection
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


    companion object {
        fun newInstance(bundle: Bundle?): AddOrderFragmentTwo = AddOrderFragmentTwo().apply {
            this.bundle = bundle
        }

        val tag = AddOrderFragmentTwo::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_order_fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        productNameET = view.findViewById(R.id.productName)
        collectionAmountET = view.findViewById(R.id.collectionAmount)
        spinnerWeight = view.findViewById(R.id.spinner_weight_selection)
        spinnerPackaging = view.findViewById(R.id.spinner_packaging_selection)
        checkBoxBreakable = view.findViewById(R.id.check_breakable)
        collectionAddressET = view.findViewById(R.id.collectionAddress)
        checkTerms = view.findViewById(R.id.check_terms_condition)
        checkTermsTV = view.findViewById(R.id.check_terms_condition_text)
        deliveryTypeRV = view.findViewById(R.id.delivery_type_selection_rV)
        toggleButtonGroup = view.findViewById(R.id.toggle_button_group)
        toggleButtonGroupSize = view.findViewById(R.id.toggle_button_group_size)
        submitBtn = view.findViewById(R.id.submit_order)
        backBtn = view.findViewById(R.id.go_to_previous_page)
        totalTV = view.findViewById(R.id.tvAddOrderTotalOrder)
        totalLayout = view.findViewById(R.id.addOrderTopLay)
        deliveryDatePicker = view.findViewById(R.id.deliveryDatePicker)
        collectionDatePicker = view.findViewById(R.id.collectionDatePicker)
        checkOfficeDrop = view.findViewById(R.id.checkOfficeDrop)
        checkOpenBox = view.findViewById(R.id.checkOpenBox)
        spinnerCollectionLocation = view.findViewById(R.id.spinnerCollectionLocation)



        with(bundle) {
            this?.let {
                customerName = getString(BundleFlag.CUSTOMER_NAME, "")
                mobileNumber = getString(BundleFlag.MOBILE_NUMBER, "")
                altMobileNumber = getString(BundleFlag.ALT_MOBILE_NUMBER, "")
                districtId = getInt(BundleFlag.DISTRICT_ID, 0)
                thanaId = getInt(BundleFlag.THANA_ID, 0)
                areaId = getInt(BundleFlag.ARIA_ID, 0)
                address = getString(BundleFlag.CUSTOMERS_ADDRESS, "")
                addressNote = getString(BundleFlag.ADDITIONAML_NOTE, "")
            }
        }

        val retrofit = RetrofitSingleton.getInstance(requireContext())
        placeOrderInterface = retrofit.create(PlaceOrderInterface::class.java)
        districtInterface = retrofit.create(DistrictInterface::class.java)
        getBreakableCharge()
        getPackagingCharge()
        getDeliveryCharge()
        getPickupLocation()


        deliveryTypeAdapter = DeliveryTypeAdapter(context!!, deliveryTypeList)
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

        collectionAmountET.addTextChangedListener(object : TextWatcher{
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

        toggleButtonGroupSize.addOnButtonCheckedListener { group1, checkedId1, isChecked1 ->
            if (isChecked1) {
                when (checkedId1) {
                    R.id.toggle_button_size_choto -> {
                        isCheckBigProduct = false
                        productType = "small"
                        calculateTotalPrice()
                    }
                    R.id.toggle_button_size_boro -> {
                        isCheckBigProduct = true
                        productType = "big"
                        calculateTotalPrice()
                    }
                }
            }
        }

        checkBoxBreakable.setOnCheckedChangeListener { compoundButton, b ->
            isBreakable = b
            calculateTotalPrice()
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

        backBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        submitBtn.setOnClickListener {
            submitOrder()
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


    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("নতুন অর্ডার")
    }

    private fun datePicker(dateTypeFlag: Int) {

        val calendar = Calendar.getInstance()
        var minDate = calendar.timeInMillis
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour24 = calendar.get(Calendar.HOUR_OF_DAY)

        if (logicExpression.isNotEmpty()) {
            if (dayAdvance.isNotEmpty()) {
                try {
                    val dayArray = dayAdvance.split(",")
                    isSameDay = dayArray[1].toInt() == 0

                    val flag = executeExpression("$hour24 $logicExpression")
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

        val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            if (dateTypeFlag == 1) {
                deliveryDate = "${month+1}/$dayOfMonth/$year"
                deliveryDatePicker.text = DigitConverter.toBanglaDigit(deliveryDate)
                collectionDatePicker.text = DigitConverter.toBanglaDigit(deliveryDate)

                //${dayOfMonth-1}
                if (alertMsg.isNotEmpty()) {
                    val day = if (isSameDay) "$dayOfMonth" else "${dayOfMonth-1}"
                    alertMsg = alertMsg.replace("dt-deliverydate", day, true)
                    alert("নির্দেশনা", alertMsg, false).show()
                }

            } else if (dateTypeFlag == 2) {
                collectionDate = "${month+1}/$dayOfMonth/$year"
                collectionDatePicker.text = DigitConverter.toBanglaDigit(collectionDate)
            }

        },year,month,day)
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

        placeOrderInterface.getBreakableCharge().enqueue(object : Callback<GenericResponse<BreakableChargeData>> {
            override fun onFailure(call: Call<GenericResponse<BreakableChargeData>>, t: Throwable) {

            }

            override fun onResponse(call: Call<GenericResponse<BreakableChargeData>>, response: Response<GenericResponse<BreakableChargeData>>) {
                if (response.isSuccessful && response.body() != null && isAdded) {
                    if (response.body()!!.model != null) {

                        val model = response.body()!!.model
                        breakableChargeApi = model.breakableCharge
                        codChargePercentage = model.codChargePercentage
                        codChargeMin = model.codChargeMin
                        bigProductCharge = model.bigProductCharge

                    }
                }
            }

        })

    }

    private fun getPackagingCharge() {

        placeOrderInterface.getPackagingCharge().enqueue(object : Callback<GenericResponse<List<PackagingData>>> {
            override fun onFailure(call: Call<GenericResponse<List<PackagingData>>>, t: Throwable) {}

            override fun onResponse(call: Call<GenericResponse<List<PackagingData>>>, response: Response<GenericResponse<List<PackagingData>>>) {
                if (response.isSuccessful && response.body() != null && isAdded) {

                    Timber.d("PackagingCharge ", " PackagingCharge_msg " + response.body()!!.model)

                    if (response.body()!!.model != null) {
                        val model = response.body()!!.model
                        packagingDataList.clear()
                        packagingDataList.addAll(model)

                        val packageNameList: MutableList<String> = mutableListOf()
                        packageNameList.add("প্যাকেজিং")
                        for (model1 in packagingDataList) {
                            packageNameList.add(model1.packagingName)
                        }

                        val packagingAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, packageNameList)
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
                    }
                }
            }
        })

    }

    private fun getDeliveryCharge() {

        placeOrderInterface.getDeliveryCharge(DeliveryChargeRequest(districtId, thanaId)).enqueue(object : Callback<GenericResponse<List<DeliveryChargeResponse>>> {
            override fun onFailure(call: Call<GenericResponse<List<DeliveryChargeResponse>>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<GenericResponse<List<DeliveryChargeResponse>>>,
                response: Response<GenericResponse<List<DeliveryChargeResponse>>>
            ) {
                if (response.isSuccessful && response.body() != null && isAdded) {

                    Timber.d("DeliveryCharge ", " DeliveryCharge_msg " + response.body()!!.model)

                    if (response.body()!!.model != null) {
                        val model = response.body()!!.model

                        val weightList: MutableList<String> = mutableListOf()
                        weightList.add("ওজন (কেজি)")
                        for (model1 in model) {
                            weightList.add(model1.weight)
                        }

                        val weightAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, weightList)
                        spinnerWeight.adapter = weightAdapter
                        spinnerWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                                if (p2 != 0) {

                                    val model2 = model[p2 - 1]
                                    weight = model2.weight
                                    deliveryTypeAdapter.clearSelectedItemPosition()
                                    deliveryTypeList.clear()
                                    deliveryTypeList.addAll(model2.weightRangeWiseData)
                                    deliveryTypeAdapter.notifyDataSetChanged()
                                    isWeightSelected = true
                                    //isOpenBoxCheck = model2.isOpenBox
                                    if (model2.isOpenBox) {
                                        checkOpenBox.visibility = View.VISIBLE
                                    } else {
                                        checkOpenBox.visibility = View.GONE
                                    }
                                } else {
                                    isWeightSelected = false
                                    deliveryTypeAdapter.clearSelectedItemPosition()
                                    deliveryTypeList.clear()
                                    deliveryTypeAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }

        })
    }

    private fun getPickupLocation() {

        placeOrderInterface.getPickupLocations(SessionManager.courierUserId).enqueue(object : Callback<GenericResponse<List<PickupLocation>>> {
            override fun onFailure(call: Call<GenericResponse<List<PickupLocation>>>, t: Throwable) {}
            override fun onResponse(call: Call<GenericResponse<List<PickupLocation>>>, response: Response<GenericResponse<List<PickupLocation>>>) {
                if (response.isSuccessful && response.body() != null && isAdded) {
                    if (response.body()!!.model != null) {
                        val pickupParentList = response.body()!!.model
                        if (pickupParentList.isNotEmpty()) {
                            setUpCollectionSpinner(pickupParentList, null, 1)
                        } else {
                            getDistrictThanaOrAria(14)
                        }
                    }
                }
            }
        })
    }

    private fun getDistrictThanaOrAria(districtId: Int) {

        districtInterface.getAllDistrictFromApi(districtId).enqueue(object : Callback<DeliveryChargePayLoad>{
            override fun onFailure(call: Call<DeliveryChargePayLoad>, t: Throwable) {}
            override fun onResponse(call: Call<DeliveryChargePayLoad>, response: Response<DeliveryChargePayLoad>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.data!!.districtInfo != null){
                    val thanaOrAriaList = response.body()!!.data!!.districtInfo!![0].thanaHome!!
                    setUpCollectionSpinner(null, thanaOrAriaList, 2)
                }
            }
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

        val pickupAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, pickupList)
        spinnerCollectionLocation.adapter = pickupAdapter
        spinnerCollectionLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    if (position != 0) {
                        if (optionFlag == 1) {
                            val model = pickupParentList!![position-1]
                            collectionAddress = model.pickupAddress ?: ""
                            collectionAddressET.setText(collectionAddress)
                            collectionDistrictId = model.districtId
                            collectionThanaId = model.thanaId
                        } else if (optionFlag == 2) {
                            val model = thanaOrAriaList!![position-1]
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
            payCODCharge = (payCollectionAmount/100.0) * codChargePercentage
            if (payCODCharge < codChargeMin){
                payCODCharge = codChargeMin.toDouble()
            }
            if (SessionManager.maxCodCharge != 0.0){
                if (payCODCharge > SessionManager.maxCodCharge){
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
        if (isCheckBigProduct){
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

        totalTV.text = "৳ ${DigitConverter.toBanglaDigit(total, true)}"

        Timber.d("BigProductCharge : ", "12 " + bigProductCharge)

    }

    private fun submitOrder() {

        if (!validateFormData()) {
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

        if(productType.equals("small")){
            payShipmentCharge
        } else {
            payShipmentCharge = payShipmentCharge + bigProductCharge
        }

        isOpenBoxCheck = checkOpenBox.isChecked

        val requestBody = OrderRequest(
            customerName,mobileNumber,altMobileNumber,address,districtId,thanaId,areaId,
            deliveryType,orderType,weight,collectionName,
            payCollectionAmount, payShipmentCharge,SessionManager.courierUserId,
            payBreakableCharge, addressNote, payCODCharge, payCollectionCharge, SessionManager.returnCharge,packingName,
            payPackagingCharge, collectionAddress, productType, deliveryRangeId, weightRangeId, isOpenBoxCheck,
            "android-${SessionManager.versionName}", true, collectionDistrictId,collectionThanaId,deliveryDate, collectionDate, isOfficeDrop)

        placeOrderInterface.placeOrder(requestBody).enqueue(object : Callback<GenericResponse<OrderResponse>> {
            override fun onFailure(call: Call<GenericResponse<OrderResponse>>, t: Throwable) {
                dialog?.hide()
                Timber.d(logTag, "${t.message}")
            }

            override fun onResponse(call: Call<GenericResponse<OrderResponse>>, response: Response<GenericResponse<OrderResponse>>) {
                dialog?.hide()
                if (response.isSuccessful && response.body() != null){

                    Timber.d("submitOrder ", " submitOrder_msg " + response.body())

                    if (response.body()!!.model != null){
                        Timber.d(logTag, "Order placed \n ${response.body()!!.model}")

                        addOrderSuccessFragment(response.body()!!.model)
                    }
                }
            }

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
        if (deliveryType.isEmpty()){
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

}
    //val fragment = OrderSuccessFragment.newInstance(null)
    private fun Context?.showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

