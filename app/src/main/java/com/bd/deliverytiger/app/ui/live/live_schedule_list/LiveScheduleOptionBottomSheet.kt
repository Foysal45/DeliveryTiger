package com.bd.deliverytiger.app.ui.live.live_schedule_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentLiveScheduleOptionBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class LiveScheduleOptionBottomSheet(): BottomSheetDialogFragment() {

    private var binding: FragmentLiveScheduleOptionBottomSheetBinding? = null
    var onActionClicked: ((action: Int) -> Unit)? = null

    companion object {
        fun newInstance(): LiveScheduleOptionBottomSheet = LiveScheduleOptionBottomSheet().apply {}
        val tag: String = LiveScheduleOptionBottomSheet::class.java.name
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
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parent?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                isHideable = false

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return FragmentLiveScheduleOptionBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.action3?.setOnClickListener {
            onActionClicked?.invoke(3)
        }

        /*binding?.action1?.setOnClickListener {
            onActionClicked?.invoke(1)
        }
        binding?.action2?.setOnClickListener {
            onActionClicked?.invoke(2)
        }
        binding?.action4?.setOnClickListener {
            onActionClicked?.invoke(4)
        }
        binding?.action5?.setOnClickListener {
            onActionClicked?.invoke(5)
        }*/
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}