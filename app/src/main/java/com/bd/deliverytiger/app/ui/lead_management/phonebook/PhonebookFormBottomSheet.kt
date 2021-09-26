package com.bd.deliverytiger.app.ui.lead_management.phonebook

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInfoRequest
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookData
import com.bd.deliverytiger.app.databinding.FragmentPhonebookFormBinding
import com.bd.deliverytiger.app.ui.lead_management.LeadManagementViewModel
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflecopter.multicontactpicker.ContactResult
import com.wafflecopter.multicontactpicker.LimitColumn
import com.wafflecopter.multicontactpicker.MultiContactPicker
import org.koin.android.ext.android.inject
import java.util.ArrayList
import kotlin.concurrent.thread

class PhonebookFormBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentPhonebookFormBinding? = null
    private val viewModel: LeadManagementViewModel by inject()

    //Pickup contact
    private val requestCodeContactPicker = 8221
    private val selectedContactList: MutableList<ContactResult> = mutableListOf()
    private val selectedNumberList: MutableList<String> = mutableListOf()
    private val selectedNameList: MutableList<String> = mutableListOf()
    private val selectedVoiceNumberList: MutableList<String> = mutableListOf()

    var onSave: ((model: PhonebookData) -> Unit)? = null

    companion object {
        fun newInstance(): PhonebookFormBottomSheet = PhonebookFormBottomSheet().apply {

        }

        val tag: String = PhonebookFormBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme1)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentPhonebookFormBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListener()
    }

    private fun initClickListener() {

        binding?.saveBtn?.setOnClickListener {
            if (validation()) {
                save()
            }
        }

        binding?.phoneBookImportBtn?.setOnClickListener {
            if (isContactPermissions()) {
                pickupContact()
            }
        }
    }

    private fun save() {
        val name = binding?.customerNameET?.text?.toString() ?: ""
        val mobile = binding?.customerPhoneET?.text?.toString() ?: ""
        onSave?.invoke(PhonebookData(
            SessionManager.courierUserId,
            mobile,
            name
        ))
    }

    private fun validation(): Boolean {

        hideKeyboard()
        val name = binding?.customerNameET?.text?.toString() ?: ""
        val mobile = binding?.customerPhoneET?.text?.toString() ?: ""

        if (name.isEmpty()) {
            context?.toast("কাস্টমার নাম লিখুন")
            return false
        }

        if (mobile.isEmpty()) {
            context?.toast("কাস্টমার মোবাইল নম্বর লিখুন")
            return false
        }

        if (mobile.length != 11) {
            context?.toast("কাস্টমার সঠিক মোবাইল নম্বর লিখুন")
            return false
        }

        return true
    }

    private fun isContactPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            return if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (storagePermissionRationale) {
                    contactPermission.launch(Manifest.permission.READ_CONTACTS)
                } else {
                    contactPermission.launch(Manifest.permission.READ_CONTACTS)
                }
                false
            } else {
                true
            }
        } else {
            return true
        }
    }

    private val contactPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission ->
        if (hasPermission) {
            pickupContact()
        }
    }

    private fun pickupContact() {

        MultiContactPicker.Builder(this)
                .theme(R.style.MyCustomPickerTheme)
                .hideScrollbar(false)
                .showTrack(true)
                .searchIconColor(Color.WHITE)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .handleColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                .bubbleColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                .bubbleTextColor(Color.WHITE)
                .setTitleText("Select Contacts")
                .setSelectedContacts(selectedContactList as ArrayList<ContactResult>)
                .setLoadingType(MultiContactPicker.LOAD_SYNC)
                .limitToColumn(LimitColumn.PHONE)
                .setActivityAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .showPickerForResult(requestCodeContactPicker)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestCodeContactPicker -> {
                if (resultCode == Activity.RESULT_OK) {
                    val contactList: MutableList<ContactResult> = MultiContactPicker.obtainResult(data)
                    //val contact = contactList.first()
                    //Timber.d("contactDebug ${contact.contactID} ${contact.displayName} ${contact.emails} ${contact.isStarred} ${contact.photo} ${contact.thumbnail} ${contact.phoneNumbers.first().typeLabel} ${contact.phoneNumbers.first().number}")
                    selectedContactList.clear()
                    selectedNumberList.clear()
                    selectedContactList.addAll(contactList)
                    var numbers = ""
                    selectedContactList.forEach {
                        val number = it.phoneNumbers.first().number
                        val modNumber = cleanPhoneNumber(number)
                        selectedNumberList.add(modNumber)
                        numbers += "$modNumber,"
                    }

                    val requestBody: MutableList<PhonebookData> = mutableListOf()
                    contactList.forEach {
                        val number = it.phoneNumbers.first().number
                        val modNumber = cleanPhoneNumber(number)
                        val name = it.displayName
                        val model = PhonebookData(
                                SessionManager.courierUserId,
                                modNumber,
                                name
                        )
                        requestBody.add(model)
                    }
                    insertPhoneBookData(requestBody)
                }
            }
        }
    }

    private fun insertPhoneBookData(requestBody: List<PhonebookData>) {
        viewModel.addToOwnPhoneBook(requestBody).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                context?.toast("সফলভাবে অ্যাড হয়েছে")
                fetchCustomerInformation(0)
            }
        })
    }

    private fun fetchCustomerInformation(index: Int) {
        viewModel.fetchCustomerList(CustomerInfoRequest(SessionManager.courierUserId, index, 20), index)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}