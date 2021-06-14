package com.bd.deliverytiger.app.ui.quick_order

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderRequest
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.bd.deliverytiger.app.databinding.FragmentQuickOrderRequestBottomSheetBinding
import com.bd.deliverytiger.app.ui.all_orders.order_edit.OrderInfoEditBottomSheet
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class QuickOrderRequestBottomSheet  : BottomSheetDialogFragment() {

    private var binding: FragmentQuickOrderRequestBottomSheetBinding? = null
    private val viewModel: QuickOrderRequestViewModel by inject()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val sdf1 = SimpleDateFormat("dd MMM, yyyy", Locale.US)

    private var selectedDate = ""
    private var selectedTimeSLotID= 0
    private var selectedPickupLocationDistrictId = 0
    private var selectedPickupLocationThanaId = 0

    var onCollectionTypeSelected: ((isPickup: Boolean, pickupLocation: PickupLocation) -> Unit)? = null
    var onCollectionTimeSlotSelected: ((isPickup: Boolean, pickupLocation: QuickOrderTimeSlotData) -> Unit)? = null

    companion object {

        fun newInstance(): QuickOrderRequestBottomSheet = QuickOrderRequestBottomSheet().apply {
        }

        val tag: String = OrderInfoEditBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentQuickOrderRequestBottomSheetBinding.inflate(inflater, container, false).also {
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


    }

    private fun initClickLister() {

        binding?.btnSubmit?.setOnClickListener {
            updateOrder()
        }

        binding?.orderRequestDatePicker?.setOnClickListener {
            datePicker()
        }

    }

    private fun updateOrder(){
        if (!validate()){
            return
        }
        val parcelCount = binding?.numberOfParcel?.text.toString()
        val parcelCountNumber = parcelCount.toIntOrNull() ?: 1
        val requestBody = QuickOrderRequest(
            SessionManager.courierUserId,
            parcelCountNumber,
            selectedPickupLocationDistrictId,
            selectedPickupLocationThanaId,
            selectedDate,
            selectedTimeSLotID
        )
        viewModel.quickOrderRequest(requestBody).observe(viewLifecycleOwner, Observer {
            context?.toast("Request Accepted")
            dialog?.dismiss()
        })
    }

    private fun fetchPickupLocation(){
        viewModel.getCollectionTimeSlot().observe(viewLifecycleOwner, Observer { list ->
            spinnerTimeSlotDataBinding(list)
        })
        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            spinnerLocationDataBinding(list)
        })
    }

    private fun spinnerLocationDataBinding(list: List<PickupLocation>){
        val pickupList: MutableList<String> = mutableListOf()
        pickupList.add("পিক আপ লোকেশন")

        list.forEach {
            pickupList.add(it.thanaName ?: "")
        }
        val pickupAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupList)
        binding?.spinnerPickupLocation?.adapter = pickupAdapter
        binding?.spinnerPickupLocation?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val model = list[position-1]
                    selectedPickupLocationDistrictId = model.districtId
                    selectedPickupLocationThanaId = model.thanaId
                    onCollectionTypeSelected?.invoke(true, model)
                }
            }
        }
    }

    private fun spinnerTimeSlotDataBinding(list: List<QuickOrderTimeSlotData>){
        val timeSlotList: MutableList<String> = mutableListOf()

        timeSlotList.add("টাইম রেঞ্জ সিলেক্ট করুন")

        list.forEach {
            timeSlotList.add("${DigitConverter.toBanglaDigit(it.startTime)}-${DigitConverter.toBanglaDigit(it.endTime)}")
        }
        val pickupAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, timeSlotList)
        binding?.spinnerTimeSlot?.adapter = pickupAdapter
        binding?.spinnerTimeSlot?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val model = list[position-1]
                    selectedTimeSLotID = model.collectionTimeSlotId
                    onCollectionTimeSlotSelected?.invoke(true, model)
                }
            }
        }
    }

    private fun validate(): Boolean {

        val numberOfParcel = binding?.numberOfParcel?.text.toString()

        if (numberOfParcel.isNullOrEmpty()){
            context?.toast("Please Insert Number of parcel")
            return false
        }
        if (selectedDate.isNullOrEmpty()){
            context?.toast("Select Date")
            return false
        }
        if (selectedTimeSLotID == 0){
            context?.toast("Select Time Slot")
            return false
        }
        if (selectedPickupLocationDistrictId == 0){
            context?.toast("Select PickUp Location")
            return false
        }
        if (selectedPickupLocationThanaId == 0){
            context?.toast("Select PickUp Location")
            return false
        }

        return true
    }

    private fun datePicker() {

        var calender = Calendar.getInstance()
        val calendarConstraints = CalendarConstraints.Builder().apply {
            calender.add(Calendar.DAY_OF_MONTH, -1)
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

        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {
            selectedDate = sdf.format(it)
            setDatePickerTitle()
        }
    }

    private fun setDatePickerTitle(){
        binding?.orderRequestDatePicker?.text = selectedDate
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