package com.bd.deliverytiger.app.ui.quick_order

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderRequest
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.bd.deliverytiger.app.databinding.FragmentQuickOrderRequestBottomSheetBinding
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
import kotlin.concurrent.thread

class QuickOrderRequestBottomSheet  : BottomSheetDialogFragment() {

    private var binding: FragmentQuickOrderRequestBottomSheetBinding? = null
    private var dataAdapter: QuickOrderTimeSlotAdapter = QuickOrderTimeSlotAdapter()
    private val viewModel: QuickOrderRequestViewModel by inject()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val sdf1 = SimpleDateFormat("dd MMM, yyyy", Locale.US)

    private var selectedDate = ""
    private var totalParcel = 1
    private var selectedTimeSLotID = 0
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

        binding?.collectionToday?.setOnClickListener {
            binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
            binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            val calender = Calendar.getInstance()
            calender.add(Calendar.DAY_OF_MONTH, 0)
            val todayDate = calender.timeInMillis
            selectedDate = sdf.format(todayDate)
            Timber.d("selectedDate $selectedDate")
        }

        binding?.collectionTomorrow?.setOnClickListener {
            binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
            binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            val calender = Calendar.getInstance()
            calender.add(Calendar.DAY_OF_MONTH, 1)
            val tomorrowDate = calender.timeInMillis
            selectedDate = sdf.format(tomorrowDate)
            Timber.d("selectedDate $selectedDate")
        }

        dataAdapter.onItemClick = { model, position  ->

            selectedTimeSLotID = model.collectionTimeSlotId
            dataAdapter.setSelectedPositions(position)

        }

        binding?.orderRequestDatePicker?.setOnClickListener {
            datePicker()
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
        viewModel.quickOrderRequest(requestBody).observe(viewLifecycleOwner, Observer {
            alert("নির্দেশনা", "আপনার কুইক বুকিং এর রিকোয়েস্ট টি গ্রহণ করা হয়েছে ।", false, "ঠিক আছে", "ক্যানসেল"){

            }.show()
            dialog?.dismiss()
        })
    }

    private fun fetchPickupLocation(){
        viewModel.getCollectionTimeSlot().observe(viewLifecycleOwner, Observer { list ->
            dataAdapter.initLoad(list)
            Timber.d("debugData fetch-> $list")
        })
        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            binding?.picUpUpLocation?.text = list.first().thanaName
            selectedPickupLocationDistrictId = list.first().districtId
            selectedPickupLocationThanaId = list.first().thanaId
        })
    }


    private fun validate(): Boolean {

        totalParcel = binding?.numberOfParcel?.text.toString().toInt()

        if (totalParcel == 0){
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
        binding?.collectionTomorrow?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
        binding?.collectionToday?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)

        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {
            selectedDate = sdf.format(it)
            Timber.d("selectedDate $selectedDate")
        }
    }


    override fun onStart() {
        super.onStart()
        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(false)
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

}