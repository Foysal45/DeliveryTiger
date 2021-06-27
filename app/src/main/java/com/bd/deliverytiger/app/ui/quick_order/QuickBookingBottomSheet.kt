package com.bd.deliverytiger.app.ui.quick_order

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderRequest
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.bd.deliverytiger.app.api.model.quick_order.TimeSlotRequest
import com.bd.deliverytiger.app.databinding.FragmentQuickBookingBinding
import com.bd.deliverytiger.app.ui.all_orders.order_edit.OrderInfoEditBottomSheet
import com.bd.deliverytiger.app.ui.quick_order.collection_location.CollectionLocationSelectionBottomSheet
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class QuickBookingBottomSheet  : BottomSheetDialogFragment() {

    private var binding: FragmentQuickBookingBinding? = null
    private var dataAdapter: QuickOrderTimeSlotAdapter = QuickOrderTimeSlotAdapter()
    private val viewModel: QuickOrderRequestViewModel by inject()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val sdf1 = SimpleDateFormat("dd MMM, yyyy", Locale.US)

    private var selectedDate = ""
    private var totalParcel = 1
    private var selectedTimeSLotID = 0
    private var selectedPickupLocationDistrictId = 0
    private var selectedPickupLocationThanaId = 0
    private var selectedPickupLocationThana: String = ""
    private var selectedTime: String = ""
    private var isTodaySelected: Boolean = false


    var onCollectionTypeSelected: ((isPickup: Boolean, pickupLocation: PickupLocation) -> Unit)? = null
    var onCollectionTimeSlotSelected: ((isPickup: Boolean, pickupLocation: QuickOrderTimeSlotData) -> Unit)? = null
    var onClose: ((type: Int) -> Unit)? = null

    companion object {

        fun newInstance(): QuickBookingBottomSheet = QuickBookingBottomSheet().apply {
        }

        val tag: String = OrderInfoEditBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentQuickBookingBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        fetchPickupLocation()
        initClickLister()
    }

    private fun initView(){
        binding?.recyclerViewTime?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = dataAdapter
            }
        }

        binding?.numberOfParcel?.setText("1")
        binding?.numberOfParcel?.clearFocus()
        binding?.numberOfParcel?.transformationMethod = null

        val calender = Calendar.getInstance()
        val todayDate = calender.timeInMillis
        selectedDate = sdf.format(todayDate)
        isTodaySelected = true
        fetchCollectionTimeSlot()
    }

    private fun initClickLister() {

        binding?.submitBtn?.setOnClickListener {
            updateOrder()
        }

        binding?.pickupLocationLayout?.setOnClickListener {
            showCollectionLocationSelectionBottomSheet()
        }

        binding?.parcelCountIncrease?.setOnClickListener {
            totalParcel+=1
            binding?.numberOfParcel?.setText(totalParcel.toString())
        }

        binding?.parcelCountDecrease?.setOnClickListener {
            if (totalParcel > 1){
                totalParcel-=1
            }
            binding?.numberOfParcel?.setText(totalParcel.toString())
        }

        binding?.numberOfParcel?.doAfterTextChanged { text ->
            val string = text.toString()
            totalParcel = string.toIntOrNull() ?: 1
            binding?.numberOfParcel?.setSelection(string.length)
        }

        binding?.collectionToday?.setOnClickListener {
            binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
            binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            val calender = Calendar.getInstance()
            val todayDate = calender.timeInMillis
            selectedDate = sdf.format(todayDate)
            Timber.d("selectedDate $selectedDate")
            isTodaySelected = true
            fetchCollectionTimeSlot()
        }

        binding?.collectionTomorrow?.setOnClickListener {
            binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
            binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            val calender = Calendar.getInstance()
            calender.add(Calendar.DAY_OF_MONTH, 1)
            val tomorrowDate = calender.timeInMillis
            selectedDate = sdf.format(tomorrowDate)
            Timber.d("selectedDate $selectedDate")
            isTodaySelected = false
            fetchCollectionTimeSlot()
        }

        dataAdapter.onItemClick = { model, position  ->
            selectedTimeSLotID = model.collectionTimeSlotId

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

                        val msg = "এই টাইম স্লটে পরবর্তী ${DigitConverter.toBanglaDigit(minute.toString())} মিনিট এর মধ্যে কালেক্টর আসতে পারবেন না। অনুগ্রহ করে পরবর্তী টাইম স্লট সিলেক্ট করে অর্ডার করুন।"
                        alert("নির্দেশনা", msg) {
                            if (it == AlertDialog.BUTTON_POSITIVE) {
                                selectedTimeSLotID = 0
                                dataAdapter.setSelectedPositions(-1)
                            }
                        }.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        }

        binding?.orderRequestDatePicker?.setOnClickListener {
            datePicker()
        }

        binding?.numberOfParcel?.setOnClickListener {
            binding?.numberOfParcel?.requestFocus()
            binding?.numberOfParcel?.requestFocusFromTouch()
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })

        binding?.parent?.setOnClickListener {
            hideKeyboard()
        }

    }

    private fun showCollectionLocationSelectionBottomSheet() {
        val tag: String = CollectionLocationSelectionBottomSheet.tag
        val dialog: CollectionLocationSelectionBottomSheet = CollectionLocationSelectionBottomSheet.newInstance()
        dialog.show(childFragmentManager, tag)
        dialog.onCollectionTypeSelected = { isPickup, pickupLocation ->
            if (isPickup){
                binding?.picUpUpLocation?.text = pickupLocation.thanaName
                selectedPickupLocationDistrictId = pickupLocation.districtId
                selectedPickupLocationThanaId = pickupLocation.thanaId
                selectedPickupLocationThana = pickupLocation.thanaName ?: ""
                dialog.dismiss()
            }
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
        binding?.submitBtn?.isEnabled = false
        viewModel.quickOrderRequest(requestBody).observe(viewLifecycleOwner, Observer {
            dialog?.dismiss()
            binding?.submitBtn?.isEnabled = true
            alert("নির্দেশনা", "পার্সেল বুকিং গ্রহণ করা হয়েছে। $selectedPickupLocationThana থেকে পার্সেল কালেক্ট করা হবে।", false, "ঠিক আছে", "ক্যানসেল"){
            }.show()
        })
    }

    private fun fetchPickupLocation(){
        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            val model = list.first()
            binding?.picUpUpLocation?.text = model.thanaName
            selectedPickupLocationDistrictId = model.districtId
            selectedPickupLocationThanaId = model.thanaId
            selectedPickupLocationThana = model.thanaName ?: ""
        })
    }

    private fun fetchCollectionTimeSlot() {
        if (isTodaySelected) {
            viewModel.currentTimeSlot.observe(viewLifecycleOwner, Observer { list ->
                Timber.d("timeSlotDebug current time slot")
                dataAdapter.initLoad(list)
            })
        } else {
            viewModel.upcomingTimeSlot.observe(viewLifecycleOwner, Observer { list ->
                Timber.d("timeSlotDebug upcoming time slot")
                dataAdapter.initLoad(list)
            })
        }
    }

    private fun validate(): Boolean {

        totalParcel = binding?.numberOfParcel?.text.toString().toInt()

        if (totalParcel == 0){
            context?.toast("Please Insert Number of parcel")
            return false
        }
        if (selectedDate.isEmpty()){
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
            binding?.collectionTomorrow?.text = selectedDate
            binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
            binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            isTodaySelected = false
            fetchCollectionTimeSlot()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parent?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                skipCollapsed = true
                isHideable = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onClose?.invoke(0)
    }

}