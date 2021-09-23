package com.bd.deliverytiger.app.ui.lead_management.phonebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookGroupData
import com.bd.deliverytiger.app.databinding.FragmentPhonebookGroupBinding
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.concurrent.thread

class PhonebookGroupBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentPhonebookGroupBinding? = null
    private val viewModel: PhonebookGroupViewModel by inject()

    var onGroupSelected: ((model: PhonebookGroupData) -> Unit)? = null

    private val groupList: MutableList<PhonebookGroupData> = mutableListOf()

    companion object {
        fun newInstance(): PhonebookGroupBottomSheet = PhonebookGroupBottomSheet().apply {

        }

        val tag: String = PhonebookGroupBottomSheet::class.java.name
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPhonebookGroupBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchMyPhoneBookGroup()
        initClickListener()
    }

    private fun showGroups(list: List<PhonebookGroupData>) {

        list.forEach { model ->
            val radioBtn = RadioButton(requireContext())
            radioBtn.apply {
                id = model.phoneBookGroupId
                text = model.groupName
            }
            radioBtn.setOnClickListener {
                selectedGroup(it.id)
                binding?.groupNameET?.isVisible = false
                binding?.saveGroup?.isVisible = false
            }
            binding?.phonebookGroup?.addView(radioBtn)
        }

        val radioBtn = RadioButton(requireContext())
        radioBtn.apply {
            id = 0
            text = "নতুন গ্রুপ তৈরি করুন"
        }
        radioBtn.setOnClickListener {
            binding?.groupNameET?.isVisible = true
            binding?.saveGroup?.isVisible = true
            expandBottomSheet()
        }
        binding?.phonebookGroup?.addView(radioBtn)

    }

    private fun initClickListener() {

        binding?.saveGroup?.setOnClickListener {
            createPhoneBookGroup()
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ProgressState -> {
                    binding?.progressBar?.isVisible = state.isShow
                }
            }
        })
    }

    private fun expandBottomSheet() {
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun fetchMyPhoneBookGroup() {
        viewModel.fetchMyPhoneBookGroup(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            groupList.clear()
            groupList.addAll(list)
            showGroups(groupList)
        })
    }

    private fun selectedGroup(groupId: Int) {
        Timber.d("selected group id $groupId")
        val model = groupList.find { it.phoneBookGroupId == groupId }
        model?.let {
            onGroupSelected?.invoke(it)
        }
    }

    private fun createPhoneBookGroup() {

        hideKeyboard()
        val groupName = binding?.groupNameET?.text?.toString() ?: ""
        if (groupName.isEmpty()) {
            context?.toast("ফোনবুক গ্রুপের নাম লিখুন")
            return
        }

        val requestBody: MutableList<PhonebookGroupData> = mutableListOf()
        requestBody.add(
            PhonebookGroupData(
                SessionManager.courierUserId,
                groupName
            )
        )
        viewModel.createPhoneBookGroup(requestBody).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                context?.toast("নতুন ফোনবুক তৈরি হয়েছে")
                onGroupSelected?.invoke(list.first())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}