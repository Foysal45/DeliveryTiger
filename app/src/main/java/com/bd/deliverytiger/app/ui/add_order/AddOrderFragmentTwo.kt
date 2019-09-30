package com.bd.deliverytiger.app.ui.add_order


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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.PlaceOrderInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.charge.WeightRangeWiseData
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.button.MaterialButtonToggleGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class AddOrderFragmentTwo : Fragment() {

    private val logTag = "AddOrderFragmentLog"
    private lateinit var productNameET: EditText
    private lateinit var collectionAmountET: EditText
    private lateinit var spinnerWeight: AppCompatSpinner
    private lateinit var spinnerPackaging: AppCompatSpinner
    private lateinit var checkBoxBreakable: AppCompatCheckBox
    private lateinit var collectionAddressET: EditText
    private lateinit var checkTerms: AppCompatCheckBox
    private lateinit var deliveryTypeRV: RecyclerView
    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup
    private lateinit var submitBtn: LinearLayout
    private lateinit var backBtn: ConstraintLayout
    private lateinit var totalTV: TextView
    private lateinit var totalLayout: ConstraintLayout

    private lateinit var placeOrderInterface: PlaceOrderInterface
    private lateinit var deliveryTypeAdapter: DeliveryTypeAdapter
    private var handler: Handler = Handler()
    private var runnable: Runnable = Runnable {  }

    // API variable
    private val packagingDataList: MutableList<PackagingData> = mutableListOf()
    private val deliveryTypeList: MutableList<WeightRangeWiseData> = mutableListOf()

    private var codChargePercentage: Double = 0.0
    private var codChargeMin: Int = 0
    private var breakableChargeApi: Double = 0.0

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
    private var weight: String = ""
    private var collectionName: String = ""
    private var packingName: String = ""

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
        deliveryTypeRV = view.findViewById(R.id.delivery_type_selection_rV)
        toggleButtonGroup = view.findViewById(R.id.toggle_button_group)
        submitBtn = view.findViewById(R.id.submit_order)
        backBtn = view.findViewById(R.id.go_to_previous_page)
        totalTV = view.findViewById(R.id.tvAddOrderTotalOrder)
        totalLayout = view.findViewById(R.id.addOrderTopLay)


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

        placeOrderInterface = RetrofitSingleton.getInstance(context!!).create(PlaceOrderInterface::class.java)
        getBreakableCharge()
        getPackagingCharge()
        getDeliveryCharge()


        deliveryTypeAdapter = DeliveryTypeAdapter(context!!, deliveryTypeList)
        with(deliveryTypeRV) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            adapter = deliveryTypeAdapter
        }
        deliveryTypeAdapter.onItemClick = { position, model ->

            payShipmentCharge = model.chargeAmount
            deliveryType = "${model.deliveryType} ${model.days}"
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

        checkBoxBreakable.setOnCheckedChangeListener { compoundButton, b ->
            isBreakable = b
            calculateTotalPrice()
        }

        checkTerms.setOnCheckedChangeListener { compoundButton, b ->
            isAgreeTerms = b
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
            }

            val detailsSheet = DetailsBottomSheet.newInstance(bundle)
            detailsSheet.show(childFragmentManager, DetailsBottomSheet.tag)

        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("প্যাকেজ ও কালেকশনের তথ্য")
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
        } else {
            payCollectionAmount = 0.0
        }
        payCollectionCharge = SessionManager.collectionCharge
        val payReturnCharge = SessionManager.returnCharge
        payCODCharge = (payCollectionAmount/100.0) * codChargePercentage
        if (payCODCharge < codChargeMin){
            payCODCharge = codChargeMin.toDouble()
        }
        if (SessionManager.maxCodCharge != 0.0){
            if (payCODCharge > SessionManager.maxCodCharge){
                payCODCharge = SessionManager.maxCodCharge
            }
        }
        var total = payShipmentCharge + payCODCharge + payCollectionCharge + payPackagingCharge
        if (isBreakable) {
            total += breakableChargeApi
            payBreakableCharge = breakableChargeApi
        }

        totalTV.text = DigitConverter.toBanglaDigit("৳ $total", true)

    }

    private fun submitOrder() {

        if (!validateFormData()) {
            return
        }
        calculateTotalPrice()

        val requestBody = OrderRequest(
            customerName,mobileNumber,altMobileNumber,address,districtId,thanaId,areaId,
            deliveryType,orderType,weight,collectionName,
            payCollectionAmount, payShipmentCharge,SessionManager.courierUserId,
            payBreakableCharge, addressNote, payCODCharge, payCollectionCharge, SessionManager.returnCharge,packingName,
            payPackagingCharge, address, "android-${SessionManager.versionName}")

        placeOrderInterface.placeOrder(requestBody).enqueue(object : Callback<GenericResponse<OrderResponse>> {
            override fun onFailure(call: Call<GenericResponse<OrderResponse>>, t: Throwable) {

            }

            override fun onResponse(call: Call<GenericResponse<OrderResponse>>, response: Response<GenericResponse<OrderResponse>>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null){
                        Timber.d(logTag, "Order placed \n ${response.body()!!.model}")
                        context?.showToast("Oder ID: ${response.body()!!.model.courierOrdersId}")
                    }
                }
            }

        })


    }

    private fun validateFormData(): Boolean {

        collectionName = productNameET.text.toString()
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
            context?.showToast("প্যাকেজ এর ওজন সিলেক্ট করুন")
            return false
        }
        if (!isPackagingSelected) {
            context?.showToast("প্যাকেজিং সিলেক্ট করুন")
            return false
        }
        if (!isAgreeTerms) {
            context?.showToast("প্লিজ একসেপ্ট টার্মস এন্ড কন্ডিশন")
            return false
        }

        return true
    }

}

private fun Context?.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
