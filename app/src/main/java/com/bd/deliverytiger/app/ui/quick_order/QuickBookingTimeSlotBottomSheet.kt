package com.bd.deliverytiger.app.ui.quick_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentQuickBookingBinding
import com.bd.deliverytiger.app.databinding.FragmentQuickBookingTimeSlotBinding
import com.bd.deliverytiger.app.ui.all_orders.order_edit.OrderInfoEditBottomSheet
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.bd.deliverytiger.app.api.model.quick_order.TimeSlotUpdateRequest
import com.bd.deliverytiger.app.api.model.quick_order.quick_order_history.QuickOrderListRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import timber.log.Timber
import kotlin.concurrent.thread

class QuickBookingTimeSlotBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentQuickBookingTimeSlotBinding? = null
    private val viewModel: QuickOrderRequestViewModel by inject()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var dataAdapter: QuickOrderTimeSlotAdapter = QuickOrderTimeSlotAdapter()

    private var orderRequestId: Int = 0
    private var preSelectedSlotId: Int = 0
    private var collectionDate: String = ""

    private var selectedDate = ""
    private var selectedTimeSLotID = 0
    private var selectedTimeSLot: String = ""
    private var isTodaySelected: Boolean = false

    var onUpdate: ((msg: String) -> Unit)? = null

    companion object {

        fun newInstance(orderRequestId: Int, preSelectedSlotId: Int, collectionDate: String): QuickBookingTimeSlotBottomSheet = QuickBookingTimeSlotBottomSheet().apply {
            this.orderRequestId = orderRequestId
            this.preSelectedSlotId = preSelectedSlotId
            this.collectionDate = collectionDate
        }

        val tag: String = QuickBookingTimeSlotBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentQuickBookingTimeSlotBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
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
        selectedTimeSLotID = preSelectedSlotId
        selectedDate = DigitConverter.formatDate(collectionDate, "yyyy-MM-dd", "yyyy-MM-dd")
        val dateOfMonth1 = DigitConverter.formatDate(collectionDate, "yyyy-MM-dd", "dd").toIntOrNull() ?: 0
        val calender = Calendar.getInstance()
        val dateOfMonth = calender.get(Calendar.DAY_OF_MONTH)
        isTodaySelected = dateOfMonth == dateOfMonth1
        fetchCollectionTimeSlot()
    }

    private fun initClickLister() {

        binding?.submitBtn?.setOnClickListener {
            updateTimeSlot()
        }

        dataAdapter.onItemClick = { model, position  ->
            selectedTimeSLotID = model.collectionTimeSlotId
            selectedTimeSLot = DigitConverter.formatTimeRange(model.startTime, model.endTime)

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
                        alert(getString(R.string.instruction), msg, false, getString(R.string.ok), getString(R.string.cancel)) {
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
    }

    private fun fetchCollectionTimeSlot() {
        if (isTodaySelected) {
            viewModel.currentTimeSlot.observe(viewLifecycleOwner, Observer { list ->
                Timber.d("timeSlotDebug current time slot")
                initTimeSlot(list)
            })
        } else {
            viewModel.upcomingTimeSlot.observe(viewLifecycleOwner, Observer { list ->
                Timber.d("timeSlotDebug upcoming time slot")
                initTimeSlot(list)
            })
        }
    }

    private fun initTimeSlot(list: List<QuickOrderTimeSlotData>) {
        dataAdapter.initLoad(list)
        binding?.emptyView?.isVisible = list.isEmpty()
        val preSelectIndex = list.indexOfFirst { it.collectionTimeSlotId == preSelectedSlotId }
        if (preSelectIndex != -1) {
            dataAdapter.setSelectedPositions(preSelectIndex)
        }
    }

    private fun updateTimeSlot() {
        if (!validate()){
            return
        }
        binding?.submitBtn?.isEnabled = false

        val requestBody: MutableList<TimeSlotUpdateRequest> = mutableListOf()
        requestBody.add(TimeSlotUpdateRequest(orderRequestId, selectedTimeSLotID))
        viewModel.updateMultipleTimeSlot(requestBody).observe(viewLifecycleOwner, Observer { flag ->
            binding?.submitBtn?.isEnabled = true
            if (flag) {
                dialog?.dismiss()
                context?.toast("কালেকশন টাইম আপডেটেড")
                onUpdate?.invoke("")
            }
        })
    }

    private fun validate(): Boolean {

        if (selectedTimeSLotID == 0){
            context?.toast("সিলেক্ট টাইম স্লট")
            return false
        }
        if (selectedTimeSLotID == preSelectedSlotId) {
            context?.toast("বুকিং রিকোয়েস্ট ইতিমধ্যে এই টাইম স্লটে আছে")
            return false
        }

        return true
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
}