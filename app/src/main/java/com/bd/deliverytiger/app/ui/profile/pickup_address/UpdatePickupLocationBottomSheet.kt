package com.bd.deliverytiger.app.ui.profile.pickup_address

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.databinding.FragmentUpdatePickupLocationBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.profile.ProfileViewModel
import com.bd.deliverytiger.app.utils.alert
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class UpdatePickupLocationBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentUpdatePickupLocationBinding? = null
    var onUpdateClicked: ((model: PickupLocation) -> Unit)? = null

    private val viewModel: ProfileViewModel by inject()
    private val homeViewModel: HomeViewModel by inject()

    private lateinit var pickUpLocation: PickupLocation
    private var isGPSRetry: Boolean = false
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    companion object {
        fun newInstance(pickUpLocation: PickupLocation): UpdatePickupLocationBottomSheet = UpdatePickupLocationBottomSheet().apply {
            this.pickUpLocation = pickUpLocation
        }
        val tag: String = UpdatePickupLocationBottomSheet::class.java.name
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
                skipCollapsed = false
                isHideable = false

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentUpdatePickupLocationBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.districtSelect?.setText(pickUpLocation.districtName)
        binding?.thanaSelect?.setText(pickUpLocation.thanaName)
        binding?.pickupAddress?.setText(pickUpLocation.pickupAddress)
        val gpsLocation = "${pickUpLocation.latitude}, ${pickUpLocation.longitude}"
        binding?.gpsLocation?.setText(gpsLocation)
        binding?.pickupContact?.setText(pickUpLocation.mobile)

        binding?.updatePickupBtn?.setOnClickListener {
            hideKeyboard()
            if (validate()) {
                updatePickUpAddress()
            }
        }

        binding?.gpsBtn?.setOnClickListener {
            alert("নির্দেশনা", "আপনি কি এখন পিকআপ ঠিকানায় আছেন? যদি পিকআপ ঠিকানায় অবস্থান করেন তাহলে জিপিএস লোকেশন অ্যাড করুন অন্যথায় পিকআপ ঠিকানায় গিয়ে জিপিএস লোকেশন অ্যাড করুন", true,"পিকআপ ঠিকানায় আছি", "ক্যানসেল") {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    getGPSLocation()
                }
            }.show()
        }
    }

    private fun updatePickUpAddress() {
        viewModel.updatePickupLocations(pickUpLocation).observe(viewLifecycleOwner, Observer { model ->
            onUpdateClicked?.invoke(model)
        })
    }

    private fun getGPSLocation() {
        (activity as HomeActivity).fetchCurrentLocation()
        homeViewModel.currentLocation.observe(viewLifecycleOwner, Observer { location ->
            location ?: return@Observer
            currentLatitude = location.latitude
            currentLongitude = location.longitude
            binding?.gpsLocation?.setText("$currentLatitude, $currentLongitude")
            pickUpLocation.latitude = location.latitude.toString()
            pickUpLocation.longitude = location.longitude.toString()
            if (isGPSRetry) {
                context?.toast("জিপিএস লোকেশন আপডেট হয়েছে")
            }
            isGPSRetry = true
            homeViewModel.currentLocation.value = null
        })
    }

    private fun validate(): Boolean {
        if (pickUpLocation.districtId == 0) {
            context?.toast(getString(R.string.select_dist))
            return false
        }
        if (pickUpLocation.thanaId == 0) {
            context?.toast(getString(R.string.select_thana))
            return false
        }

        val newPickUpAddress = binding?.pickupAddress?.text?.toString() ?: ""
        if (newPickUpAddress.trim().isEmpty() || newPickUpAddress.length < 15) {
            context?.toast("বিস্তারিত ঠিকানা লিখুন, ন্যূনতম ১৫ ডিজিট")
            binding?.pickupAddress?.requestFocus()
            return false
        }
        pickUpLocation.pickupAddress = newPickUpAddress

        val pickupContact = binding?.pickupContact?.text?.toString() ?: ""
        if (pickupContact.length != 11) {
            context?.toast("সঠিক মোবাইল নাম্বার লিখুন")
            binding?.pickupContact?.requestFocus()
            return false
        }
        pickUpLocation.mobile = pickupContact

        return true
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}