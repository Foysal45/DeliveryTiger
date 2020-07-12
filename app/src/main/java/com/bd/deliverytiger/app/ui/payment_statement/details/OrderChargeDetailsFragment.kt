package com.bd.deliverytiger.app.ui.payment_statement.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_statement.OrderHistoryData
import com.bd.deliverytiger.app.databinding.DialogOrderChangeDetailsBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderChargeDetailsFragment: BottomSheetDialogFragment() {

    private var model: OrderHistoryData? = null
    private var binding: DialogOrderChangeDetailsBinding? = null

    companion object {
        fun newInstance(orderHistoryData: OrderHistoryData?): OrderChargeDetailsFragment = OrderChargeDetailsFragment().apply {
            this.model = orderHistoryData
        }
        val tag: String = OrderChargeDetailsFragment::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DialogOrderChangeDetailsBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding?.orderCode?.text = model?.orderCode
        binding?.collectedAmount?.text = "${DigitConverter.toBanglaDigit(model?.collectedAmount.toString())} ৳"
        binding?.shipmentsCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.deliveryCharge.toString())} ৳"
        binding?.CODCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.CODCharge.toString())} ৳"
        binding?.breakingCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.breakableCharge.toString())} ৳"
        binding?.collectionCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.collectionCharge.toString())} ৳"
        binding?.returnCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.returnCharge.toString())} ৳"
        binding?.packagingCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.packagingCharge.toString())} ৳"
        binding?.netAmount?.text = "${DigitConverter.toBanglaDigit(model?.amount.toString())} ৳"
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}