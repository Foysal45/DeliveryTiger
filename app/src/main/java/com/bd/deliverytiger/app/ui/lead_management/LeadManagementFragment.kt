package com.bd.deliverytiger.app.ui.lead_management

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInfoRequest
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInformation
import com.bd.deliverytiger.app.api.model.lead_management.phonebook.PhonebookData
import com.bd.deliverytiger.app.api.model.voice_SMS.Message
import com.bd.deliverytiger.app.api.model.voice_SMS.VoiceSmsAudiRequestBody
import com.bd.deliverytiger.app.databinding.FragmentLeadManagementBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.lead_management.customer_details_bottomsheet.CustomerDetailsBottomSheet
import com.bd.deliverytiger.app.ui.lead_management.phonebook.PhonebookFormBottomSheet
import com.bd.deliverytiger.app.ui.lead_management.phonebook.PhonebookGroupBottomSheet
import com.bd.deliverytiger.app.ui.recorder.RecordBottomSheet
import com.bd.deliverytiger.app.ui.share.SmsShareDialogue
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.wafflecopter.multicontactpicker.ContactResult
import com.wafflecopter.multicontactpicker.LimitColumn
import com.wafflecopter.multicontactpicker.MultiContactPicker
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class LeadManagementFragment : Fragment() {

    private var binding: FragmentLeadManagementBinding? = null
    private val viewModel: LeadManagementViewModel by inject()

    private var dataAdapter: LeadManagementAdapter = LeadManagementAdapter()
    private var isLoading = false

    //private var totalProduct = 0
    private val visibleThreshold = 5

    //Pickup contact
    private val requestCodeContactPicker = 8221
    private val selectedContactList: MutableList<ContactResult> = mutableListOf()
    private val selectedNumberList: MutableList<String> = mutableListOf()
    private val selectedNameList: MutableList<String> = mutableListOf()
    private val selectedVoiceNumberList: MutableList<String> = mutableListOf()

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.lead_management))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentLeadManagementBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickLister()
    }

    private fun initView() {
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        fetchCustomerInformation(0)

        fetchBanner()

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
            }
        })

        viewModel.pagingState.observe(viewLifecycleOwner, Observer { state ->
            isLoading = false
            if (state.isInitLoad) {
                dataAdapter.initLoad(state.dataList)

                if (state.dataList.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                } else {
                    binding?.emptyView?.visibility = View.GONE
                }

            } else {
                dataAdapter.pagingLoad(state.dataList)
                if (state.dataList.isEmpty()) {
                    isLoading = true
                }
            }
        })

        binding?.recyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    if (!isLoading && currentItemCount <= lastVisibleItem + visibleThreshold) {
                        isLoading = true
                        fetchCustomerInformation(currentItemCount)
                    }
                }

            }
        })
    }

    private fun initClickLister() {

        dataAdapter.onItemClicked = { model, position ->
            dataAdapter.multipleSelection(model, position)
            binding?.sendSMSBtn?.isVisible = true
            binding?.sendVoiceSMSBtn?.isVisible = true
            binding?.clearBtn?.isVisible = true
            binding?.addToPhoneGroupBtn?.isVisible = true

            if (dataAdapter.getSelectedItemCount() == 0) {
                binding?.clearBtn?.performClick()
            }
        }

        dataAdapter.onOrderDetailsClicked = {model, position ->
            if (model.totalOrder > 0){
                goToCustomerDetailsBottomSheet(model.mobile ?: "")
            }
        }

        binding?.sendSMSBtn?.setOnClickListener {
            if (dataAdapter.getSelectedItemCount() > 0) {
                goToSmsSendBottomSheet(dataAdapter.getSelectedItemModelList())
            }
        }

        binding?.clearBtn?.setOnClickListener {
            dataAdapter.clearSelections()
            binding?.clearBtn?.isVisible = false
            binding?.sendSMSBtn?.isVisible = false
            binding?.sendVoiceSMSBtn?.isVisible = false
            binding?.addToPhoneGroupBtn?.isVisible = false
        }

        binding?.sendVoiceSMSBtn?.setOnClickListener {
            if (dataAdapter.getSelectedItemCount() > 0) {
                val selectedCustomerList = dataAdapter.getSelectedItemModelList()
                selectedNameList.clear()
                selectedNameList.addAll(selectedCustomerList.map { it.customerName ?: "" })
                selectedVoiceNumberList.clear()
                selectedVoiceNumberList.addAll(selectedCustomerList.map { "88" + it.mobile })
                goToRecordingBottomSheet()
            }
        }

        binding?.phoneBookImportBtn?.setOnClickListener {
            if (isContactPermissions()) {
                pickupContact()
            }
        }

        binding?.phoneBookFormBtn?.setOnClickListener {
            showPhonebookForm()
        }

        binding?.addToPhoneGroupBtn?.setOnClickListener {
            if (dataAdapter.getSelectedItemCount() > 0) {
                val selectedCustomerList = dataAdapter.getSelectedItemModelList()
                showPhonebookGroup(selectedCustomerList)
            } else {
                context?.toast("কমপক্ষে একজন কাস্টমার সিলেক্ট করুন")
            }
        }

    }

    private fun fetchCustomerInformation(index: Int) {
        viewModel.fetchCustomerList(CustomerInfoRequest(SessionManager.courierUserId, index, 20), index)
    }

    private fun goToCustomerDetailsBottomSheet(mobile: String) {
        val tag = CustomerDetailsBottomSheet.tag
        val dialog = CustomerDetailsBottomSheet.newInstance(mobile)
        dialog.show(childFragmentManager, tag)
    }

    private fun goToRecordingBottomSheet() {
        val tag = RecordBottomSheet.tag
        val dialog = RecordBottomSheet.newInstance()
        dialog.show(childFragmentManager, tag)
        dialog.onCancel = {
            binding?.clearBtn?.performClick()
        }
        dialog.onRecordingComplete = { audioPath ->
            dialog.dismiss()
            val names = selectedNameList.joinToString()
            val alert = alert("নির্দেশনা", "আপনার সিলেক্টেড কাস্টমার \n $names", true,
                "ঠিক আছে", "ক্যানসেল"
            ) {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    uploadAudio(
                        "aud_${SessionManager.courierUserId}_${Date().time}.mp3",
                        "audio/merchant",
                        audioPath
                    )
                    binding?.clearBtn?.performClick()
                }
            }
            alert.setCancelable(false)
            alert.show()
        }
    }

    private fun uploadAudio(fileName: String, audioPath: String, fileUrl: String) {

        val progressDialog = progressDialog()
        progressDialog.show()

        Timber.d("requestBody $fileName, $audioPath, $fileUrl")
        viewModel.audioUploadForFile(requireContext(), fileName, audioPath, fileUrl)
            .observe(viewLifecycleOwner, Observer { model ->
                progressDialog.hide()
                if (model) {
                    context?.toast("Uploaded successfully")
                    sendVoiceSMS("https://static.ajkerdeal.com/audio/merchant/$fileName")
                } else {
                    context?.toast("Uploaded Failed")
                }
            })
    }

    private fun sendVoiceSMS(audioPath: String) {
        if (selectedNameList.isNotEmpty()){
            val requestBody = VoiceSmsAudiRequestBody(
                listOf(Message(audioPath, "8804445650020", selectedVoiceNumberList))
            )
            viewModel.sendVoiceSms(requestBody).observe(viewLifecycleOwner, Observer {
                context?.toast("Voice SMS Send")
            })
        }
    }

    private fun goToSmsSendBottomSheet(model: List<CustomerInformation>) {
        val tag = SmsShareDialogue.tag
        val dialog = SmsShareDialogue.newInstance(model)
        dialog.show(childFragmentManager, tag)
        dialog.onSend = { isSend ->
            if (isSend) {
                dataAdapter.clearSelections()
            }
            dialog.dismiss()
        }
    }

    private fun fetchBanner() {
        val options = RequestOptions()
            .placeholder(R.drawable.ic_banner_place)
            .signature(ObjectKey(Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toString()))
        binding?.bannerImage?.let { image ->
            Glide.with(image)
                .load("https://static.ajkerdeal.com/images/merchant/chumbok_banner.jpg")
                .apply(options)
                .into(image)
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

    private fun showPhonebookGroup(selectedCustomerList: List<CustomerInformation>) {
        val dialog = PhonebookGroupBottomSheet.newInstance()
        val tag = PhonebookGroupBottomSheet.tag
        dialog.show(childFragmentManager, tag)
        dialog.onGroupSelected = { model ->
            dialog.dismiss()
            val requestBody: MutableList<PhonebookData> = mutableListOf()
            selectedCustomerList.forEach { customer ->
                requestBody.add(
                    PhonebookData(
                        SessionManager.courierUserId,
                        customer.mobile,
                        customer.companyName,
                        phoneBookGroupId = model.phoneBookGroupId
                    )
                )
            }
            viewModel.addToOwnPhoneBookGroup(requestBody).observe(viewLifecycleOwner, Observer { flag ->
                if (flag) {
                    context?.toast("ফোনবুক গ্রুপে অ্যাড হয়েছে")
                }
            })
        }
    }

    private fun showPhonebookForm() {
        val dialog = PhonebookFormBottomSheet.newInstance()
        val tag = PhonebookFormBottomSheet.tag
        dialog.show(childFragmentManager, tag)
        dialog.onSave = { model ->
            dialog.dismiss()
            val requestBody: MutableList<PhonebookData> = mutableListOf()
            requestBody.add(model)
            insertPhoneBookData(requestBody)
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}