package com.bd.deliverytiger.app.ui.all_orders.details_bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.databinding.FragmentAllOrderDetailsDialogueBinding
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class AllOrdersDetailsDialog : BottomSheetDialogFragment() {

    private var binding: FragmentAllOrderDetailsDialogueBinding? = null
    private var dataList: MutableList<CourierOrderViewModel> = mutableListOf()
    private var index: Int = 0

    companion object {

        fun newInstance(data: MutableList<CourierOrderViewModel>, position: Int): AllOrdersDetailsDialog = AllOrdersDetailsDialog().apply {
            dataList = data
            index = position
        }
        val tag: String = AllOrdersDetailsDialog::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentAllOrderDetailsDialogueBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOrderData()
        initCustomerData()
        initServiceChargeData()

    }

    @SuppressLint("SetTextI18n")
    private fun initOrderData(){
        val model = dataList[index]

        binding?.orderId?.text = model.courierOrdersId.toString()
        val formattedDate = DigitConverter.toBanglaDate(model.courierOrderDateDetails?.orderDate, "MM-dd-yyyy HH:mm:ss")
        binding?.date?.text = formattedDate
        binding?.reference?.text = model.courierOrderInfo?.collectionName
        binding?.paymentType?.text = model.courierOrderInfo?.paymentType
        binding?.orderType?.text = model.courierOrderInfo?.orderType
        binding?.status?.text = model.status

    }

    private fun initCustomerData(){
        val model = dataList[index]

        binding?.customerName?.text = model.customerName
        var mobile = "${model.courierAddressContactInfo?.mobile}"
        if (model.courierAddressContactInfo?.otherMobile?.isEmpty() == false) {
            mobile += ",${model.courierAddressContactInfo?.otherMobile}"
        }
        binding?.customerPhone?.text = mobile
        binding?.customerAddress?.text = model.courierAddressContactInfo?.address
        binding?.customerThana?.text = model.courierAddressContactInfo?.thanaName
        binding?.customerDistrict?.text = model.courierAddressContactInfo?.districtName

        if (!model.courierAddressContactInfo?.areaName.isNullOrEmpty()){
            binding?.customerArea?.text = model.courierAddressContactInfo?.areaName
        }else{
            binding?.customerArea?.visibility = View.GONE
            binding?.key03?.visibility = View.GONE
        }

    }
    @SuppressLint("SetTextI18n")
    private fun initServiceChargeData(){
        val model = dataList[index]
        binding?.shipmentTV?.text = "${DigitConverter.toBanglaDigit(model.courierPrice?.deliveryCharge, true)} ৳"

        val codChargePercentage = if (model.courierAddressContactInfo?.districtId == 14) {
            SessionManager.codChargePercentageInsideDhaka
        } else {
            SessionManager.codChargePercentageOutsideDhaka
        }
        val codMsg = "COD চার্জঃ (সর্বনিম্ন ${DigitConverter.toBanglaDigit(SessionManager.codChargeMin)}৳) (${DigitConverter.toBanglaDigit(codChargePercentage)}% × ${DigitConverter.toBanglaDigit(model.courierPrice?.collectionAmount, true)} ৳)"
        binding?.key003?.text = codMsg
        binding?.codChargeTV?.text = "${DigitConverter.toBanglaDigit(model.courierPrice?.codCharge, true)} ৳"
        binding?.breakableChargeTV?.text = "${DigitConverter.toBanglaDigit(model.courierPrice?.breakableCharge, true)} ৳"
        binding?.collectionChargeTV?.text = "${DigitConverter.toBanglaDigit(model.courierPrice?.collectionCharge, true)} ৳"
        binding?.packagingChargeTV?.text = "${DigitConverter.toBanglaDigit(model.courierPrice?.packagingCharge, true)} ৳"
        binding?.totalTV?.text = "${DigitConverter.toBanglaDigit(model.courierPrice?.totalServiceCharge, true)} ৳"

    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
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
            BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                    BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                }
                override fun onStateChanged(p0: View, p1: Int) {
                    /*if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                    }*/
                }
            })
        }
    }

    private fun addOrderTrackFragment(orderId: String) {
        dialog?.dismiss()
        val fragment = OrderTrackingFragment.newInstance(orderId)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, OrderTrackingFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
