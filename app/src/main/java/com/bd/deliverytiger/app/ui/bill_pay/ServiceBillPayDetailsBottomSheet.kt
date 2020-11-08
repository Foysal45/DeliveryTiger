package com.bd.deliverytiger.app.ui.bill_pay

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthData
import com.bd.deliverytiger.app.databinding.FragmentServiceBillPayDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class ServiceBillPayDetailsBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentServiceBillPayDetailsBinding? = null
    private lateinit var monthData: MonthData

    companion object {
        fun newInstance(monthData: MonthData): ServiceBillPayDetailsBottomSheet = ServiceBillPayDetailsBottomSheet().apply {
            this.monthData = monthData
        }
        val tag: String = ServiceBillPayDetailsBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()

        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        //val metrics = resources.displayMetrics

        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parent?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                //state = BottomSheetBehavior.STATE_COLLAPSED
                skipCollapsed = false
                isHideable = false

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentServiceBillPayDetailsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataAdapter = ServiceBillAdapter()
        dataAdapter.initLoad(monthData.orderList)
        binding?.recyclerview?.let { view1 ->
            with(view1) {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        }

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}