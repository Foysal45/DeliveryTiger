package com.bd.deliverytiger.app.ui.lead_management.phonebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookData
import com.bd.deliverytiger.app.databinding.FragmentPhonebookFormBinding
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class PhonebookFormBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentPhonebookFormBinding? = null

    var onSave: ((model: PhonebookData) -> Unit)? = null

    companion object {
        fun newInstance(): PhonebookFormBottomSheet = PhonebookFormBottomSheet().apply {

        }

        val tag: String = PhonebookFormBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme1)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentPhonebookFormBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListener()
    }

    private fun initClickListener() {

        binding?.saveBtn?.setOnClickListener {
            if (validation()) {
                save()
            }
        }
    }

    private fun save() {
        val name = binding?.customerNameET?.text?.toString() ?: ""
        val mobile = binding?.customerPhoneET?.text?.toString() ?: ""
        onSave?.invoke(PhonebookData(
            SessionManager.courierUserId,
            mobile,
            name
        ))
    }

    private fun validation(): Boolean {

        hideKeyboard()
        val name = binding?.customerNameET?.text?.toString() ?: ""
        val mobile = binding?.customerPhoneET?.text?.toString() ?: ""

        if (name.isEmpty()) {
            context?.toast("কাস্টমার নাম লিখুন")
            return false
        }

        if (mobile.isEmpty()) {
            context?.toast("কাস্টমার মোবাইল নম্বর লিখুন")
            return false
        }

        if (mobile.length != 11) {
            context?.toast("কাস্টমার সঠিক মোবাইল নম্বর লিখুন")
            return false
        }

        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}