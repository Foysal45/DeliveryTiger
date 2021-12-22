package com.bd.deliverytiger.app.ui.all_orders.order_edit.reattempt_bottomsheet

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.databinding.FragmentReattemptBottomSheetBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.callHelplineNumber
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread


class ReattemptBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentReattemptBottomSheetBinding? = null

    var onReattemptClick: ((comments: String) -> Unit)? = null

    private var data: CourierOrderViewModel = CourierOrderViewModel()

    companion object {

        fun newInstance(model: CourierOrderViewModel): ReattemptBottomSheet =
            ReattemptBottomSheet().apply {
                data = model
            }

        val tag: String = ReattemptBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme1)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentReattemptBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickLister()
    }

    private fun initView() {
        binding?.mobile?.text = data?.courierAddressContactInfo?.mobile
        binding?.smsChargeInfo?.text = DigitConverter.toBanglaDigit(SessionManager.reAttemptCharge)
        if (!data?.courierAddressContactInfo?.otherMobile.isNullOrEmpty()) {
            binding?.alternateMobileNumber?.isVisible = true
            binding?.alternateMobileNumber?.text = data?.courierAddressContactInfo?.otherMobile
        }

    }

    private fun initClickLister() {

        binding?.reattemptBtn?.setOnClickListener {
            val comment = binding?.customComment?.text?.toString() ?: ""
            onReattemptClick?.invoke(comment)
        }

        binding?.callBtn?.setOnClickListener {
            var number = data?.courierAddressContactInfo?.mobile
            var altNumber = data?.courierAddressContactInfo?.otherMobile

            if (!number.isNullOrEmpty() && !altNumber.isNullOrEmpty()) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("কোন নাম্বার এ কল করতে চান")
                val numberLists = arrayOf(number, altNumber)
                builder.setItems(numberLists) { _, which ->
                    when (which) {
                        0 -> {
                            callHelplineNumber(numberLists[0])
                        }
                        1 -> {
                            callHelplineNumber(numberLists[1])
                        }
                    }
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                callHelplineNumber(number!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parentLayout?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                skipCollapsed = true
                isHideable = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}