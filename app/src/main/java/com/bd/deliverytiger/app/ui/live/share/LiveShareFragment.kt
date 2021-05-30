package  com.bd.deliverytiger.app.ui.live.share

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bd.deliverytiger.app.R
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.api.model.live.live_schedule_list.MyLiveSchedule
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSBody
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSRequest
import com.bd.deliverytiger.app.databinding.FragmentLiveShareBinding
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.wafflecopter.multicontactpicker.ContactResult
import com.wafflecopter.multicontactpicker.LimitColumn
import com.wafflecopter.multicontactpicker.MultiContactPicker
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.ArrayList
import kotlin.concurrent.thread

class LiveShareFragment() : BottomSheetDialogFragment() {

    private var binding: FragmentLiveShareBinding? = null
    private val viewModel: LiveShareViewModel by inject()

    var onShare: ((shareMsg: String, selectedNumberList: MutableList<String>) -> Unit)? = null

    private val requestCodeContact = 123
    private val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
    private val requestCodeContactPicker = 8221

    private val selectedContactList: MutableList<ContactResult> = mutableListOf()
    private val selectedNumberList: MutableList<String> = mutableListOf()

    private var shareMsg = ""
    private var isFreeSMSActive: Boolean = false
    private var shareSmsLimit: Int = 0
    private var sendSMSCount: Int = 0
    private var instantLive: Boolean = false

    private var liveSchedule: MyLiveSchedule = MyLiveSchedule()
    private lateinit var sessionManager: SessionManager

    companion object {
        fun newInstance(liveSchedule: MyLiveSchedule, instantLive: Boolean): LiveShareFragment = LiveShareFragment().apply {
            this.liveSchedule = liveSchedule
            this.instantLive = instantLive
        }
        val tag: String = LiveShareFragment::class.java.name
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
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parent?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                isHideable = false

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLiveShareBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager.init(requireContext())
        //liveSchedule = arguments?.getParcelable("model") ?: MyLiveSchedule()

        viewModel.fetchFreeSMSCondition().observe(viewLifecycleOwner, Observer { model->
            shareSmsLimit = model.freeSMSCount
            isFreeSMSActive = model.freeSMSActive
        })
        viewModel.fetchTotalSMSCount(liveSchedule.id).observe(viewLifecycleOwner, Observer {
            sendSMSCount = it
        })
        shareMsg = "লাইভে থাকুন আমার সাথে"

        if (!instantLive) {
            binding?.sendSocial?.isVisible = true
        }

        binding?.shareMessage?.setText(shareMsg)
        binding?.shareMessage?.setSelection(binding?.shareMessage?.text?.length ?: 0)

        binding?.addContactBtn?.setOnClickListener {
            if (isPermissions()) {
                pickupContact()
            }
        }

        binding?.receiverNumberLayout?.setEndIconOnClickListener {
            selectedContactList.clear()
            binding?.receiverNumber?.text?.clear()
        }

        binding?.sendSMS?.setOnClickListener {
            if (validation()) {
                shareLiveViaSMS()
            }
        }

        binding?.sendSocial?.setOnClickListener {
            shareLive()
        }
    }

    private fun validation(): Boolean {

        if (!isFreeSMSActive) {
            context?.toast("ফ্রি SMS সার্ভিস টি এই মুহূর্তে বন্ধ আছে")
            return false
        }

        val senderNumber = binding?.receiverNumber?.text?.toString() ?: ""
        if (senderNumber.isEmpty()) {
            context?.toast("শেয়ার মোবাইল নম্বর অ্যাড করুন")
            return false
        }

        val shareMessage = binding?.shareMessage?.text?.toString() ?: ""
        if (shareMessage.isEmpty()) {
            context?.toast("শেয়ার মেসেজ লিখুন")
            return false
        }

        if (selectedNumberList.size > shareSmsLimit-sendSMSCount) {
            context?.toast("আপনি আর সর্বোচ্চ ${DigitConverter.toBanglaDigit(shareSmsLimit-sendSMSCount)}টি sms পাঠাতে পারবেন")
            return false
        }

        return true
    }

