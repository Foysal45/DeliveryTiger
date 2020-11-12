package com.bd.deliverytiger.app.ui.quick_order

import android.app.AlertDialog
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
import androidx.core.os.bundleOf
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
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.databinding.FragmentQuickOrderBinding
import com.bd.deliverytiger.app.ui.add_order.*
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.profile.ProfileFragment
import com.bd.deliverytiger.app.utils.*
import timber.log.Timber

class QuickOrderFragment(): Fragment() {

    private var binding: FragmentQuickOrderBinding? = null
    private val viewModel: AddOrderViewModel by inject()

    private val districtList: ArrayList<DistrictDeliveryChargePayLoad> = ArrayList()
    private val deliveryTypeList: MutableList<WeightRangeWiseData> = mutableListOf()
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
    private var isAriaAvailable = true
    private var isMerchantCreditAvailable: Boolean = false
    private var isProfileComplete: Boolean = false

    // Step 2
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
    private var cityDeliveryCharge: Double = 0.0
    private var payCODCharge: Double = 0.0
    private var payBreakableCharge: Double = 0.0
    private var payCollectionCharge: Double = 0.0
    private var payPackagingCharge: Double = 0.0
    private var payActualPackagePrice: Double = 0.0
    private var isOpenBoxCheck: Boolean = false
    private var isOfficeDrop: Boolean = false
    private var isCollectionLocationSelected: Boolean = false
    private var collectionAmountLimit: Double = 0.0
    private var actualPackagePriceLimit: Double = 0.0

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
    private var collectionSlotDate: String = ""
    private var selectedCollectionSlotDate: String = ""
    private var timeSlotId: Int = 0

    companion object {
        fun newInstance(): QuickOrderFragment = QuickOrderFragment().apply {

        }
        val tag: String = QuickOrderFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("কুইক অর্ডার")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentQuickOrderBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        getPickupLocation()
        clickListens()
    }

    private fun initViews() {
        deliveryTypeAdapter = DeliveryTypeAdapter(requireContext(), deliveryTypeList)
        with(binding?.deliveryTypeSelectionRV!!) {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            layoutAnimation = null
            adapter = deliveryTypeAdapter
        }
        deliveryTypeAdapter.onItemClick = { position, model ->

            payShipmentCharge = model.chargeAmount
            //cityDeliveryCharge = model.cityDeliveryCharge

            deliveryType = "${model.deliveryType} ${model.days}"
            deliveryRangeId = model.deliveryRangeId
            weightRangeId = model.weightRangeId

            //alertMsg = model.deliveryAlertMessage ?: ""
            //logicExpression = model.loginHours ?: ""
            //dayAdvance = model.dateAdvance ?: ""

            calculateTotalPrice()

            if (model.deliveryType.contains("Postal", true)) {
                alert("নির্দেশনা", "এই জায়গায় কাস্টমারকে কালেকশন পয়েন্ট থেকে পার্সেল কালেক্ট করতে হবে। অনিবার্য কারণ বশত ডেলিভারি সময় সর্বোচ্চ ৭ দিন হতে পারে") {
                }.show()
            }
        }

        binding?.checkTermsConditionText?.text = HtmlCompat.fromHtml("আমি <font color='#00844A'>শর্তাবলী</font> মেনে নিলাম", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun clickListens() {

        binding?.etDistrict?.setOnClickListener {
            if (districtList.isEmpty()) {
                getDistrictThanaOrAria(0, 1)
            } else {
                goToDistrict()
            }
        }

        binding?.toggleButtonGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.toggle_button_1 -> {
                        binding?.collectionAmount?.visibility = View.GONE
                        isCollection = false
                        orderType = "Only Delivery"
                        calculateTotalPrice()
                    }
                    R.id.toggle_button_2 -> {
                        binding?.collectionAmount?.visibility = View.VISIBLE
                        binding?.collectionAmount?.requestFocus()
                        isCollection = true
                        orderType = "Delivery Taka Collection"
                        calculateTotalPrice()
                    }
                }
            }
        }

        binding?.checkTermsCondition?.setOnCheckedChangeListener { compoundButton, b ->
            isAgreeTerms = b
        }
        binding?.checkTermsConditionText?.setOnClickListener {

            val termsSheet = TermsConditionBottomSheet.newInstance()
            termsSheet.show(childFragmentManager, TermsConditionBottomSheet.tag)
            termsSheet.onTermsAgreed = {
                binding?.checkTermsCondition?.isChecked = it
            }
        }

