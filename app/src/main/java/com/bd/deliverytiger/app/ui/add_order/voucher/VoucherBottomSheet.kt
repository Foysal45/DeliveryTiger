package com.bd.deliverytiger.app.ui.add_order.voucher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.voucher.VoucherCheckRequest
import com.bd.deliverytiger.app.api.model.voucher.VoucherCheckResponse
import com.bd.deliverytiger.app.databinding.FragmentVoucherBottomSheetBinding
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

class VoucherBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentVoucherBottomSheetBinding? = null
    var onVoucherApplied: ((model: VoucherCheckResponse, isVoucherApplied: Boolean) -> Unit)? = null

    private val viewModel: VoucherViewModel by inject()

    private var deliveryRangeId: Int = 0
    private var isVoucherApplied: Boolean = false

    companion object {

        fun newInstance(deliveryRangeId: Int): VoucherBottomSheet = VoucherBottomSheet().apply {
            this.deliveryRangeId = deliveryRangeId
        }
        val tag: String = VoucherBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme2)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentVoucherBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickLister()

    }

    private fun initClickLister(){
        binding?.applyBtn?.setOnClickListener {
            if (validate()){
                hideKeyboard()
                val requestBody = VoucherCheckRequest(SessionManager.courierUserId, deliveryRangeId, binding?.voucherCodeET?.text.toString())
                viewModel.checkVoucher(requestBody).observe(viewLifecycleOwner, Observer { response->
                    if (response.message.isEmpty()){
                        context?.toast("সফলভাবে এপ্লাই করা হয়েছে")
                        isVoucherApplied = true
                        onVoucherApplied?.invoke(response.model, isVoucherApplied)
                        dismiss()
                    }else{
                        isVoucherApplied = false
                        context?.toast(response.message)
                    }
                })
            }
        }
    }

    private fun validate(): Boolean{

        if (binding?.voucherCodeET?.text.isNullOrEmpty()){
            context?.toast("ভাউচার কোড লিখুন")
            binding?.voucherCodeET?.requestFocus()
            return false
        }

        return true
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
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
            BottomSheetBehavior.from(bottomSheet)
                .addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(p0: View, p1: Float) {
                        BottomSheetBehavior.from(bottomSheet).state =
                            BottomSheetBehavior.STATE_EXPANDED
                    }

                    override fun onStateChanged(p0: View, p1: Int) {
                        /*if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                        }*/
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}