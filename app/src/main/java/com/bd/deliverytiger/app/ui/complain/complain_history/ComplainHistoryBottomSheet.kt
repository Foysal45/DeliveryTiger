package com.bd.deliverytiger.app.ui.complain.complain_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentComplainHistoryBottomSheetBinding
import com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet.ServicesSelectionBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class ComplainHistoryBottomSheet : BottomSheetDialogFragment() {
    private var binding: FragmentComplainHistoryBottomSheetBinding? = null

    companion object {

        fun newInstance(): ComplainHistoryBottomSheet = ComplainHistoryBottomSheet().apply {

        }

        val tag: String = ServicesSelectionBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentComplainHistoryBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
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

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}