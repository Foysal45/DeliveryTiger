package com.bd.deliverytiger.app.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInformation
import com.bd.deliverytiger.app.databinding.FragmentSmsShareDialogueBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import kotlin.concurrent.thread

class SmsShareDialogue : BottomSheetDialogFragment() {

    private var binding: FragmentSmsShareDialogueBinding? = null


    private val selectedNumberList: MutableList<String> = mutableListOf()
    var names = ""

    companion object {

        fun newInstance(model: List<CustomerInformation>): SmsShareDialogue = SmsShareDialogue().apply {
            model.forEach {
                val name = it.customerName
                selectedNumberList.add(name ?: "")
                names += "$name,"
            }

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


        initView()
        initClickLister()
    }

    private fun initView() {
        binding?.receiverNumber?.setText(names)
    }

    private fun initClickLister(){
        binding?.shareMessage?.setSelection(binding?.shareMessage?.text?.length ?: 0)
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