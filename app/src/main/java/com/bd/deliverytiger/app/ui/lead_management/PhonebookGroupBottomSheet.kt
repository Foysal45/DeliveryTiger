package com.bd.deliverytiger.app.ui.lead_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookGroupData
import com.bd.deliverytiger.app.databinding.FragmentPhonebookGroupBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class PhonebookGroupBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentPhonebookGroupBinding? = null

    companion object {
        fun newInstance(): PhonebookGroupBottomSheet = PhonebookGroupBottomSheet().apply {

        }
        val tag: String = PhonebookGroupBottomSheet::class.java.name
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
        return FragmentPhonebookGroupBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list: MutableList<PhonebookGroupData> = mutableListOf()
        list.add(PhonebookGroupData("Group1", 1))
        showGroups(list)

        initClickListener()
    }

    private fun showGroups(list: List<PhonebookGroupData>) {

        list.forEach { model ->
            val radioBtn = RadioButton(requireContext())
            radioBtn.apply {
                text = model.groupName
                id = model.phoneBookGroupId
            }
            binding?.phonebookGroup?.addView(radioBtn)
        }
        val radioBtn = RadioButton(requireContext())
        radioBtn.apply {
            text = "Create new group"
        }

    }

    private fun initClickListener() {
        binding?.phonebookGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}