    private fun shareLiveViaSMS() {

        //https://m.ajkerdeal.com/livevideoshopping/9999/01728959986

        val requestBody: MutableList<SMSBody> = mutableListOf()
        selectedNumberList.forEach { mobile ->
            val model = SMSBody(mobile, "$shareMsg https://m.ajkerdeal.com/livevideoshopping/${liveSchedule.id}/$mobile")
            requestBody.add(model)
        }
        val smsRequest = SMSRequest(
            liveSchedule.id ?: 0,
            requestBody.size,
            sessionManager.profileId,
            requestBody
        )

        if (!instantLive) {
            viewModel.shareSMS(smsRequest).observe(viewLifecycleOwner, Observer { flag ->
                if (flag) {
                    context?.toast("লাইভ শেয়ার sms পাঠানো হয়েছে")
                }
            })
        } else {
            onShare?.invoke(shareMsg, selectedNumberList)
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
            .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out)
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
                    binding?.receiverNumber?.setText(numbers)
                    //binding?.smsCount?.text = DigitConverter.toBanglaDigit
                    binding?.receiverLayout?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun isPermissions(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)

            return if (cameraPermission != PackageManager.PERMISSION_GRANTED) {

                val cameraPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_CONTACTS)
                if (cameraPermissionRationale) {
                    requestPermissions(permissions, requestCodeContact)
                } else {
                    requestPermissions(permissions, requestCodeContact)
                }
                false
            } else {
                true
            }
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeContact) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickupContact()
                } else {
                    alert("Permission Required", "App required Contact permission to function properly. Please grand permission.", true, "Give Permission", "Cancel") {
                        if (it == AlertDialog.BUTTON_POSITIVE) {
                            requestPermissions(permissions, requestCodeContact)
                        }
                    }.show()
                }
            }
        }
    }

    private fun shareLive() {

        val dialog = progressDialog("অপেক্ষা করুন, শেয়ার লিংক তৈরি হচ্ছে")
        dialog.show()

        val referralUrl = "https://m.ajkerdeal.com/livevideoshopping/${liveSchedule.id}/0"
        val uri = Uri.parse(referralUrl)
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = uri
            domainUriPrefix = AppConstant.DOMAIN_URL_PREFIX
            androidParameters {
                minimumVersion = 21
                fallbackUrl = uri
            }
            googleAnalyticsParameters {
                source = "AndroidApp"
            }
            socialMetaTagParameters {
                title = liveSchedule.liveTitle ?: "Live Video Shopping"
                description = "আজকেরডিল লাইভ ভিডিও শপিং"
                imageUrl = Uri.parse(liveSchedule.coverPhoto)
            }
            navigationInfoParameters {
                forcedRedirectEnabled = true
            }
            buildShortDynamicLink().addOnSuccessListener { shortDynamicLink ->
                dialog.dismiss()
                val shortLink = shortDynamicLink.shortLink
                val flowchartLink = shortDynamicLink.previewLink //flowchart link is a debugging URL
                Timber.d("createDynamicLink shortLink: $shortLink flowchartLink: $flowchartLink")

                generateShareContent(shortLink.toString())

            }.addOnFailureListener {
                dialog.dismiss()
                Timber.d(it)
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে")
            }
        }

    }

    private fun generateShareContent(url: String) {
        val msg = "$shareMsg\n$url"
        shareContent(msg)
    }

    private fun shareContent(msg: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, msg)
        }.also {
            activity?.startActivity(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            pickupContact()
        } else {
            binding?.parent?.snackbar("স্টোরেজ পারমিশন প্রয়োজন", Snackbar.LENGTH_INDEFINITE, "ঠিক আছে") {
                goToSetting(requireContext())
            }?.show()
        }
    }

}