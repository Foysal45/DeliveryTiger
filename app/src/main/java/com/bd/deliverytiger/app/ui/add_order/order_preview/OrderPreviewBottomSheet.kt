package com.bd.deliverytiger.app.ui.add_order.order_preview

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order.OrderPreviewData
import com.bd.deliverytiger.app.databinding.FragmentOrderPreviewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class OrderPreviewBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentOrderPreviewBinding? = null

    private var model: OrderPreviewData? = null
    var onClose: ((type: Int) -> Unit)? = null
    var onConfirmedClick: ((type: Int) -> Unit)? = null

    companion object {
        fun newInstance(model: OrderPreviewData): OrderPreviewBottomSheet = OrderPreviewBottomSheet().apply {
            this.model = model
        }
        val tag: String = OrderPreviewBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentOrderPreviewBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickLister()
    }


    private fun initView() {
        binding?.nameTV?.text = model?.customerName
        binding?.mobileTV?.text = model?.mobile
        binding?.districtTV?.text = model?.district
        binding?.thanaTV?.text = model?.thana
        if (model?.isCollection == true) {
            binding?.codAmountTV?.isVisible = true
            binding?.detailsItem5?.isVisible = true
            binding?.codAmountTV?.text = "৳ ${model?.codCharge}"
        }
    }

    private fun initClickLister() {

        binding?.confirmBtn?.setOnClickListener {
            onConfirmedClick?.invoke(1)
        }
        binding?.editBtn?.setOnClickListener {
            onClose?.invoke(1)
        }
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
            /*BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                    BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                }
                override fun onStateChanged(p0: View, p1: Int) {
                    *//*if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                    }*//*
                }
            })*/
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onClose?.invoke(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}