        binding?.paymentDetails?.setOnClickListener {

            val bundle = Bundle()
            with(bundle) {
                putDouble("payShipmentCharge", payDeliveryCharge)
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

        binding?.collectionAmount?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                handler.removeCallbacks(runnable)
                runnable = Runnable {
                    calculateTotalPrice()
                    Timber.d( "$p0")
                }
                handler.postDelayed(runnable, 400L)
            }

        })

        binding?.orderPlaceBtn?.setOnClickListener {
            orderPlaceProcess()
        }
    }

    private fun orderPlaceProcess() {
        when {
            !isCollection && !isMerchantCreditAvailable -> showCreditLimitAlert()
            !isProfileComplete -> checkProfileData()
            else -> submitOrder()
        }
    }

    private fun showCreditLimitAlert() {
        alert("নির্দেশনা", "আপনার ক্রেডিট লিমিট শেষ হয়ে গিয়েছে। অনুগ্রহপূর্বক সাপোর্ট এর সাথে যোগাযোগ করুন।", false).show()
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
            } /*else if (track == 2) {
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
            }*/
        })
    }

    private fun getDistrictThanaOrAria(districtId: Int) {

        viewModel.getAllDistrictFromApi(districtId).observe(viewLifecycleOwner, Observer { list ->
            setUpCollectionSpinner(null, list.first().thanaHome, 2)
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

                binding?.etDistrict?.setText(name)
                districtId = clickedID
                thanaId = 0
                areaId = 0
                //etAriaPostOffice.setText("")
                //etThana.setText("")
                //etAriaPostOffice.visibility = View.GONE
                if (districtId == 14) {
                    getDeliveryCharge(districtId, 10026) // Fetch data if any district selected
                    codChargePercentage = codChargePercentageInsideDhaka
                } else {
                    getDeliveryCharge(1, 10137)
                    codChargePercentage = codChargePercentageOutsideDhaka
                }
                calculateTotalPrice()
            }
        })
    }

    private fun getDeliveryCharge(districtId: Int, thanaId: Int) {

        viewModel.getDeliveryCharge(DeliveryChargeRequest(districtId, thanaId)).observe(viewLifecycleOwner, Observer { list ->

            val model = list.first()
            weight = model.weight
            deliveryTypeAdapter.clearSelectedItemPosition()
            deliveryTypeList.clear()
            deliveryTypeList.addAll(model.weightRangeWiseData)
            deliveryTypeAdapter.notifyDataSetChanged()
            deliveryType = ""


        })

    }

    private fun getPickupLocation() {

        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                setUpCollectionSpinner(list, null, 1)
                //collectionAddressET.visibility = View.GONE
            } else {
                getDistrictThanaOrAria(14)
               //collectionAddressET.visibility = View.VISIBLE
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

        val pickupAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupList)
        binding?.spinnerCollectionLocation?.adapter = pickupAdapter
        binding?.spinnerCollectionLocation?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (position != 0) {
                    if (optionFlag == 1) {
                        val model = pickupParentList!![position - 1]
                        collectionAddress = model.pickupAddress ?: ""
                        //binding?.collectionAddressET.setText(collectionAddress)
                        collectionDistrictId = model.districtId
                        collectionThanaId = model.thanaId
                    } else if (optionFlag == 2) {
                        val model = thanaOrAriaList!![position - 1]
                        collectionAddress = ""
                        //collectionAddressET.setText(collectionAddress)
                        collectionDistrictId = 14
                        collectionThanaId = model.thanaId
                    }
                    isCollectionLocationSelected = true
                    calculateTotalPrice()
                } else {
                    isCollectionLocationSelected = false
                }
            }
        }
    }

    private fun calculateTotalPrice() {

        /*payDeliveryCharge = if (districtId == collectionDistrictId) {
            if (cityDeliveryCharge > 0.0) {
                cityDeliveryCharge
            } else {
                payShipmentCharge
            }
        } else {
            payShipmentCharge
        }*/
        payDeliveryCharge = payShipmentCharge


        // Total = Shipment + cod + breakable + collection + packaging
        if (isCollection) {
            val collectionAmount = binding?.collectionAmount?.text.toString()
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

        binding?.tvAddOrderTotalOrder?.text = "${DigitConverter.toBanglaDigit(total.toInt(), true)} ৳"

    }

    private fun checkProfileData(): Boolean {
        val model = SessionManager.getSessionData()
        var missingValues = "নতুন অর্ডার করার আগে প্রোফাইল-এ "
        var isMissing = false
        if (model.companyName.isNullOrEmpty()) {
            if(!isMissing) isMissing = true
            missingValues += "মার্চেন্ট/কোম্পানি নাম, "
        }
        if (model.mobile.isNullOrEmpty()) {
            if(!isMissing) isMissing = true
            missingValues += "মোবাইল নাম্বার, "
        }
        if (model.bkashNumber.isNullOrEmpty()) {
            if(!isMissing) isMissing = true
            missingValues += "বিকাশ নম্বর (পেমেন্ট গ্রহনের জন্য), "
        }
        if (model.address.isNullOrEmpty()) {
            if(!isMissing) isMissing = true
            missingValues += "বিস্তারিত কালেকশন ঠিকানা (বাড়ি/রোড/হোল্ডিং), "
        }
        if (model.emailAddress.isNullOrEmpty()) {
            if(!isMissing) isMissing = true
            missingValues += "ইমেইল "
        }
        missingValues += "যোগ করুন"
        if (isMissing) {
            alert("নির্দেশনা", missingValues, false) {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    addFragment(ProfileFragment.newInstance(), ProfileFragment.tag)
                }
            }.show()
            timber.log.Timber.d("missingValues: $missingValues")
        }

        return !isMissing
    }

    private fun validate(): Boolean {
        var go = true

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

    private fun submitOrder() {

        if (!validate()) {
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
            "android-${SessionManager.versionName}", true, collectionDistrictId, collectionThanaId,
            deliveryDate, collectionDate, isOfficeDrop,payActualPackagePrice, timeSlotId, selectedCollectionSlotDate
        )


        viewModel.placeOrder(requestBody).observe(viewLifecycleOwner, Observer { model ->
            dialog?.hide()
            addOrderSuccessFragment(model)
        })

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

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}