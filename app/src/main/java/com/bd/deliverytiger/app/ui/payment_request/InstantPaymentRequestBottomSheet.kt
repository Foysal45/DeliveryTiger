package com.bd.deliverytiger.app.ui.payment_request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantInstantPaymentRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceiveableDetailRequest
import com.bd.deliverytiger.app.databinding.FragmentInstantPaymentRequestBottomSheetBinding
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

class InstantPaymentRequestBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentInstantPaymentRequestBottomSheetBinding? = null

    private val viewModel: InstantPaymentUpdateViewModel by inject()

    var onCloseBottomSheet: (() -> Unit)? = null


    private var instantPayableAmount: Int = 0
    private var payableAmount: Int = 0
    private var instantTransferCharge: Int = 0

    companion object {

        fun newInstance(): InstantPaymentRequestBottomSheet = InstantPaymentRequestBottomSheet().apply {}
        val tag: String = InstantPaymentRequestBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme3)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentInstantPaymentRequestBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickLister()
        fetchData()

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                }
            }
        })

    }

    private fun initData(){

        binding?.paymentAmount?.text = HtmlCompat.fromHtml("<font color='#626366'>৳ </font> <font color='#f05a2b'>${DigitConverter.toBanglaDigit(payableAmount, true)}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding?.instantPaymentTransferCharge?.text = "${DigitConverter.toBanglaDigit(instantTransferCharge)}"

        if (payableAmount == 0){
            binding?.transferBtnLayout?.isEnabled = false
            binding?.requestBtnLayout?.isEnabled = false
        }else{
            binding?.transferBtnLayout?.isEnabled = true
            binding?.requestBtnLayout?.isEnabled = true
        }
    }

    private fun initClickLister(){

        binding?.transferBtnLayout?.setOnClickListener {
            alert("নির্দেশনা", "আপনি কি ইনস্ট্যান্ট পেমেন্ট ট্রান্সফার করতে চান।",true, "হ্যাঁ", "না") {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    instantPaymentRequestAndTransfer(2) // for transfer balance 2
                }
            }.show()

        }

        binding?.requestBtnLayout?.setOnClickListener {
            alert("নির্দেশনা", "আপনি কি রেগুলার পেমেন্ট রিকোয়েস্ট করতে চান।",true, "হ্যাঁ", "না") {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    instantPaymentRequestAndTransfer(1) // for transfer balance 1
                }
            }.show()
        }

        binding?.paymentAmount?.setOnClickListener {
            val progressDialog = progressDialog("আপনার পেমেন্ট টি প্রসেসিং হচ্ছে। অনুগ্রহ করে অপেক্ষা করুন...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

    }



    private fun fetchData(){
        val requestBody = MerchantPayableReceiveableDetailRequest(SessionManager.courierUserId, 0)
        viewModel.getMerchantPayableDetailForInstantPayment(requestBody).observe(viewLifecycleOwner, Observer { model->
            if (model != null){
                payableAmount = model.payableAmount
                instantPayableAmount = model.netPayableAmount
                instantTransferCharge = model.instantPaymentCharge
                initData()
            }
        })
    }

    private fun instantPaymentRequestAndTransfer( paymentType: Int){
        val progressDialog = progressDialog("আপনার পেমেন্ট টি প্রসেসিং হচ্ছে। অনুগ্রহ করে অপেক্ষা করুন...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var charge = instantTransferCharge
        var amount = 0
        if (paymentType == 1) {
            charge = 0
            amount = payableAmount
        } else if (paymentType == 2){
            amount = instantPayableAmount
            charge = instantTransferCharge
        }
        if (amount > 0){
            val requestBody = MerchantInstantPaymentRequest(charge, SessionManager.courierUserId, amount, paymentType)
            viewModel.instantOr24hourPayment(requestBody).observe(viewLifecycleOwner, Observer { model->
                progressDialog.dismiss()
                dismiss()
                if (model != null) {
                    when (paymentType) {
                        1 -> {
                            if (model.message == 1){
                                alert("নির্দেশনা", "আপনার রিকোয়েস্ট টি সফল হয়েছে।",false, "ঠিক আছে") {
                                    if (it == AlertDialog.BUTTON_POSITIVE) {
                                        onCloseBottomSheet?.invoke()
                                    }
                                }.show()
                            }else{
                                alert("নির্দেশনা", "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন।",false, "ঠিক আছে") {}.show()
                            }
                        }
                        2 -> {
                            if (model.message == 1 && !model.transactionId.isNullOrEmpty()){
                                alert("নির্দেশনা", "আপনার লেনদেনটি সফলভাবে সম্পন্ন হয়েছে।",false, "ঠিক আছে") {
                                    if (it == AlertDialog.BUTTON_POSITIVE) {
                                        onCloseBottomSheet?.invoke()
                                    }
                                }.show()
                            }else if (model.message == 1 && model.transactionId.isNullOrEmpty()){
                                alert("নির্দেশনা", "অনুগ্রহ পূর্বক ডেলিভারি টাইগার এর একাউন্টস ডিপার্মেন্ট এর সাথে যোগাযোগ করুন।",false, "ঠিক আছে") {
                                    if (it == AlertDialog.BUTTON_POSITIVE) {
                                        onCloseBottomSheet?.invoke()
                                    }
                                }.show()
                            }else{
                                alert("নির্দেশনা", "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন।",false, "ঠিক আছে") {}.show()
                            }
                        }
                    }
                }else{
                    val message = "কোথাও কোনো সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                    context?.toast(message)
                    dismiss()
                }
            })
        }else{
            progressDialog.dismiss()
            dismiss()
            alert("নির্দেশনা", "আপনার ট্রান্সফার করার মতো ব্যালান্স নেই ",false, "ঠিক আছে") {}.show()
        }

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