package com.bd.deliverytiger.app.ui.add_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.databinding.FragmentToggleButtonPickupBottomSheetBinding
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
import com.bd.deliverytiger.app.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread


class ToggleButtonPickupBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentToggleButtonPickupBottomSheetBinding? = null

    var onTogglePickupGroupClicked:  ((isOfficeDrop: Boolean, isDialogueDismiss: Boolean) -> Unit)? = null
    var onSpinnerCollectionLocationClicked:  ((position: Int, isDialogueDismiss: Boolean) -> Unit)? = null

    private lateinit var togglePickupGroup: MaterialButtonToggleGroup
    private lateinit var toggleButtonPickup1: MaterialButton
    private lateinit var pickupAddressLayout: ConstraintLayout
    private lateinit var spinnerCollectionLocation: AppCompatSpinner
    private val viewModel: AddOrderViewModel by inject()

    private var isOfficeDrop: Boolean = false
    private var isDialogueDismiss: Boolean = false

    companion object {

        fun newInstance(): ToggleButtonPickupBottomSheet = ToggleButtonPickupBottomSheet().apply {

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
        return FragmentToggleButtonPickupBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        togglePickupGroup = view.findViewById(R.id.toggleButtonPickupGroup)
        toggleButtonPickup1 = view.findViewById(R.id.toggleButtonPickup1)
        pickupAddressLayout = view.findViewById(R.id.pickupAddressLayout)
        spinnerCollectionLocation = view.findViewById(R.id.spinnerCollectionLocation)

        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            spinnerDataBinding(list)
        })


        togglePickupGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.toggleButtonPickup1 ->
                    {
                        isOfficeDrop = true
                        pickupAddressLayout.visibility = View.GONE
                        isDialogueDismiss = true
                    }
                    R.id.toggleButtonPickup2 -> {
                        isOfficeDrop = false
                        pickupAddressLayout.visibility = View.VISIBLE
                    }
                }
                onTogglePickupGroupClicked?.invoke(isOfficeDrop, isDialogueDismiss)
                isDialogueDismiss = false
            }
        }

        spinnerCollectionLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //isDialogueDismiss = true
                onSpinnerCollectionLocationClicked?.invoke(position, isDialogueDismiss)
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
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}