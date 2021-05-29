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
import com.bd.deliverytiger.app.api.model.live.live_schedule.PriceTemp
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleData
import com.bd.deliverytiger.app.api.model.live.live_schedule.ScheduleRequest
import com.bd.deliverytiger.app.api.model.live.live_schedule_insert.LiveScheduleInsertRequest
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSBody
import com.bd.deliverytiger.app.api.model.live.share_sms.SMSRequest
import com.bd.deliverytiger.app.databinding.FragmentLiveScheduleBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private var priceRange: String = ""
    private var paymentMode = "both"

    private var redirectToFB: Boolean = false
    private var isShowMobile: Boolean = false
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

    private lateinit var sessionManager: SessionManager

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

        sessionManager.init(requireContext())

        instantLive = activity?.intent?.getBooleanExtra("instantLive", false) ?: false

        if (instantLive) {
            binding?.uploadBtn?.text = "ইন্সট্যান্ট লাইভ শুরু করুন"
            (activity as LiveScheduleActivity).updateToolbarTitle("ইন্সট্যান্ট লাইভ")
            findNavController().currentDestination?.label = "ইন্সট্যান্ট লাইভ"
        } else {
            binding?.uploadBtn?.text = "শিডিউল সাবমিট করুন"
            (activity as LiveScheduleActivity).updateToolbarTitle("লাইভ শিডিউল")
            findNavController().currentDestination?.label = "লাইভ শিডিউল"
            initDatePicker()
            initTimePicker()
        }
        initPriceList()
        initClickLister()

        //ToDo: remove after test
        if (BuildConfig.DEBUG) {
            //testForm()
        }
    }

    private fun initClickLister() {

        binding?.checkButtonThirdParty?.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    binding?.facebookPageLinkEnableLayout?.isVisible = true
                }
                false -> {
                    binding?.facebookPageLinkEnableLayout?.isVisible = false
                }
            }
        }

        binding?.checkButtonOwn?.setOnCheckedChangeListener { buttonView, isChecked ->
            redirectToFB = when (isChecked) {
                true -> {
                    true
                }
                false -> {
                    false
                }
            }
        }

        binding?.checkButtonPhoneNumberShare?.setOnCheckedChangeListener { buttonView, isChecked ->
            isShowMobile = when (isChecked) {
                true -> {
                    true
                }
                false -> {
                    false
                }
            }
        }

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
                    val descriptionText = "আপনি ৩০ মিনিটের জন্যে ইনস্ট্যান্ট লাইভ শুরু করতে যাচ্ছেন।"
                    val noBtnText = "ক্যানসেল"
                    val yesBtnText = "লাইভ শুরু"

                    customAlert(titleText, descriptionText, noBtnText, yesBtnText) {
                        if (it == 1) {
                            insertSchedule()
                        }
                    }
                } else {
                    insertSchedule()
                }
            }
        }

        binding?.addPriceBtn?.setOnClickListener {
            hideKeyboard()
            val model = priceAdapter.getList().last()
            if (model.price > 0) {
                priceAdapter.addItem(PriceTemp())
                binding?.recyclerViewPrice?.post(Runnable {
                    binding?.recyclerViewPrice?.scrollToPosition(priceAdapter.itemCount - 1)
                })
            } else {
                context?.toast("প্রাইস লিখুন")
            }
        }

        binding?.addContactBtn?.setOnClickListener {
           /* val dialog = LiveShareFragment.newInstance(MyLiveSchedule(), instantLive)
            dialog.show(childFragmentManager, LiveShareFragment.tag)
            dialog.onShare = { shareMsg, numberList ->
                liveShareMsg = shareMsg
                selectedNumberList.clear()
                selectedNumberList.addAll(numberList)
                dialog.dismiss()
            }*/
        }

        binding?.paymentGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.bothPayment -> {
                    paymentMode = "both"
                }
                R.id.advPayment -> {
                    paymentMode = "advance"
                }
            }
        }

        binding?.checkButtonFB?.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    //context?.toast( "checkButtonFB checked")
                    isFacebook = true
                    binding?.fbStreamLayout?.isVisible = true
                    binding?.fbStreamUrl?.setText(sessionManager.fbStreamURL)
                    binding?.fbStreamKey?.setText(sessionManager.fbStreamKey)
                    binding?.nestedScrollView?.post {
                        binding?.nestedScrollView?.fullScroll(View.FOCUS_DOWN)
                        binding?.fbStreamLayout?.requestFocus()
                    }
                }
                false -> {
                    //context?.toast( "checkButtonFB unchecked")
                    isFacebook = false
                    binding?.facebookTV?.isVisible = false
                    binding?.fbStreamLayout?.isVisible = false
                    binding?.fbStreamUrl?.setText("")
                    binding?.fbStreamKey?.setText("")
                }
            }
        }

        binding?.checkButtonYT?.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    //context?.toast( "checkButtonYT checked")
                    isYoutube = true
                    binding?.youtubeStreamLayout?.isVisible = true
                    binding?.youtubeStreamUrl?.setText(sessionManager.youtubeStreamURL)
                    binding?.youtubeStreamKey?.setText(sessionManager.youtubeStreamKey)
                    binding?.nestedScrollView?.post {
                        binding?.nestedScrollView?.fullScroll(View.FOCUS_DOWN)
                        binding?.youtubeStreamUrl?.requestFocus()
                    }
                }
                false -> {
                    //context?.toast( "checkButtonYT unchecked")
                    isYoutube = false
                    binding?.youtubeTV?.isVisible = false
                    binding?.youtubeStreamLayout?.isVisible = false
                    binding?.youtubeStreamUrl?.setText("")
                    binding?.youtubeStreamKey?.setText("")
                }
            }
        }

        binding?.fbAbout?.setOnClickListener {
            val imageSource = R.drawable.ic_fb_stream_about
            pictureDialog(imageSource)
        }

        binding?.ytAbout?.setOnClickListener {
            val imageSource = R.drawable.ic_yt_stream_about
            pictureDialog(imageSource)
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

    private fun initPriceList() {
        priceAdapter = PriceAdapter()
        priceAdapter.addItem(PriceTemp())
        priceAdapter.addItem(PriceTemp())
        priceAdapter.addItem(PriceTemp())
        priceAdapter.addItem(PriceTemp())
        priceAdapter.addItem(PriceTemp())
        with(binding?.recyclerViewPrice!!) {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = priceAdapter
        }
        priceAdapter.onItemRemove = { model, position ->
            hideKeyboard()
            if (priceAdapter.getList().isEmpty()){
                binding?.recyclerViewPrice?.visibility = View.GONE
                //binding?.productListTitle?.visibility = View.GONE
            }
        }
        priceAdapter.onMsg = { model, position ->
            context?.toast("কমপক্ষে একটি প্রাইস থাকতে হবে")
        }
    }

    private fun instantScheduleTime() {
        scheduleId = 0
        liveDate = sdf.format(Date().time)
        fromTime = sdf1.format(Date().time)
        toTime = sdf1.format(Date().time + (33*60*1000L))
    }

    private fun insertSchedule() {
        var userId = sessionManager.profileId

        val model = LiveScheduleInsertRequest(
            liveDate, fromTime, toTime,
            userId, "merchant", 1,
            userId, scheduleId, liveTitle,
            priceRange, paymentMode, redirectToFB, isShowMobile, ownNumber, ownAlternateNumber,
            fbPageUrl, isFacebook, fbStreamUrl, fbStreamKey,
            isYoutube, youtubeStreamUrl, youtubeStreamKey, if (instantLive) 1 else 0
        )
        Timber.d("requestBody $model")

        viewModel.insertLiveSchedule(model).observe(viewLifecycleOwner, Observer { id ->
            if (id == -1) {
                if (instantLive) {
                    context?.toast("আমাদের Instant Live -এর সবকয়টি এই মুহূর্তে ব্যাস্ত আছে, আপনি একটু পর আবার চেষ্টা করুন অথবা Live Schedule নিন")
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
            }
        })
    }

    private fun insertLiveCover() {
        val progressDialog = progressDialog("লাইভ শিডিউল তৈরি হচ্ছে, অপেক্ষা করুন")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        viewModel.uploadLiveCoverPhoto(requireContext(), liveId, coverUrl).observe(viewLifecycleOwner, Observer { flag ->
            if (flag) {
                Timber.tag("LiveScheduleFragment").d("Live cover upload")
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
                        sessionManager.profileId,
                        requestBody
                    )
                    viewModel.shareSMS(smsRequest).observe(viewLifecycleOwner, Observer { flag ->
                        if (flag) {
                            context?.toast("লাইভ শেয়ার sms পাঠানো হয়েছে")
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
                    findNavController().navigate(R.id.nav_live_product_add, bundle)
                }
            }
        }
    }

    private fun datePicker() {
        val currentDateStamp = Date().time
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setStart(currentDateStamp)
        constraintsBuilder.setValidator(object : CalendarConstraints.DateValidator {
            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(p0: Parcel?, p1: Int) {
                p0?.writeLong(currentDateStamp)
            }

            override fun isValid(date: Long): Boolean {
                return date >= currentDateStamp
            }

        })

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("ডেট সিলেক্ট করুন")
        builder.setCalendarConstraints(constraintsBuilder.build())
        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener { date ->
            val pickedDate = sdf.format(date)
            //binding?.dateTV?.text = pickedDate
            fetchLiveSchedule(pickedDate)
        }
    }

    private fun validation(): Boolean {

        hideKeyboard()

        liveTitle = binding?.titleName?.text?.toString()?.trim() ?: ""
        if (liveTitle.isEmpty()) {
            context?.toast("লাইভ টাইটেল লিখুন")
            return false
        }

        if (coverUrl.isEmpty()) {
            context?.toast("লাইভ কভার ছবি যোগ করুন")
            return false
        }

        var priceString = ""
        priceAdapter.getList().forEach { model ->
            if (model.price > 0) {
                priceString = priceString + model.price + ","
            }
        }
        priceString = priceString.dropLast(1)

        /*var price = ""
        val price1 = binding?.productPrice1?.text.toString() ?: ""
        val price2 = binding?.productPrice2?.text.toString() ?: ""
        val price3 = binding?.productPrice3?.text.toString() ?: ""
        val price4 = binding?.productPrice4?.text.toString() ?: ""
        val price5 = binding?.productPrice5?.text.toString() ?: ""
        if (price1.isNotEmpty()) {
            price += price1

            if (price2.isNotEmpty()) {
                price += ","+price2
            }
            if (price3.isNotEmpty()) {
                price += ","+price3
            }
            if (price4.isNotEmpty()) {
                price += ","+price4
            }
            if (price5.isNotEmpty()) {
                price += ","+price5
            }
        }*/

        if (priceString.isEmpty()) {
            context?.toast("প্রোডাক্টের দাম লিখুন")
            return false
        }
        Timber.d("LiveProductPriceList $priceString")

        if (!isIntRange(priceString)) {
            context?.toast("প্রোডাক্টের দাম লিখুন")
            return false
        }
        priceRange = priceString
        Timber.d("LiveProductPriceList $priceRange")

        if (fromTime.isEmpty() || toTime.isEmpty()) {
            context?.toast("সিলেক্ট লাইভ টাইম")
            return false
        }

        if (binding?.checkButtonThirdParty?.isChecked == true) {
            fbPageUrl = binding?.fbPageUrl?.text.toString().trim()
            if (fbPageUrl.isEmpty()) {
                context?.toast("ফেসবুক পেজ লিংক দিন")
                binding?.fbPageUrl?.requestFocus()
                return false
            }

            ownNumber = binding?.ownPhoneNumber?.text.toString().trim()
            ownAlternateNumber = binding?.ownAlternatePhoneNumber?.text.toString().trim()
            if (ownNumber.isEmpty()) {
                context?.toast("আপনার ফোন নম্বরটি দিন")
                binding?.ownPhoneNumber?.requestFocus()
                return false
            } else if (ownNumber.length != 11) {
                context?.toast("সঠিক ফোন নম্বরটি দিন")
                binding?.ownPhoneNumber?.requestFocus()
                return false
            } else if (ownAlternateNumber.isNotEmpty() && ownAlternateNumber.length != 11) {
                context?.toast("সঠিক অল্টারনেটিভ ফোন নম্বরটি দিন")
                binding?.ownAlternatePhoneNumber?.requestFocus()
                return false
            }
        }


        if (isFacebook) {
            fbStreamUrl = binding?.fbStreamUrl?.text?.toString() ?: ""
            fbStreamKey = binding?.fbStreamKey?.text?.toString() ?: ""
            if (!fbStreamUrl.startsWith("rtmp")) {
                context?.toast("Facebook stream url এর শুরুতে \n\"rtmp\" থাকতে হবে")
                return false
            }
            if (fbStreamUrl.isEmpty()) {
                context?.toast("Facebook stream url লিখুন")
                return false
            }
            //rtmps://live-api-s.facebook.com:443/rtmp/
            if (!fbStreamUrl.contains("facebook.com")) {
                context?.toast("সঠিক Facebook stream url লিখুন")
                return false
            }
            if (fbStreamKey.isEmpty()) {
                context?.toast("Facebook stream key লিখুন")
                return false
            }
        } else {
            fbStreamUrl = ""
            fbStreamKey = ""
        }

        if (isYoutube) {
            youtubeStreamUrl = binding?.youtubeStreamUrl?.text?.toString() ?: ""
            youtubeStreamKey = binding?.youtubeStreamKey?.text?.toString() ?: ""
            if (!youtubeStreamUrl.startsWith("rtmp://")) {
                context?.toast("Youtube stream url এর শুরুতে \n\"rtmp\" থাকতে হবে")
                return false
            }
            if (youtubeStreamUrl.isEmpty()) {
                context?.toast("Youtube stream url লিখুন")
                return false
            }
            //rtmp://x.rtmp.youtube.com/live2
            if (!youtubeStreamUrl.contains("youtube.com")) {
                context?.toast("সঠিক Youtube stream url লিখুন")
                return false
            }
            if (youtubeStreamKey.isEmpty()) {
                context?.toast("Youtube stream key লিখুন")
                return false
            }
        } else {
            youtubeStreamUrl = ""
            youtubeStreamKey = ""
        }

        return true
    }

    private fun testForm() {
        fbStreamUrl = "rtmps://live-api-s.facebook.com:443/rtmp/"
        fbStreamKey = "841647253298885?s_bl=1&s_ps=1&s_psm=1&s_sw=0&s_vt=api-s&a=Abx5T0HQGueAzug0"
        youtubeStreamUrl = "rtmp://x.rtmp.youtube.com/live2"
        youtubeStreamKey = "cukx-j5q6-r2um-qx22-bbkb"

        sessionManager.fbStreamURL = fbStreamUrl
        sessionManager.fbStreamKey = fbStreamKey
        sessionManager.youtubeStreamURL = youtubeStreamUrl
        sessionManager.youtubeStreamKey = youtubeStreamKey

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

    private fun initDatePicker() {
        //val sdf1 = SimpleDateFormat("MMM dd EEE yyyy", Locale.US)
        val sdf2 = SimpleDateFormat("MMM", Locale.US)
        val sdf3 = SimpleDateFormat("EEE", Locale.US)
        val dateList: MutableList<DateData> = mutableListOf()
        val calender = Calendar.getInstance()
        liveDate = sdf.format(calender.time)
        calender.add(Calendar.DATE, -1)
        for (i in 0..6) {
            calender.add(Calendar.DATE, 1)
            //val newDate = sdf1.format(calender.time)
            val model = DateData(
                calender.get(Calendar.DAY_OF_MONTH),
                calender.get(Calendar.MONTH) + 1,
                calender.get(Calendar.YEAR),
                sdf2.format(calender.time),
                sdf3.format(calender.time),
                sdf.format(calender.time)
            )
            dateList.add(model)
        }

        val dateAdapter = ScheduleDateAdapter()

        binding?.scheduleDateLayout?.isVisible = true
        //dateAdapter.selectedPosition = 0
        //fetchLiveSchedule(liveDate)

        dateAdapter.initLoad(dateList)
        with(binding?.recyclerViewDate!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter
        }
        dateAdapter.onItemClicked = { model ->
            val selectedDate = model.formattedDate
            fetchLiveSchedule(selectedDate)
            liveDate = selectedDate
        }
    }

    private fun initTimePicker() {
        timeAdapter = ScheduleTimeAdapter()
         with(binding?.recyclerViewTime!!) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = timeAdapter
        }
        timeAdapter.onItemClicked = { model ->
            selectedTime(model)
        }
    }

    private fun fetchLiveSchedule(date: String) {
        val requestBody = ScheduleRequest(date)
        viewModel.fetchLiveSchedule(requestBody).observe(viewLifecycleOwner, Observer { list ->
            timeAdapter.initLoad(list)
            binding?.scheduleTimeLayout?.isVisible = true
            /*if (!instantLive) {
            } else {
                val firstActiveIndex = list.indexOfFirst { it.isTimeActive == 0 }
                if (firstActiveIndex == -1) {
                    context?.toast("কারেন্ট লাইভ শিডিউল খালি নেই, অন্য শিডিউল সিলেক্ট করুন")
                    binding?.scheduleDateLayout?.isVisible = true
                    binding?.scheduleTimeLayout?.isVisible = true
                } else {
                    val model = timeAdapter.modelByIndex(firstActiveIndex)
                    selectedTime(model)
                }
            }*/
        })
    }

    private fun selectedTime(model: ScheduleData) {
        scheduleId = model.id
        fromTime = model.fromScheduleTime ?: ""
        toTime = model.toScheduleTime ?: ""
    }

    /*private fun initSpinner(list: List<ScheduleData>) {
        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, list)
        binding?.timeSpinner?.adapter = spinnerAdapter
        binding?.timeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
        }
    }*/

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

            binding?.coverUploadBtn?.text = "লাইভ কভার ছবি পরিবর্তন"
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