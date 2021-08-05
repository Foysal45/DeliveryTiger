package com.bd.deliverytiger.app.ui.live.live_schedule

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.provider.Settings
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.DateData
import com.bd.deliverytiger.app.api.model.live.auth.SignUpNew
import com.bd.deliverytiger.app.api.model.live.live_schedule.PriceTemp
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleData
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule_insert.LiveScheduleInsertRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule_list.MyLiveSchedule
import com.bd.deliverytiger.app.api.model.live.live_status.LiveStatusUpdateRequest
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSBody
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSRequest
import com.bd.deliverytiger.app.databinding.FragmentLiveScheduleBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.live.share.LiveShareFragment
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_live_schedule.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LiveScheduleFragment(): Fragment() {

    private var binding: FragmentLiveScheduleBinding? = null
    private val viewModel: LiveScheduleViewModel by inject()
    private lateinit var priceAdapter: PriceAdapter

    private lateinit var timeAdapter: ScheduleTimeAdapter
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val sdf1 = SimpleDateFormat("HH:mm:ss", Locale.US)

    private var liveDate: String = ""
    private var scheduleId: Int = 0
    private var fromTime: String = ""
    private var toTime: String = ""
    private var liveId: Int = 0
    private var liveTitle: String = ""
    private var coverUrl: String = ""
    private var facebookVideoUrl: String = ""
    private var videoId: String = ""

    private var priceRange: String = ""
    private var paymentMode = "both"

    private var redirectToFB: Boolean = true
    private var isShowMobile: Boolean = true
    private var ownNumber: String = ""
    private var ownAlternateNumber: String = ""

    private var isFacebook: Boolean = false
    private var isYoutube: Boolean = false
    private var fbPageUrl: String = ""
    private var fbStreamUrl: String = ""
    private var fbStreamKey: String = ""
    private var youtubeStreamUrl: String = ""
    private var youtubeStreamKey: String = ""

    private var selectedNumberList: MutableList<String> = mutableListOf()
    private var liveShareMsg: String = ""

    private var instantLive: Boolean = false
    private var listPosition: Int = -1

    companion object {
        fun newInstance(): LiveScheduleFragment = LiveScheduleFragment()
        val tag: String = LiveScheduleFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLiveScheduleBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instantLive = activity?.intent?.getBooleanExtra("instantLive", false) ?: false

        if (instantLive) {
            binding?.uploadBtn?.text = "ফেসবুক ভিডিও শেয়ার করুন"
            (activity as LiveScheduleActivity).updateToolbarTitle("ভিডিও শেয়ার")
            findNavController().currentDestination?.label = "ভিডিও শেয়ার"
        } else {
            binding?.uploadBtn?.text = "শিডিউল সাবমিট করুন"
            (activity as LiveScheduleActivity).updateToolbarTitle("লাইভ শিডিউল")
            findNavController().currentDestination?.label = "লাইভ শিডিউল"
        }
        initClickLister()
        ownNumber = SessionManager.mobile
        ownAlternateNumber = SessionManager.alterMobile
        //ToDo: remove after test
        if (BuildConfig.DEBUG) {
            //testForm()
        }
    }

    private fun initClickLister() {

        binding?.coverUploadBtn?.setOnClickListener {
            pickUpImage()
        }

        binding?.uploadBtn?.setOnClickListener {
            if (instantLive) {
                instantScheduleTime()
            }
            if (validation()) {
                if (instantLive) {
                    val titleText = "নির্দেশনা"
                    val descriptionText = "আপনি ফেসবুকের ভিডিও শেয়ার করতে যাচ্ছেন।"
                    val noBtnText = "ক্যানসেল"
                    val yesBtnText = "শেয়ার করুন"

                    customAlert(titleText, descriptionText, noBtnText, yesBtnText) {
                        if (it == 1) {
                            insertSchedule()
                        }
                    }
                } else {
                    //insertSchedule()
                }
            }
        }

        binding?.addContactBtn?.setOnClickListener {
            val dialog = LiveShareFragment.newInstance(MyLiveSchedule(), instantLive)
            dialog.show(childFragmentManager, LiveShareFragment.tag)
            dialog.onShare = { shareMsg, numberList ->
                liveShareMsg = shareMsg
                selectedNumberList.clear()
                selectedNumberList.addAll(numberList)
                dialog.dismiss()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().onBackPressed()
            }
        })

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
    }

    private fun instantScheduleTime() {
        scheduleId = 0
        liveDate = sdf.format(Date().time)
        fromTime = sdf1.format(Date().time)
        toTime = sdf1.format(Date().time + (33*60*1000L))
    }

    private fun insertSchedule() {
        //var userId = 328702
        val userId =  SessionManager.channelId

        val model = LiveScheduleInsertRequest(
            liveDate, fromTime, toTime,
            userId, "customer", 1,
            userId, scheduleId, liveTitle,
            priceRange, paymentMode, redirectToFB, isShowMobile, ownNumber, ownAlternateNumber,
            fbPageUrl, isFacebook, fbStreamUrl, fbStreamKey,
            isYoutube, youtubeStreamUrl, youtubeStreamKey, if (instantLive) 1 else 0,
            facebookVideoUrl, videoId, "dt-android-${appVersion()}"
        )
        Timber.d("requestBody $model")

        viewModel.insertLiveSchedule(model).observe(viewLifecycleOwner, Observer { id ->
            if (id == -1) {
                if (instantLive) {
                    context?.toast("এই মুহূর্তে সার্ভারের কোনো সমস্যা হচ্ছে, অনুগ্রহপূর্বক একটু পর আবার চেষ্টা করুন।")
                } else {
                    context?.toast("কারেন্ট লাইভ শিডিউল খালি নেই, অন্য শিডিউল সিলেক্ট করুন")
                }
            } else {
                if (instantLive) UserLogger.logGenie("LiveSchedule_Live_Instant")
                else UserLogger.logGenie("LiveSchedule_Live_Later")
                if (isFacebook) UserLogger.logGenie("LiveStream_Facebook")
                if (isYoutube) UserLogger.logGenie("LiveStream_Youtube")
                liveId = id
                Timber.tag("LiveScheduleFragment").d("insertLiveSchedule with $liveId")
                insertLiveCover()
                updateLiveStatus()
            }
        })
    }

    private fun insertLiveCover() {
        val progressDialog = progressDialog("ভিডিও শেয়ার হচ্ছে, অপেক্ষা করুন")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        viewModel.uploadLiveCoverPhoto(requireContext(), liveId, coverUrl).observe(viewLifecycleOwner, Observer { flag ->
            if (flag) {
                Timber.tag("LiveScheduleFragment").d("Video cover upload")
                progressDialog.dismiss()
                //showCompleteDialog(liveId)
                if (instantLive && selectedNumberList.isNotEmpty()) {
                    val requestBody: MutableList<SMSBody> = mutableListOf()
                    selectedNumberList.forEach { mobile ->
                        val model = SMSBody(mobile, "$liveShareMsg https://m.ajkerdeal.com/livevideoshopping/$liveId/$mobile")
                        requestBody.add(model)
                    }
                    val smsRequest = SMSRequest(
                        liveId,
                        requestBody.size,
                        SessionManager.courierUserId,
                        requestBody
                    )
                    viewModel.shareSMS(smsRequest).observe(viewLifecycleOwner, Observer { flag ->
                        if (flag) {
                            context?.toast("ভিডিও শেয়ার sms পাঠানো হয়েছে")
                        }
                    })
                }

                val intent = Intent().apply {
                    putExtra("instantLive", instantLive)
                    putExtra("liveId", liveId)
                }
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }
        })
    }

    private fun updateLiveStatus() {
        val status = "replay"
        val requestBody = LiveStatusUpdateRequest(liveId, status, "", facebookVideoUrl)
        viewModel.updateLiveStatus(requestBody).observe(viewLifecycleOwner, Observer { flag ->
        })
    }

    private fun showCompleteDialog(liveId: Int) {
        val tag = LiveScheduleBottomSheet.tag
        val dialog = LiveScheduleBottomSheet.newInstance()
        dialog.show(childFragmentManager, tag)
        dialog.onActionClicked = { flag ->
            dialog.dismiss()
            when(flag) {
                1 -> {
                    findNavController().navigate(R.id.nav_live_schedule_list)
                }
                2 -> {
                    val bundle = bundleOf(
                        "liveId" to liveId,
                        "suggestedPrice" to priceRange
                    )
                    //Todo: Remove Cmnt 1
                    //findNavController().navigate(R.id.nav_live_product_add, bundle)
                }
            }
        }
    }

    private fun validation(): Boolean {

        if (SessionManager.channelId == 0) {
            createChannelId()
            context?.toast("Creating new channel")
            return false
        }

        hideKeyboard()
        facebookVideoUrl = binding?.fbVideoUrl?.text?.toString()?.trim() ?: ""
        liveTitle = binding?.titleName?.text?.toString()?.trim() ?: ""

        if (liveTitle.isEmpty()) {
            context?.toast("ভিডিও টাইটেল লিখুন")
            return false
        }

        if (coverUrl.isEmpty()) {
            context?.toast("ভিডিও কভার ছবি যোগ করুন")
            return false
        }

        if (facebookVideoUrl.isEmpty()) {
            context?.toast("ফেসবুক ভিডিও লিংক লিখুন")
            return false
        } else {
            if (!validateUrl()) {
                context?.toast("Link must Contain: \"https://www.facebook.com/\"")
                return false
            }
        }

        priceRange = ""
        Timber.d("LiveProductPriceList $priceRange")

        if (fromTime.isEmpty() || toTime.isEmpty()) {
            context?.toast("সিলেক্ট লাইভ টাইম")
            return false
        }

        return true
    }

    private fun createChannelId() {
        //TODO make it dynamic
        var mobile = SessionManager.mobile
        //mobile = "01676100969"

        val requestBody = SignUpNew(
            SessionManager.deviceId, SessionManager.firebaseToken, "", 0, "",
            "", "", 0, mobile, SessionManager.companyName, mobile, 4
        )

        viewModel.signUpForLivePlaza(requestBody).observe(viewLifecycleOwner, Observer {
            if ( it.id != 0 ) {
                SessionManager.channelId = it.id
                context?.toast("Channel created with your Phone number as password")
                binding?.uploadBtn?.performClick()
            }
        })
    }

    private fun validateUrl(): Boolean {
        //Sample https://www.facebook.com/332656306807004/videos/188025879796045
        Timber.d("URL $facebookVideoUrl")

        if (facebookVideoUrl.isNotEmpty()) {
            val uri = Uri.parse(facebookVideoUrl)
            if (facebookVideoUrl.contains("https://www.facebook.com/")) {
                Timber.d("URL ${uri.scheme} ${uri.host} ${uri.lastPathSegment}${uri.pathSegments}")
                if (uri.host == "www.facebook.com") {
                    val segments = uri.pathSegments
                    if (!segments.isNullOrEmpty()) {
                        if (segments.size == 3) {
                            videoId = segments[2]
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun testForm() {
        fbStreamUrl = "rtmps://live-api-s.facebook.com:443/rtmp/"
        fbStreamKey = "841647253298885?s_bl=1&s_ps=1&s_psm=1&s_sw=0&s_vt=api-s&a=Abx5T0HQGueAzug0"
        youtubeStreamUrl = "rtmp://x.rtmp.youtube.com/live2"
        youtubeStreamKey = "cukx-j5q6-r2um-qx22-bbkb"

        SessionManager.fbStreamURL = fbStreamUrl
        SessionManager.fbStreamKey = fbStreamKey
        SessionManager.youtubeStreamURL = youtubeStreamUrl
        SessionManager.youtubeStreamKey = youtubeStreamKey

        binding?.titleName?.setText("Live with Nirob")
        priceAdapter.addItem(PriceTemp(100))

    }

    private fun pickUpImage() {
        if (!isStoragePermissions()) {
            return
        }
        try {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
            }.also {
                getImages.launch(it)
            }
        } catch (e: Exception) {
            Timber.d(e)
            context?.toast("No Application found to handle this action")
        }
    }

    private val getImages = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val fileUri = result?.data?.data
            Timber.d("FileUri: $fileUri")
            val actualPath = FileUtils(requireContext()).getPath(fileUri)
            Timber.d("FilePath: $actualPath")
            coverUrl = actualPath

            binding?.coverPhoto?.let { view ->
                Glide.with(view)
                    .load(actualPath)
                    .into(view)
            }

            binding?.coverUploadBtn?.text = "ভিডিও কভার ছবি পরিবর্তন"
            /*binding?.coverUploadBtn?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_200))
            binding?.coverUploadBtn?.iconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_30))
            binding?.coverUploadBtn?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_70))*/
        }

    }

    private fun isStoragePermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (storagePermissionRationale) {
                    permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                false
            } else {
                true
            }
        } else {
            return true
        }
    }

    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission ->
        if (hasPermission) {

        } else {
            val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (storagePermissionRationale) {
                alert("Permission Required", "App required Storage permission to function properly. Please grand permission.", true, "Give Permission", "Cancel") {
                    if (it == AlertDialog.BUTTON_POSITIVE) {
                        isStoragePermissions()
                    }
                }.show()
            } else {
                alert("Permission Required", "Please go to Settings to enable Storage permission. (Settings-apps--permissions)", true, "Settings", "Cancel") {
                    if (it == AlertDialog.BUTTON_POSITIVE) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${requireContext().packageName}")).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                }.show()
            }
        }
    }

    private fun pictureDialog(imageSource: Int) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_product_overview, null)
        builder.setView(view)
        val title: TextView = view.findViewById(R.id.title)
        val productImage: ImageView = view.findViewById(R.id.image)
        val close: ImageView = view.findViewById(R.id.close)

        Glide.with(productImage)
            .load(imageSource)
            .apply(RequestOptions().placeholder(R.drawable.ic_logo_ad1))
            .into(productImage)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3000000")))
        dialog.show()
        close.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}