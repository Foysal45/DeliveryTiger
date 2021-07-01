package com.bd.deliverytiger.app.ui.all_orders.order_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.databinding.FragmentOrderInfoEditBottomSheetBinding
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationType
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread


class OrderInfoEditBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentOrderInfoEditBottomSheetBinding? = null
    private val viewModel: OrderInfoEditViewModel by inject()

    private var model: CourierOrderViewModel = CourierOrderViewModel()

    private var codChargePercentage: Double = 0.0
    private var codChargePercentageInsideDhaka: Double = 0.0
    private var codChargePercentageOutsideDhaka: Double = 0.0
    private var codChargeMin: Int = 0

    private var collectionChargeApi: Double = 0.0
    private var merchantDistrict: Int = 0

    private var collectionDistrictId: Int = 0
    private var collectionThanaId: Int = 0
    private var collectionAddress: String = ""

    private val updateOrderReqBody = UpdateOrderReqBody()
    private var officeDropSelected: Boolean = false

    var onUpdate: ((orderId: String, requestBody: UpdateOrderReqBody) -> Unit)? = null

    companion object {

        fun newInstance(model: CourierOrderViewModel): OrderInfoEditBottomSheet = OrderInfoEditBottomSheet().apply {
            this.model = model
        }

        val tag: String = OrderInfoEditBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentOrderInfoEditBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        getBreakableCharge()
        getCourierUsersInformation()
        fetchPickupLocation()
        initClickLister()
    }

    private fun initData() {
        binding?.etCustomerName?.setText(model.customerName)
        binding?.etOrderMobileNo?.setText(model.courierAddressContactInfo?.mobile)
        binding?.etAlternativeMobileNo?.setText(model.courierAddressContactInfo?.otherMobile)
        binding?.etCustomersAddress?.setText(model.courierAddressContactInfo?.address)
        binding?.etOrderProductName?.setText(model.courierOrderInfo?.collectionName)

        if (model.courierOrderInfo?.orderType == "Delivery Taka Collection") {
            binding?.collectionAmount?.visibility = View.VISIBLE
            val collectionAmount = model.courierPrice?.collectionAmount.toString()
            binding?.collectionAmount?.setText(collectionAmount)
        } else {
            binding?.collectionAmount?.visibility = View.GONE
        }

        if (model.courierPrice?.officeDrop == true) {
            officeDropSelected = true
            binding?.toggleButtonPickupGroup?.selectButton(R.id.toggleButtonPickup1)
            binding?.toggleButtonPickupGroup?.isVisible = false
        } else {
            officeDropSelected = false
            binding?.toggleButtonPickupGroup?.selectButton(R.id.toggleButtonPickup2)
            binding?.msg?.isVisible = false
            binding?.pickupAddressLayout?.visibility = View.VISIBLE
            binding?.chargeMsgLayout?.visibility = View.GONE

        }
    }

    private fun fetchPickupLocation() {
        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            spinnerDataBinding(list)
        })
    }

    private fun getBreakableCharge() {

        viewModel.getBreakableCharge().observe(viewLifecycleOwner, Observer { model ->
            codChargeMin = model.codChargeMin
            codChargePercentageInsideDhaka = model.codChargeDhakaPercentage
            codChargePercentageOutsideDhaka = model.codChargePercentage

            codChargePercentage = if (this.model.courierAddressContactInfo?.districtId == 14) {
                codChargePercentageInsideDhaka
            } else {
                codChargePercentageOutsideDhaka
            }
        })

    }

    private fun getCourierUsersInformation() {
        viewModel.getCourierUsersInformation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            collectionChargeApi = model.collectionCharge
            merchantDistrict = model.districtId
        })
    }

    private fun spinnerDataBinding(list: List<PickupLocation>) {

        val pickupList: MutableList<String> = mutableListOf()
        list.forEach {
            pickupList.add(it.thanaName ?: "")
        }

        val pickupAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupList)
        binding?.spinnerCollectionLocation?.adapter = pickupAdapter
        binding?.spinnerCollectionLocation?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val model = list[position]
                collectionDistrictId = model.districtId
                collectionThanaId = model.thanaId
                collectionAddress = model.pickupAddress ?: ""
            }
        }

        val selectedIndex = list.indexOfFirst { it.districtId == model.courierAddressContactInfo?.collectAddressDistrictId && it.thanaId == model.courierAddressContactInfo?.collectAddressThanaId }
        if (selectedIndex != -1) {
            binding?.spinnerCollectionLocation?.setSelection(selectedIndex)
        }

    }

    private fun initClickLister() {

        binding?.btUpdate?.setOnClickListener {
            updateOrder()
        }

        binding?.toggleButtonPickupGroup?.setOnSelectListener { button ->
            when (button.id) {
                R.id.toggleButtonPickup1 -> {
                    binding?.pickupAddressLayout?.visibility = View.GONE
                    binding?.chargeMsgLayout?.visibility = View.GONE
                    officeDropSelected = true
                }
                R.id.toggleButtonPickup2 -> {
                    if ((model.courierOrderInfo?.weightRangeId ?: 0) > 6) {
                        binding?.msg?.isVisible = true
                        binding?.msg?.text = "পার্সেলের ওজন ৫ কেজির উপরে হলে কালেকশন হাবে ড্রপ করতে হবে, হাব ড্রপ সিলেক্ট করুন"
                    } else {
                        binding?.msg?.isVisible = false
                        binding?.pickupAddressLayout?.visibility = View.VISIBLE
                        binding?.chargeMsgLayout?.visibility = View.GONE
                        officeDropSelected = false
                    }
                }
            }
        }

    }

    private fun updateOrder() {
        if (!validate()) {
            return
        }

        onUpdate?.invoke(model.courierOrdersId ?: "", updateOrderReqBody)

        //context?.toast("সফলভাবে আপডেট হয়েছে")
        /*viewModel.updateOrderInfo(model.courierOrdersId ?: "", updateOrderReqBody).observe(viewLifecycleOwner, Observer { model ->
            if (model != null) {
                context?.toast(getString(R.string.update_success))
            } else {
                context?.toast(getString(R.string.error_msg))
            }
        })*/
    }

    private fun validate(): Boolean {

        hideKeyboard()

        val name = binding?.etCustomerName?.text.toString().trim()
        val mobile = binding?.etOrderMobileNo?.text.toString().trim()
        val alternateMobile = binding?.etAlternativeMobileNo?.text.toString().trim()
        val address = binding?.etCustomersAddress?.text.toString().trim()
        val referenceInvoice = binding?.etOrderProductName?.text.toString().trim()
        val collectionAmount = binding?.collectionAmount?.text.toString().trim()

        if (name.isEmpty()) {
            context?.showToast(getString(R.string.write_yr_name))
            return false
        }
        if (mobile.isEmpty()) {
            context?.showToast(getString(R.string.write_phone_number))
            return false
        }
        if (!Validator.isValidMobileNumber(mobile) || mobile.length < 11) {
            context?.toast(getString(R.string.write_proper_phone_number_recharge))
            return false
        }
        if (alternateMobile.isNotEmpty()) {
            if (!Validator.isValidMobileNumber(alternateMobile) || alternateMobile.length < 11) {
                context?.toast(getString(R.string.write_proper_phone_number_recharge))
                return false
            }
        }
        if (address.isEmpty()) {
            context?.showToast(getString(R.string.write_yr_address))
            return false
        }
        if (referenceInvoice.isEmpty()) {
            context?.showToast("নিজস্ব রেফারেন্স নম্বর/ইনভয়েস লিখুন")
            return false
        }

        var payCODCharge = 0.0
        if (model.courierOrderInfo?.orderType == "Delivery Taka Collection") {
            if (collectionAmount.isEmpty()) {
                context?.showToast("কালেকশন অ্যামাউন্ট লিখুন")
                return false
            }

            val serviceChange = model.courierPrice?.totalServiceCharge ?: 0.0
            val payCollectionAmount = collectionAmount.toDoubleOrNull() ?: 0.0
            if (payCollectionAmount < serviceChange) {
                context?.showToast("কালেকশন অ্যামাউন্ট সার্ভিস চার্জ (${DigitConverter.toBanglaDigit(serviceChange, true)}) থেকে বেশি হতে হবে")
                return false
            }
            payCODCharge = (payCollectionAmount / 100.0) * codChargePercentage
            if (payCODCharge < codChargeMin) {
                payCODCharge = codChargeMin.toDouble()
            }

        }

        val payCollectionCharge = if (officeDropSelected) {
            0.0
        } else {
            collectionChargeApi
        }



        updateOrderReqBody.apply {
            this.customerName = name
            this.mobile = mobile
            this.otherMobile = alternateMobile
            this.address = address
            this.collectionName = referenceInvoice
            this.collectionAmount = collectionAmount.toDoubleOrNull() ?: 0.0
            this.codCharge = payCODCharge
            this.officeDrop = officeDropSelected
            this.collectionCharge = payCollectionCharge
        }

        return true
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(false)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        val metrics = resources.displayMetrics
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    BottomSheetBehavior.from(bottomSheet).peekHeight = metrics.heightPixels
                }
            }
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = false

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}