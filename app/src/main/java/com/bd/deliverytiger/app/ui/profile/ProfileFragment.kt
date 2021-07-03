package com.bd.deliverytiger.app.ui.profile

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.district.AllDistrictListsModel
import com.bd.deliverytiger.app.api.model.location.LocationData
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.databinding.FragmentProfileBinding
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationSelectionDialog
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.district.v2.CustomModel
import com.bd.deliverytiger.app.ui.district.v2.DistrictThanaAriaSelectFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.ui.profile.pickup_address.PickUpLocationAdapter
import com.bd.deliverytiger.app.ui.profile.pickup_address.UpdatePickupLocationBottomSheet
import com.bd.deliverytiger.app.utils.*
import com.bd.deliverytiger.app.utils.VariousTask.getCircularImage
import com.bd.deliverytiger.app.utils.VariousTask.saveImage
import com.bd.deliverytiger.app.utils.VariousTask.scaledBitmapImage
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.util.*

@SuppressLint("SetTextI18n")
class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private val viewModel: ProfileViewModel by inject()
    private val homeViewModel: HomeViewModel by inject()

    private val districtList: MutableList<AllDistrictListsModel> = mutableListOf()
    private val thanaOrAriaList: MutableList<AllDistrictListsModel> = mutableListOf()
    private val GALLERY_REQ_CODE = 101

    private var districtId = 0
    private var thanaId = 0
    private var areaId = 0
    private var isAriaAvailable: Boolean = false
    private var districtName: String = ""
    private var thanaName: String = ""
    private var areaName: String = ""

    private var companyName = ""
    private var contactPersonName = ""
    private var mobileNumber = ""
    private var alternativeNumber = ""
    private var bkashMobileNumber = ""
    private var webLink = ""
    private var fbLink = ""
    private var collectionAddress = ""
    private var emailAddress = ""
    private var newPickUpAddress: String = ""

    private var isPickupLocationAvailable = false
    private var isPickupLocationSelected = false
    private var isAddingNewLocation = false
    private var selectedPickupLocation: PickupLocation? = null

    private lateinit var pickupAddressAdapter: PickUpLocationAdapter

    private var isGPSRetry: Boolean = false
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private var isPickupLocation: Boolean = false
    private var isFromOrderPlace: Boolean = false
    private var isPickupLocationEmpty: Boolean = false

    companion object {
        fun newInstance(isPickupLocation: Boolean = false, isFromOrderPlace: Boolean = false): ProfileFragment = ProfileFragment().apply {
            this.isPickupLocation = isPickupLocation
            this.isFromOrderPlace = isFromOrderPlace
        }
        val tag: String = ProfileFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentProfileBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("প্রোফাইল")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpProfileFromSession()
        getPickupLocation()

        binding?.profilePic?.setOnClickListener {
            getImageFromDevice()
        }
        binding?.editProfilePic?.setOnClickListener {
            binding?.profilePic?.performClick()
        }
        /*binding?.districtSelect?.setOnClickListener {
            if (districtList.isEmpty()) {
                getDistrictThanaOrAria(0, 1)
            } else {
                goToDistrict()
            }
        }*/
        binding?.thanaSelect?.setOnClickListener {
            if (districtId != 0) {
                getDistrictThanaOrAria(districtId, 2)
            } else {
                context?.toast(getString(R.string.select_dist))
            }
        }
        binding?.areaSelect?.setOnClickListener {
            if (isAriaAvailable) {
                if (thanaId != 0) {
                    getDistrictThanaOrAria(thanaId, 3)
                } else {
                    context?.toast(getString(R.string.select_thana))
                }
            } else {
                context?.toast(getString(R.string.no_aria))
            }
        }
        binding?.saveBtn?.setOnClickListener {
            if (validate()) {
                updateProfile()
            }
        }
        binding?.addPickupBtn?.setOnClickListener {
            addPickupAddress()
        }
        binding?.gpsBtn?.setOnClickListener {
            alert("নির্দেশনা", "আপনি কি এখন পিকআপ ঠিকানায় আছেন? যদি পিকআপ ঠিকানায় অবস্থান করেন তাহলে জিপিএস লোকেশন অ্যাড করুন অন্যথায় পিকআপ ঠিকানায় গিয়ে জিপিএস লোকেশন অ্যাড করুন", true,"পিকআপ ঠিকানায় আছি", "ক্যানসেল") {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    getGPSLocation()
                }
            }.show()
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
                is ViewState.NextState -> {
                    activity?.onBackPressed()
                }
            }
        })


    }

    @SuppressLint("SetTextI18n")
    private fun getGPSLocation() {
        (activity as HomeActivity).fetchCurrentLocation()
        homeViewModel.currentLocation.observe(viewLifecycleOwner, Observer { location ->
            location ?: return@Observer
            currentLatitude = location.latitude
            currentLongitude = location.longitude
            binding?.gpsLocation?.setText("$currentLatitude, $currentLongitude")
            if (isGPSRetry) {
                context?.toast("জিপিএস লোকেশন আপডেট হয়েছে")
            }
            isGPSRetry = true
            homeViewModel.currentLocation.value = null
        })
    }

    private fun getDistrictThanaOrAria(id: Int, track: Int) {
        hideKeyboard()
        //track = 1 district , track = 2 thana, track = 3 aria
        val dialog = progressDialog()
        dialog.show()
        viewModel.loadAllDistrictsById(id).observe(viewLifecycleOwner, Observer { list ->
            dialog.dismiss()
            when (track) {
                1 -> {
                    districtList.clear()
                    districtList.addAll(list)
                    goToLocationSelectionDialog(districtList, 1)
                }
                2 -> {
                    thanaOrAriaList.clear()
                    thanaOrAriaList.addAll(list)
                    goToLocationSelectionDialog(thanaOrAriaList, 2)
                }
                3 -> {
                    thanaOrAriaList.clear()
                    thanaOrAriaList.addAll(list)
                    goToLocationSelectionDialog(thanaOrAriaList, 3)
                }
            }
        })
    }

    private fun goToLocationSelectionDialog(list: MutableList<AllDistrictListsModel>, operationFlag: Int) {

        val locationList: MutableList<LocationData> = mutableListOf()
        list.forEach { model ->
            locationList.add(LocationData.from(model))
        }

        val dialog = LocationSelectionDialog.newInstance(locationList)
        dialog.show(childFragmentManager, LocationSelectionDialog.tag)
        dialog.onLocationPicked = { _, model ->
            when (operationFlag) {
                1 -> {
                    districtId = model.id
                    thanaId = 0
                    areaId = 0
                    binding?.districtSelect?.setText(model.displayNameBangla)
                    districtName = model.displayNameBangla ?: ""

                    binding?.thanaSelect?.setText("")
                    binding?.areaSelect?.setText("")
                    binding?.areaSelect?.visibility = View.GONE
                }
                2 -> {
                    thanaId = model.id
                    thanaName = model.displayNameBangla ?: ""
                    binding?.thanaSelect?.setText(model.displayNameBangla)
                    areaId = 0
                }
                3 -> {
                    areaId = model.id
                    areaName = model.displayNameBangla ?: ""
                    binding?.areaSelect?.setText(areaName)
                }
            }
        }
    }

    private fun validate(): Boolean {
        hideKeyboard()

        companyName = binding?.companyName?.text?.trim().toString()
        if (companyName.isEmpty()) {
            context?.toast("মার্চেন্ট/কোম্পানি নাম লিখুন")
            binding?.companyName?.requestFocus()
            return false
        }

        /* if (!isExistSpecialCharacter(companyName)) {
            context?.toast("সঠিক মার্চেন্ট/কোম্পানি  নাম লিখুন")
            binding?.companyName?.requestFocus()
            return false
        }*/

        contactPersonName = binding?.contactPersonName?.text?.trim().toString()
        if (contactPersonName.isEmpty()) {
            context?.toast("কন্টাক্ট পারসনের নাম লিখুন")
            binding?.contactPersonName?.requestFocus()
            return false
        }

        mobileNumber = binding?.mobileNumber?.text?.trim().toString()
        if (mobileNumber.isEmpty()) {
            context?.toast(getString(R.string.write_phone_number))
            binding?.mobileNumber?.requestFocus()
            return false
        }
        if (!Validator.isValidMobileNumber(mobileNumber) || mobileNumber.length < 11) {
            context?.toast(getString(R.string.write_proper_phone_number_recharge))
            binding?.mobileNumber?.requestFocus()
            return false
        }

        alternativeNumber = binding?.alternateMobileNumber?.text?.trim().toString()
        if (alternativeNumber.isNotEmpty() && alternativeNumber.length < 11) {
            context?.toast("সঠিক বিকল্প ফোন নাম্বার লিখুন")
            binding?.alternateMobileNumber?.requestFocus()
            return false
        }

        bkashMobileNumber = binding?.bkashNumber?.text?.trim().toString()
        if (bkashMobileNumber.isEmpty()) {
            context?.toast("সঠিক বিকাশ নাম্বার লিখুন")
            binding?.bkashNumber?.requestFocus()
            return false
        }
        if (!Validator.isValidMobileNumber(bkashMobileNumber) || bkashMobileNumber.length < 11) {
            context?.toast(getString(R.string.write_proper_phone_number_recharge))
            binding?.bkashNumber?.requestFocus()
            return false
        }

        emailAddress = binding?.emailAddress?.text?.trim().toString()
        if (emailAddress.isEmpty()) {
            context?.toast(getString(R.string.write_email_address))
            binding?.emailAddress?.requestFocus()
            return false
        }

        webLink = binding?.pageLink?.text?.trim().toString()
        fbLink = binding?.fbLink?.text?.trim().toString()

        if (!isValidFacebookPage(fbLink)) {
            context?.toast("সঠিক ফেইসবুক পেইজ লিংক দিন")
            binding?.emailAddress?.requestFocus()
            return false
        }

        /*if (!isAddingNewLocation && !isPickupLocationSelected) {
            context?.toast("কালেকশন লোকেশন সিলেক্ট করুন")
            return false
        }*/

        collectionAddress = binding?.collectionAddress?.text?.trim().toString()
        /*if (collectionAddress.isEmpty() || collectionAddress.length < 15) {
            context?.toast("বিস্তারিত কালেকশন ঠিকানা লিখুন, ন্যূনতম ১৫ ডিজিট")
            binding?.collectionAddress?.requestFocus()
            return false
        }*/

        /*
        if (isAriaAvailable && areaId == 0) {
            context?.toast(getString(R.string.select_aria))
            return false
        }*/

        if (!SessionManager.isPickupLocationAdded) {
            context?.toast("কমপক্ষে একটি পিকআপ লোকেশন অ্যাড করুন")
            binding?.nestedScrollView?.fullScroll(View.FOCUS_DOWN)
            return false
        }

        return true
    }

    private fun addPickupAddress() {

        if (districtId == 0) {
            context?.toast(getString(R.string.select_dist))
            return
        }
        if (thanaId == 0) {
            context?.toast(getString(R.string.select_thana))
            return
        }

        newPickUpAddress = binding?.pickupAddress?.text?.toString() ?: ""
        if (newPickUpAddress.trim().isEmpty() || newPickUpAddress.length < 15) {
            context?.toast("বিস্তারিত ঠিকানা লিখুন, ন্যূনতম ১৫ ডিজিট")
            binding?.pickupAddress?.requestFocus()
            return
        }

        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            context?.toast("জিপিএস লোকেশন অ্যাড করুন")
            return
        }

        val requestBody = PickupLocation().apply {
            districtId = this@ProfileFragment.districtId
            thanaId = this@ProfileFragment.thanaId
            courierUserId = SessionManager.courierUserId
            pickupAddress = newPickUpAddress
            districtName = this@ProfileFragment.districtName
            thanaName = this@ProfileFragment.thanaName
            latitude = currentLatitude.toString()
            longitude = currentLongitude.toString()
        }
        viewModel.addPickupLocations(requestBody).observe(viewLifecycleOwner, Observer { model ->
            //pickupAddressAdapter.addItem(requestBody)
            getPickupLocation()
            context?.toast("পিকআপ লোকেশন অ্যাড হয়েছে")

            binding?.spinnerPickUpDistrict?.setSelection(0)
            binding?.pickupAddress?.text?.clear()
            binding?.gpsLocation?.text?.clear()
        })

    }

    private fun updateProfile() {

        val requestBody = ProfileUpdateReqBody(
            SessionManager.courierUserId,
            companyName,
            contactPersonName,
            mobileNumber,
            alternativeNumber,
            emailAddress,
            bkashMobileNumber,
            collectionAddress,
            binding?.checkSmsUpdate?.isChecked ?: true,
            fbLink,
            webLink,
            districtId, thanaId, areaId,
            districtName,
            thanaName,
            areaName
        )

        viewModel.updateMerchantInformation(SessionManager.courierUserId, requestBody).observe(viewLifecycleOwner, Observer {
            SessionManager.createSession(it)
            if (isFromOrderPlace) {
                homeViewModel.refreshEvent.value = "OrderPlace"
            }
        })

    }

    private fun setUpProfileFromSession() {

        val model = SessionManager.getSessionData()
        binding?.companyName?.setText(model.companyName)
        binding?.contactPersonName?.setText(model.userName)
        binding?.mobileNumber?.setText(model.mobile)
        binding?.alternateMobileNumber?.setText(model.alterMobile)
        binding?.bkashNumber?.setText(model.bkashNumber)
        binding?.emailAddress?.setText(model.emailAddress)
        collectionAddress = model.address ?: ""
        binding?.collectionAddress?.setText(model.address)
        binding?.checkSmsUpdate?.isChecked = model.isSms
        binding?.fbLink?.setText(model.fburl)
        binding?.pageLink?.setText(model.webURL)

        //binding?.districtSelect?.setText(model.districtName)
        binding?.districtSelect?.setText("ঢাকা সিটি")
        //binding?.thanaSelect?.setText(model.thanaName)
        //binding?.areaSelect?.setText(model.areaName)
        districtId = 14
        //districtId = model.districtId // Now only in dhaka city available
        //thanaId = model.thanaId
        //areaId = model.areaId

        if (!model.companyName.isNullOrEmpty()) {
            binding?.companyName?.isClickable = false
            binding?.companyName?.isFocusable = false
        }
        if (!model.emailAddress.isNullOrEmpty()) {
            binding?.emailAddress?.isClickable = false
            binding?.emailAddress?.isFocusable = false
        }
        if (!model.mobile.isNullOrEmpty()) {
            binding?.mobileNumber?.isClickable = false
            binding?.mobileNumber?.isFocusable = false
        }
        if (!model.bkashNumber.isNullOrEmpty()) {
            binding?.bkashNumber?.isClickable = false
            binding?.bkashNumber?.isFocusable = false
        }
        /*if (areaId > 0) {
            binding?.areaSelect?.visibility = View.VISIBLE
        }*/

        if (SessionManager.profileImgUri.isNotEmpty()) {
            setProfileImgUrl(SessionManager.profileImgUri)
        }

        pickupAddressAdapter = PickUpLocationAdapter()
        pickupAddressAdapter.showEdit = true
        binding?.recyclerview?.let { view ->
            with(view) {
                setHasFixedSize(false)
                isNestedScrollingEnabled = false
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pickupAddressAdapter
            }
        }
        pickupAddressAdapter.onEditClicked = {model ->
            val tag = UpdatePickupLocationBottomSheet.tag
            val dialog = UpdatePickupLocationBottomSheet.newInstance(model)
            dialog.show(childFragmentManager, tag)
            dialog.onUpdateClicked = { model ->
                dialog.dismiss()
                context?.toast("সফলভাবে পিকআপ লোকেশন আপডেট হয়েছে")
                getPickupLocation()
            }
        }
        pickupAddressAdapter.onDeleteClicked = { model ->
            alert("নির্দেশনা", "পিকআপ লোকেশন ডিলিট করতে চান?", true,"হ্যাঁ, ডিলিট করবো", "ক্যানসেল") {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    viewModel.deletePickupLocations(model).observe(viewLifecycleOwner, Observer {
                        if (it) {
                            context?.toast("সফলভাবে ডিলিট হয়েছে")
                            getPickupLocation()
                        }
                    })
                }
            }.show()
        }

        setUpPickupDistrict()
    }

    private fun setProfileImgUrl(imageUri: String?) {
        //Timber.d("HomeActivityLog 1 ", SessionManager.profileImgUri)
        try {
            val imgFile = File(imageUri + "");
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                binding?.profilePic?.setImageDrawable(getCircularImage(context, myBitmap))
                //Timber.d("HomeActivityLog 2 ", myBitmap.allocationByteCount.toString()+" "+ myBitmap.byteCount.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE) {
            val imageStream: InputStream? = activity?.contentResolver?.openInputStream(Uri.parse(data?.data.toString()))
            imageStream?.let {
                val scImg = scaledBitmapImage(BitmapFactory.decodeStream(imageStream))
                binding?.profilePic?.setImageDrawable(getCircularImage(context, scImg))
                SessionManager.profileImgUri = saveImage(scImg)
            }
        }
    }

    private fun getImageFromDevice() {
        if (ContextCompat.checkSelfPermission(requireContext(), permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE), 102)
        } else {
            pickupGallery()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 102) {
            if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                pickupGallery()
            }
        }
    }

    private fun pickupGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, GALLERY_REQ_CODE)
    }

    private fun getPickupLocation() {

        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                //setUpCollectionSpinner(list, null, 1)
                pickupAddressAdapter.initList(list)
                binding?.emptyView?.visibility = View.GONE
                SessionManager.isPickupLocationAdded = true
                isPickupLocationEmpty = false
            } else {
                //getDistrictThanaOrAria(14, 4)
                binding?.emptyView?.visibility = View.VISIBLE
                SessionManager.isPickupLocationAdded = false
                isPickupLocationEmpty = true
            }

            if (isPickupLocation) {
                binding?.nestedScrollView?.fullScroll(View.FOCUS_DOWN)
            }
        })
    }

    private fun setUpPickupDistrict() {

        viewModel.loadAllDistrictsById(0).observe(viewLifecycleOwner, Observer { list->

            val filterList = list.filter { it.isPickupLocation }
            val pickupDistrictList: MutableList<String> = mutableListOf()
            pickupDistrictList.add("জেলা নির্বাচন করুন")
            filterList.forEach {
                pickupDistrictList.add(it.districtBng ?: "")
            }

            val pickupDistrictAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupDistrictList)
            binding?.spinnerPickUpDistrict?.adapter = pickupDistrictAdapter
            binding?.spinnerPickUpDistrict?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        val model = filterList[position-1]
                        districtId = model.districtId
                        districtName = model.districtBng ?: ""
                        binding?.thanaSelect?.setText("থানা/এরিয়া নির্বাচন করুন")
                    } else {
                        districtId = 0
                        districtName = ""
                        binding?.thanaSelect?.setText("থানা/এরিয়া নির্বাচন করুন")
                    }
                }
            }

        })

    }

    /*private fun setUpCollectionSpinner(pickupParentList: List<PickupLocation>?, thanaOrAriaList: List<ThanaPayLoad>?, optionFlag: Int) {

        val pickupList: MutableList<String> = mutableListOf()
        pickupList.add("কালেকশন লোকেশন")
        if (optionFlag == 1) {
            pickupParentList?.forEach {
                pickupList.add(it.thanaName ?: "")
            }
        } else if (optionFlag == 2) {
            thanaOrAriaList?.forEach {
                pickupList.add(it.thanaBng ?: "")
            }

        }

        val pickupAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupList)
        binding?.spinnerCollectionLocation?.adapter = pickupAdapter
        binding?.spinnerCollectionLocation?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (position != 0) {
                    if (optionFlag == 1) {
                        val model = pickupParentList!![position-1]
                        collectionAddress = model.pickupAddress ?: ""
                        binding?.collectionAddress?.setText(collectionAddress)
                        //districtId = model.districtId
                        districtId = 14
                        thanaId = model.thanaId

                        selectedPickupLocation = model
                    } else if (optionFlag == 2) {
                        val model = thanaOrAriaList!![position-1]
                        collectionAddress = ""
                        binding?.collectionAddress?.setText(collectionAddress)
                        districtId = 14
                        thanaId = model.thanaId
                    }
                    isPickupLocationSelected = true
                } else {
                    isPickupLocationSelected = false
                }
            }
        }
        if (optionFlag == 1 && pickupList.size >= 2) {
            binding?.spinnerCollectionLocation?.setSelection(1)
        }
        isPickupLocationAvailable = true

    }*/
}
