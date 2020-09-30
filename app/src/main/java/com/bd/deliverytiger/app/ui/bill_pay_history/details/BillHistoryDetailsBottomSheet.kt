package com.bd.deliverytiger.app.ui.bill_pay_history.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.bill_pay_history.BillPayHistoryResponse
import com.bd.deliverytiger.app.databinding.FragmentBillHistoryDetailsBottomSheetBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class BillHistoryDetailsBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentBillHistoryDetailsBottomSheetBinding? = null
    var onItemClicked: ((model: BillPayHistoryResponse) -> Unit)? = null

    private lateinit var model: BillPayHistoryResponse

    companion object {

        fun newInstance(model: BillPayHistoryResponse): BillHistoryDetailsBottomSheet = BillHistoryDetailsBottomSheet().apply {
            this.model = model
        }
        val tag: String = BillHistoryDetailsBottomSheet::class.java.name
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
        return FragmentBillHistoryDetailsBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataAdapter = BillHistoryDetailsAdapter()
        dataAdapter.initLoad(model.orderList)
        binding?.recyclerview?.let { view1 ->
            with(view1) {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        }

        binding?.header?.info1?.text = "মোট অর্ডার: ${DigitConverter.toBanglaDigit(model.orderList.size)} টি"
        binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ${DigitConverter.toBanglaDigit(model.netPaidAmount, true)} ৳"
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}