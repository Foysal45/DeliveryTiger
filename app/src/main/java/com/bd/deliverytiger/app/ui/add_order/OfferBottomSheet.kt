package com.bd.deliverytiger.app.ui.add_order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentOfferBottomSheetBinding
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class OfferBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentOfferBottomSheetBinding? = null
    var onOfferSelected: ((offerType: Int) -> Unit)? = null

    private var bundle: Bundle? = null
    private var offerType = 0
    private var offerCodDiscount = 0
    private var offerBkashDiscount = 0
    private var offerBkashClaimed = false
    private var offerCodClaimed = false
    private var isCollection = false


    companion object {

        fun newInstance(bundle: Bundle?): OfferBottomSheet = OfferBottomSheet().apply {
            this.bundle = bundle
        }

        val tag: String = OfferBottomSheet::class.java.name
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
                skipCollapsed = true
                isHideable = true

            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentOfferBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offerType = bundle?.getInt("offerType", 0) ?: 0
        offerCodDiscount = bundle?.getInt("offerCodDiscount", 0) ?: 0
        offerBkashDiscount = bundle?.getInt("offerBkashDiscount", 0) ?: 0
        offerBkashClaimed = bundle?.getBoolean("offerBkashClaimed") ?: false
        offerCodClaimed = bundle?.getBoolean("offerCodClaimed") ?: false
        isCollection = bundle?.getBoolean("isCollection") ?: false


        binding?.bodyTV?.text = "আপনার অর্ডারটি সফলভাবে গ্রহণ করা হয়েছে। আপনার জন্য রয়েছে ডিসকাউন্ট অফার।"

        when (offerType) {
            // COD
            1 -> {
                if (!offerCodClaimed) {
                    codLayout()
                } else {
                    codClaimLayout()
                }

            }
            // bkash
            2 -> {
                if (isCollection) {
                    if (!offerBkashClaimed) {
                        advanceLayout()
                    } else {
                        advanceClaimLayout()
                    }
                }
            }
            // All
            3 -> {

                if (!offerCodClaimed) {
                    codLayout()
                } else {
                    codClaimLayout()
                }

                if (isCollection) {
                    if (!offerBkashClaimed) {
                        advanceLayout()
                    } else {
                        advanceClaimLayout()
                    }
                }
            }
        }

    }


    private fun codLayout() {
        binding?.codLayout?.visibility = View.VISIBLE
        binding?.codTitle?.text = "অর্ডার পার্সেলের প্রোডাক্টটির ছবি ও দাম আজকেরডিল এর মার্কেটপ্লেস-এ লিস্ট করুন। ${DigitConverter.toBanglaDigit(offerCodDiscount)} টাকা ছাড় নিন"
        binding?.codOfferBtn?.text = "${DigitConverter.toBanglaDigit(offerCodDiscount)} টাকা ছাড় নিন।"
        binding?.codOfferBtn?.setOnClickListener {
            onOfferSelected?.invoke(1)
        }
    }

    private fun advanceLayout() {
        binding?.advanceLayout?.visibility = View.VISIBLE
        binding?.advanceTitle?.text = "অর্ডার পার্সেলের প্রোডাক্টটির ছবি ও দাম আজকেরডিল এর মার্কেটপ্লেস-এ লিস্ট করুন।\nকাস্টমারকে বিকাশ পেমেন্ট করতে বলুন এবং সফল পেমেন্টে\n${DigitConverter.toBanglaDigit(offerBkashDiscount)} টাকা ছাড় নিন।"
        binding?.advanceOfferBtn?.text = "${DigitConverter.toBanglaDigit(offerBkashDiscount)} টাকা ছাড় নিন"
        binding?.advanceOfferBtn?.setOnClickListener {
            onOfferSelected?.invoke(2)
        }
    }

    private fun codClaimLayout() {
        binding?.codClaimedLayout?.visibility = View.VISIBLE
        binding?.codClaimTitle?.text = "আপনার প্রোডাক্টটি আজকেরডিল মার্কেটপ্লেসে যুক্ত হয়েছে এবং আপনি ${DigitConverter.toBanglaDigit(offerCodDiscount)} টাকা ছাড় পেয়েছেন।"
    }

    private fun advanceClaimLayout() {
        binding?.advanceClaimedLayout?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}