package com.bd.deliverytiger.app.ui.add_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.databinding.FragmentCollectionInfoBottomSheetBinding
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread


class ToggleButtonPickupBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentCollectionInfoBottomSheetBinding? = null

    var onCollectionTypeSelected: ((isPickup: Boolean, pickupLocation: PickupLocation) -> Unit)? = null

    private lateinit var pickupAddressLayout: ConstraintLayout
    private lateinit var spinnerCollectionLocation: AppCompatSpinner

    private var weightRangeId = 0

    private val viewModel: AddOrderViewModel by inject()

    companion object {
        fun newInstance(weightRangeId: Int): ToggleButtonPickupBottomSheet = ToggleButtonPickupBottomSheet().apply {
            this.weightRangeId = weightRangeId
        }
        val tag: String = ToggleButtonPickupBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()

        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(false)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)

        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                skipCollapsed = true
                isHideable = false

            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCollectionInfoBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerCollectionLocation = view.findViewById(R.id.spinnerCollectionLocation)
        pickupAddressLayout = view.findViewById(R.id.pickupAddressLayout)

        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            spinnerDataBinding(list)
        })

        binding?.toggleButtonPickupGroup?.setOnSelectListener { button ->
            when (button.id) {
                R.id.toggleButtonPickup1 -> {
                    onCollectionTypeSelected?.invoke(false, PickupLocation())
                    pickupAddressLayout.visibility = View.GONE
                }
                R.id.toggleButtonPickup2 -> {
                    if (weightRangeId > 6) {
                        binding?.msg?.isVisible = true
                        binding?.msg?.text = "পার্সেলের ওজন ৫ কেজির উপরে হলে কালেকশন হাবে ড্রপ করতে হবে, হাব ড্রপ সিলেক্ট করুন"
                    } else {
                        binding?.msg?.isVisible = false
                        pickupAddressLayout.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    private fun spinnerDataBinding(list: List<PickupLocation>){
        val pickupList: MutableList<String> = mutableListOf()
        pickupList.add("পিক আপ লোকেশন")
        list.forEach {
            pickupList.add(it.thanaName ?: "")
        }
        val pickupAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupList)
        spinnerCollectionLocation.adapter = pickupAdapter
        spinnerCollectionLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val model = list[position-1]
                    onCollectionTypeSelected?.invoke(true, model)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}