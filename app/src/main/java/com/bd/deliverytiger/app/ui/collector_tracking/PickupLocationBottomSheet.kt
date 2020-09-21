package com.bd.deliverytiger.app.ui.collector_tracking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.databinding.FragmentPickupLocationBottomSheetBinding
import com.bd.deliverytiger.app.ui.profile.pickup_address.PickUpLocationAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class PickupLocationBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentPickupLocationBottomSheetBinding? = null
    var onItemClicked: ((model: PickupLocation) -> Unit)? = null

    private lateinit var pickUpList: List<PickupLocation>

    companion object {

        fun newInstance(pickUpList: List<PickupLocation>): PickupLocationBottomSheet = PickupLocationBottomSheet().apply {
            this.pickUpList = pickUpList
        }
        val tag: String = PickupLocationBottomSheet::class.java.name
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
        return FragmentPickupLocationBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickupAddressAdapter = PickUpLocationAdapter()
        pickupAddressAdapter.initList(pickUpList)
        binding?.recyclerview?.let { view ->
            with(view) {
                setHasFixedSize(false)
                isNestedScrollingEnabled = false
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pickupAddressAdapter
            }
        }
        pickupAddressAdapter.onItemClicked = { model ->
            onItemClicked?.invoke(model)
        }

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}