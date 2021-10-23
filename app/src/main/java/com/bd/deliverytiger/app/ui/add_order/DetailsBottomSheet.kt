package com.bd.deliverytiger.app.ui.add_order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentDetailsBottomSheetBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@SuppressLint("SetTextI18n")
class DetailsBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentDetailsBottomSheetBinding? = null

    private var bundle: Bundle? = null
    private var payShipmentCharge: Double = 0.0
    private var deliveryCharge: Double = 0.0
    private var extraDeliveryCharge: Double = 0.0
    private var payCODCharge: Double = 0.0
    private var payBreakableCharge: Double = 0.0
    private var payCollectionCharge: Double = 0.0
    private var payPackagingCharge: Double = 0.0
    private var codChargePercentage: Double = 0.0
    private var boroProductCheck: Boolean = false
    private var bigProductCharge: Double = 0.0
    private var total: Double = 0.0
    private var voucherDiscount: Double = 0.0
    private var productType: String = ""
    private var isBreakable: Boolean = false

    companion object{
        fun newInstance(bundle: Bundle): DetailsBottomSheet = DetailsBottomSheet().apply {
            this.bundle = bundle
        }
        val tag: String = DetailsBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDetailsBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bundle?.let {
            with(it) {
                payShipmentCharge = getDouble("payShipmentCharge", 0.0)
                deliveryCharge = getDouble("deliveryCharge", 0.0)
                extraDeliveryCharge = getDouble("extraDeliveryCharge", 0.0)
                payCODCharge = getDouble("payCODCharge", 0.0)
                payBreakableCharge = getDouble("payBreakableCharge", 0.0)
                payCollectionCharge = getDouble("payCollectionCharge", 0.0)
                payPackagingCharge = getDouble("payPackagingCharge", 0.0)
                codChargePercentage = getDouble("codChargePercentage", 0.0)
                bigProductCharge = getDouble("bigProductCharge", 0.0)
                boroProductCheck = getBoolean("boroProductCheck", false)
                productType = getString("productType", "")
                total = getDouble("total", 0.0)
                isBreakable = getBoolean("isBreakable", false)
                voucherDiscount = getDouble("voucherDiscount", 0.0)
            }
        }

        binding?.shipmentTV?.text = "${DigitConverter.toBanglaDigit(deliveryCharge, true)} ৳"
        binding?.shipmentExtraTV?.text = "${DigitConverter.toBanglaDigit(extraDeliveryCharge, true)} ৳"
        binding?.codChargeTitleTV?.text = "COD চার্জঃ (${DigitConverter.toBanglaDigit(codChargePercentage, false)}%)"
        binding?.codChargeTV?.text = "${DigitConverter.toBanglaDigit(payCODCharge, true)} ৳"
        binding?.breakableChargeTV?.text = "${DigitConverter.toBanglaDigit(payBreakableCharge, true)} ৳"
        binding?.collectionChargeTV?.text = "${DigitConverter.toBanglaDigit(payCollectionCharge, true)} ৳"
        binding?.packagingChargeTV?.text = "${DigitConverter.toBanglaDigit(payPackagingCharge, true)} ৳"
        binding?.totalTV?.text = "${DigitConverter.toBanglaDigit(total, true)} ৳"
        binding?.voucherDiscountTV?.text = "${DigitConverter.toBanglaDigit(voucherDiscount, true)} ৳"

        if (isBreakable) {
            binding?.detailsItem5?.text = "এক্সট্রা প্যাকেজিং চার্জ (তরল/ভঙ্গুর প্রোডাক্টের জন্য)"
        }

        binding?.closeBtn?.setOnClickListener {
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
