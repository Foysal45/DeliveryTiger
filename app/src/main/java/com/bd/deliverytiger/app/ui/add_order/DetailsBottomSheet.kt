package com.bd.deliverytiger.app.ui.add_order


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A simple [Fragment] subclass.
 */
class DetailsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var shipmentTV: TextView
    private lateinit var codChargeTV: TextView
    private lateinit var breakableChargeTV: TextView
    private lateinit var collectionChargeTV: TextView
    private lateinit var packagingChargeTV: TextView
    private lateinit var totalTV: TextView
    private lateinit var codPercentTV: TextView

    private lateinit var bundle: Bundle
    private var payShipmentCharge: Double = 0.0
    private var payCODCharge: Double = 0.0
    private var payBreakableCharge: Double = 0.0
    private var payCollectionCharge: Double = 0.0
    private var payPackagingCharge: Double = 0.0
    private var codChargePercentage: Double = 0.0


    companion object{
        fun newInstance(bundle: Bundle): DetailsBottomSheet = DetailsBottomSheet().apply {
            this.bundle = bundle
        }
        val tag = DetailsBottomSheet::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shipmentTV = view.findViewById(R.id.details_item_value_1)
        codChargeTV = view.findViewById(R.id.details_item_value_2)
        breakableChargeTV = view.findViewById(R.id.details_item_value_3)
        collectionChargeTV = view.findViewById(R.id.details_item_value_4)
        packagingChargeTV = view.findViewById(R.id.details_item_value_5)
        totalTV = view.findViewById(R.id.details_item_value_6)
        codPercentTV = view.findViewById(R.id.details_item_2)

        with(bundle) {
            payShipmentCharge = getDouble("payShipmentCharge", 0.0)
            payCODCharge = getDouble("payCODCharge", 0.0)
            payBreakableCharge = getDouble("payBreakableCharge", 0.0)
            payCollectionCharge = getDouble("payCollectionCharge", 0.0)
            payPackagingCharge = getDouble("payPackagingCharge", 0.0)
            codChargePercentage = getDouble("codChargePercentage", 0.0)
        }

        codPercentTV.text = "COD চার্জঃ (${DigitConverter.toBanglaDigit(codChargePercentage, false)}%)"

        shipmentTV.text = "৳ ${DigitConverter.toBanglaDigit(payShipmentCharge, true)}"
        codChargeTV.text = "৳ ${DigitConverter.toBanglaDigit(payCODCharge, true)}"
        breakableChargeTV.text = "৳ ${DigitConverter.toBanglaDigit(payBreakableCharge, true)}"
        collectionChargeTV.text = "৳ ${DigitConverter.toBanglaDigit(payCollectionCharge, true)}"
        packagingChargeTV.text = "৳ ${DigitConverter.toBanglaDigit(payPackagingCharge, true)}"

        val total = payShipmentCharge + payCODCharge + payBreakableCharge + payCollectionCharge + payPackagingCharge
        totalTV.text = "৳ ${DigitConverter.toBanglaDigit(total, true)}"

    }


}
