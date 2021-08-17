package com.bd.deliverytiger.app.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInformation
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSBody
import com.bd.deliverytiger.app.api.model.sms.SMSModel
import com.bd.deliverytiger.app.databinding.FragmentSmsShareDialogueBinding
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.concurrent.thread

class SmsShareDialogue : BottomSheetDialogFragment() {

    private var binding: FragmentSmsShareDialogueBinding? = null
    private val viewModel: SmsShareViewModel by inject()

    private var customerList: List<CustomerInformation>? = null
    private val selectedNameList: MutableList<String> = mutableListOf()
    private val selectedNumberList: MutableList<String> = mutableListOf()
    private var smsLimit: Int = 0
    var onSend: ((isSend: Boolean) -> Unit)? = null

    companion object {
        fun newInstance(model: List<CustomerInformation>): SmsShareDialogue = SmsShareDialogue().apply {
            this.customerList = model
        }
        val tag: String = SmsShareDialogue::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentSmsShareDialogueBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchCourierInfo()
        initView()
        initClickLister()
    }

    private fun initView() {
        selectedNameList.clear()
        selectedNumberList.clear()
        customerList?.forEach {
            selectedNameList.add(it.customerName ?: "")
            selectedNumberList.add(it.mobile ?: "")
        }

        val names = selectedNameList.joinToString()
        binding?.receiverNumber?.setText(names)

        val msgLength = binding?.shareMessage?.text?.length ?: 0
        binding?.shareMessage?.setSelection(msgLength)
    }

    private fun fetchCourierInfo() {
        viewModel.getCourierUsersInformation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            smsLimit = model.customerSMSLimit
        })
    }

    private fun initClickLister(){

        binding?.sendSMS?.setOnClickListener {
            if (validation()) {
                sendSMS()
            }
        }
    }

    private fun sendSMS() {
        binding?.progressBar?.isVisible = true

        val msg = binding?.shareMessage?.text?.toString() ?: ""
        val requestBody: MutableList<SMSModel> = mutableListOf()
        requestBody.add(SMSModel(numbers = selectedNumberList, text = msg))
        viewModel.sendSMS(requestBody).observe(viewLifecycleOwner, Observer { model ->
            if (model.status) {
                context?.toast("SMS Send")
                if (isAdded) {
                    binding?.progressBar?.isVisible = false
                }
                onSend?.invoke(true)
            }
        })
        viewModel.updateCustomerSMSLimit(SessionManager.courierUserId, selectedNumberList.size).observe(viewLifecycleOwner, Observer { model ->
            smsLimit = model.customerSMSLimit
        })
    }

    private fun validation(): Boolean {

        if (selectedNumberList.isEmpty()) {
            context?.toast("Enter mobile number")
            return false
        }
        val msg = binding?.shareMessage?.text?.toString() ?: ""
        if (msg.isEmpty()) {
            context?.toast("Enter message")
            return false
        }
        if (selectedNumberList.size > smsLimit) {
            context?.toast("Current SMS limit is $smsLimit")
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parentLayout?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
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