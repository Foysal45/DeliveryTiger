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
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.databinding.FragmentOrderInfoEditBottomSheetBinding
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
import com.bd.deliverytiger.app.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread


class OrderInfoEditBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentOrderInfoEditBottomSheetBinding? = null
    private val viewModel: OrderInfoEditViewModel by inject()
    private var model: CourierOrderViewModel = CourierOrderViewModel()

    var onCollectionTypeSelected: ((isPickup: Boolean, pickupLocation: PickupLocation) -> Unit)? = null

    companion object {

        fun newInstance(model:  CourierOrderViewModel): OrderInfoEditBottomSheet = OrderInfoEditBottomSheet().apply {
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
        fetchPickupLocation()
        initClickLister()
    }

    private fun initData(){
        binding?.etCustomerName?.setText(model.customerName)
        binding?.etOrderMobileNo?.setText(model.courierAddressContactInfo?.mobile)
        binding?.etAlternativeMobileNo?.setText(model.courierAddressContactInfo?.otherMobile)
        binding?.etOrderProductName?.setText(model.courierOrderInfo?.collectionName)
        binding?.etCustomersAddress?.setText(model.courierAddressContactInfo?.address)

        if (model.courierOrderInfo?.orderType == "Delivery Taka Collection"){
            binding?.collectionAmount?.visibility = View.VISIBLE
            binding?.collectionAmount?.setText(model.courierPrice?.collectionAmount.toString())
        }else{
            binding?.collectionAmount?.visibility = View.GONE
        }
        if (model.courierPrice?.officeDrop == true){
            binding?.toggleButtonPickupGroup?.selectButton(R.id.toggleButtonPickup1)
        }else{
            binding?.toggleButtonPickupGroup?.selectButton(R.id.toggleButtonPickup2)
            binding?.msg?.isVisible = false
            binding?.pickupAddressLayout?.visibility = View.VISIBLE
            binding?.chargeMsgLayout?.visibility = View.GONE
        }
    }

    private fun fetchPickupLocation(){
        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            spinnerDataBinding(list)
        })
    }

    private fun spinnerDataBinding(list: List<PickupLocation>){
        val pickupList: MutableList<String> = mutableListOf()
        pickupList.add("পিক আপ লোকেশন")
        list.forEach {
            pickupList.add(it.thanaName ?: "")
        }
        val pickupAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupList)
        binding?.spinnerCollectionLocation?.adapter = pickupAdapter
        binding?.spinnerCollectionLocation?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val model = list[position-1]
                    onCollectionTypeSelected?.invoke(true, model)
                }
            }
        }
    }

    private fun initClickLister() {

        binding?.toggleButtonPickupGroup?.setOnSelectListener { button ->
            when (button.id) {
                R.id.toggleButtonPickup1 -> {
                    binding?.pickupAddressLayout?.visibility = View.GONE
                    binding?.chargeMsgLayout?.visibility = View.GONE
                }
                R.id.toggleButtonPickup2 -> {
                    if (model.courierOrderInfo?.weightRangeId ?: 0 > 6) {
                        binding?.msg?.isVisible = true
                        binding?.msg?.text = "পার্সেলের ওজন ৫ কেজির উপরে হলে কালেকশন হাবে ড্রপ করতে হবে, হাব ড্রপ সিলেক্ট করুন"
                    } else {
                        binding?.msg?.isVisible = false
                        binding?.pickupAddressLayout?.visibility = View.VISIBLE
                        binding?.chargeMsgLayout?.visibility = View.VISIBLE
                    }
                }
            }
        }

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