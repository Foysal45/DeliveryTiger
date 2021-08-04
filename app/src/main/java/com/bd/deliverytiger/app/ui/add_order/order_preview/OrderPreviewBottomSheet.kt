package com.bd.deliverytiger.app.ui.add_order.order_preview

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

class OrderPreviewBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentOrderPreviewBinding? = null

    private var dataList: OrderPreviewData? = null
    var onClose: ((type: Int) -> Unit)? = null
    var onConfirmedClick: ((type: Int) -> Unit)? = null

    companion object {

        fun newInstance(data: OrderPreviewData): OrderPreviewBottomSheet = OrderPreviewBottomSheet().apply {
            dataList = data
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
        binding?.nameTV?.text = dataList?.customerName
        binding?.mobileTV?.text = dataList?.mobile
        binding?.districtTV?.text = dataList?.district
        binding?.thanaTV?.text = dataList?.thana
        if (dataList?.codCharge == 0.0){
            binding?.codAmountTV?.isVisible = false
            binding?.detailsItem5?.isVisible = false
        }else{

            binding?.codAmountTV?.isVisible = true
            binding?.detailsItem5?.isVisible = true
            binding?.codAmountTV?.text = "৳ ${dataList?.codCharge}"
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