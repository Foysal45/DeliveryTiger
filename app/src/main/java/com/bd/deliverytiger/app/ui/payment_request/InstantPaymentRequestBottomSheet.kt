package com.bd.deliverytiger.app.ui.payment_request

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.accounts.BankCheckForEftRequest
import com.bd.deliverytiger.app.api.model.chat.ChatUserData
import com.bd.deliverytiger.app.api.model.chat.FirebaseCredential
import com.bd.deliverytiger.app.api.model.instant_payment_rate.AllAlertMessage
import com.bd.deliverytiger.app.api.model.instant_payment_rate.InstantPaymentRateModel
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantInstantPaymentRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceivableDetailResponse
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceiveableDetailRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.OptionImageUrl
import com.bd.deliverytiger.app.databinding.FragmentInstantPaymentRequestBottomSheetBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.accounts_mail_format.AccountsMailFormatBottomSheet
import com.bd.deliverytiger.app.ui.add_order.AddProductBottomSheet
import com.bd.deliverytiger.app.ui.chat.ChatConfigure
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.meta.When
import kotlin.concurrent.thread

class InstantPaymentRequestBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentInstantPaymentRequestBottomSheetBinding? = null

    private val viewModel: InstantPaymentUpdateViewModel by inject()

    private var dataAdapter: RequestPaymentMethodAdapter = RequestPaymentMethodAdapter()

    private var model: MerchantPayableReceivableDetailResponse = MerchantPayableReceivableDetailResponse()

    private var messageLists: AllAlertMessage = AllAlertMessage()

    private var chargeModel: InstantPaymentRateModel = InstantPaymentRateModel()
    private var expressChargeModel: InstantPaymentRateModel = InstantPaymentRateModel()
    private var superExpressChargeModel: InstantPaymentRateModel = InstantPaymentRateModel()

    private val notificationId: Int = 10003200

    var onCloseBottomSheet: (() -> Unit)? = null

    private var isExpress: Int = 0

    private var requestPaymentMethod: Int = 0
    private var isPaymentTypeSelect: Boolean = false
    var day = ""
    var time = ""

    private var paymentMethodLists: MutableList<OptionImageUrl> = mutableListOf()

    var isMatchBankAccount = 0

    companion object {

        fun newInstance(): InstantPaymentRequestBottomSheet = InstantPaymentRequestBottomSheet().apply {}
        val tag: String = InstantPaymentRequestBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme3)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentInstantPaymentRequestBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.recyclerViewPaymentMethod?.let { view->
            with(view){
                setHasFixedSize(false)
                isNestedScrollingEnabled = false
                layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
                layoutAnimation = null
                adapter = dataAdapter
            }
        }


        initClickLister()
        fetchData()

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                }
            }
        })

    }

    private fun initData(){

        binding?.paymentAmount?.text = HtmlCompat.fromHtml("<font color='#626366'>৳ </font> <font color='#f05a2b'>${DigitConverter.toBanglaDigit(model.payableAmount, true)}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding?.instantPaymentTransferCharge?.text =HtmlCompat.fromHtml("(<font color='#E84545'>${DigitConverter.toBanglaDigit(model.instantPaymentCharge, true)}</font> টাকা চার্জ প্রযোজ্য)", HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding?.checkExpress?.setTextWith("${model.expressTime} ঘন্টা", "EXPRESS", HtmlCompat.fromHtml("(<font color='#E84545'>${DigitConverter.toBanglaDigit(model.expressCharge, true)}</font> টাকা চার্জ প্রযোজ্য)", HtmlCompat.FROM_HTML_MODE_LEGACY),0)
        binding?.checkNormal?.setTextWith("${model.normalTime} ঘন্টা", "NORMAL", "(অতিরিক্ত চার্জ নেই)",1)

        viewModel.checkBankNameForEFT(BankCheckForEftRequest(model.bankName)).observe(viewLifecycleOwner, Observer {
            isMatchBankAccount = it.isMatch
            if (isMatchBankAccount == 1){
                binding?.bankTransferLayout?.visibility = View.VISIBLE
            }else{
                binding?.bankTransferLayout?.visibility = View.GONE
            }
        })
    }

    private fun checkDay(): Boolean {

        //Fetch 7 days data
        val calendar = Calendar.getInstance()
        val simpleDayFormat = SimpleDateFormat("EEEE", Locale.US)
        val simpleTimeFormat = SimpleDateFormat("HH:mm", Locale.US)
        var isPopupShow = false
        day = simpleDayFormat.format(calendar.time)
        time = simpleTimeFormat.format(calendar.time)
        var timeLimit = time.split(":")
        if (day == "Thursday" && Integer.parseInt(timeLimit[0]) in 12..23){
            isPopupShow = true
        }
        if (day == "Friday" && Integer.parseInt(timeLimit[0]) in 1..23){
            isPopupShow = true
        }

        return isPopupShow
    }

    private fun checkTime(): Boolean {

        //Fetch 7 days data
        val calendar = Calendar.getInstance()
        val simpleTimeFormat = SimpleDateFormat("HH:mm", Locale.US)
        var isInTime = false
        time = simpleTimeFormat.format(calendar.time)
        var timeLimit = time.split(":")

        var cutOffTime = model.cutOffTime.split(":")
        if (Integer.parseInt(cutOffTime[1]) == 0){
            if (Integer.parseInt(timeLimit[0]) in 1..Integer.parseInt(cutOffTime[0])){
                isInTime = true
            }
        }else{
            if (Integer.parseInt(timeLimit[0]) in 1..Integer.parseInt(cutOffTime[0]) && Integer.parseInt(timeLimit[1]) in 0..Integer.parseInt(cutOffTime[1])){
                isInTime = true
            }
        }

        return isInTime
    }

    private fun initClickLister(){

        binding?.chatLayout?.setOnClickListener {
            gotoChatActivity()
        }

        binding?.transferBtnLayout?.setOnClickListener {
            if (model.payableAmount != 0){
                if (model.payableAmount <= model.limit){
                    alert("", HtmlCompat.fromHtml("<font><b>আপনি কি এখনই আপনার বিকাশ একাউন্টে (${model.bKashNo}) পেমেন্ট নিতে চান?</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY),true, "হ্যাঁ", "না") {
                        if (it == AlertDialog.BUTTON_POSITIVE) {
                            UserLogger.logGenie("Instant_payment_transfer_clicked")
                            instantPaymentRequestAndTransfer(2) // for transfer balance 2
                        }
                    }.show()
                }else{
                    showAlert(messageLists.instantPaymentLimitAlert)
                }

            }else{
                context?.toast("আপনার তথ্য লোড হচ্ছে। অনুগ্রহ করে অপেক্ষা করুন ।")
            }
        }

        binding?.bankTransferBtnLayout?.setOnClickListener {
            if (model.payableAmount != 0){
                if (model.payableAmount <= model.limit){
                    alert("", HtmlCompat.fromHtml("<font><b>আপনি কি ব্যাংক অ্যাকাউন্টে (${model.bankName} অ্যাকাউন্ট নাম্বারঃ ${model.bankACNo}) পেমেন্ট নিতে চান?</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY),true, "হ্যাঁ", "না") {
                        if (it == AlertDialog.BUTTON_POSITIVE) {
                            UserLogger.logGenie("Instant_payment_transfer_clicked")
                            instantPaymentRequestAndTransfer(2) // for transfer balance 2
                        }
                    }.show()
                }else{
                    showAlert(messageLists.instantPaymentLimitAlert)
                }

            }else{
                context?.toast("আপনার তথ্য লোড হচ্ছে। অনুগ্রহ করে অপেক্ষা করুন ।")
            }
        }

        binding?.requestBtn?.setOnClickListener {
            if (model.payableAmount != 0 && model.optionImageUrl.isNotEmpty()){
                var message = ""
                if (validateRequest()){
                    if (requestPaymentMethod == 1){
                        message = "আপনি কি বিকাশ অ্যাকাউন্টে (${model.bKashNo}) পেমেন্ট নিতে চান?"
                    }else if (requestPaymentMethod == 3){
                        message = "আপনি কি ব্যাংক অ্যাকাউন্টে (${model.bankName} অ্যাকাউন্ট নাম্বারঃ ${model.bankACNo}) পেমেন্ট নিতে চান?"
                    }
                    alert("",  HtmlCompat.fromHtml("<font><b>$message</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY),true, "হ্যাঁ", "না") {
                        if (it == AlertDialog.BUTTON_POSITIVE) {
                            UserLogger.logGenie("Regular_payment_Request_Click")
                            instantPaymentRequestAndTransfer(1) // for request
                        }
                    }.show()
                }
            }else{
                context?.toast("আপনার তথ্য লোড হচ্ছে । অনুগ্রহ করে অপেক্ষা করুন ।")
            }

        }

        binding?.chargeInfo?.setOnClickListener {
            if (chargeModel.charge.isNotEmpty() && chargeModel.discount.isNotEmpty()){
                viewInstantPaymentRateDialog(chargeModel, messageLists.instantChargeTitle, false)
            } else{
                context?.toast("কোন তথ্য পাওয়া যায়নি!")
            }
        }

        binding?.bankChargeInfo?.setOnClickListener {
            if (superExpressChargeModel.charge.isNotEmpty() && superExpressChargeModel.discount.isNotEmpty()){
                viewInstantPaymentRateDialog(superExpressChargeModel, messageLists.superEftChargeTitle, false)
            } else{
                context?.toast("কোন তথ্য পাওয়া যায়নি!")
            }
        }

        binding?.expressChargeInfo?.setOnClickListener {
            if (expressChargeModel.charge.isNotEmpty() && expressChargeModel.discount.isNotEmpty()){
                viewInstantPaymentRateDialog(expressChargeModel, messageLists.eftChargeTitle, true)
            } else{
                context?.toast("কোন তথ্য পাওয়া যায়নি!")
            }
        }

        dataAdapter.onItemClick = { paymentMethodList, _ ->

            requestPaymentMethod = paymentMethodList.paymentMethod
            isPaymentTypeSelect = true

            if (model.bankStatus > 0 && paymentMethodList.paymentMethod == 3){
                binding?.normalExpressRadioGroup?.visibility = View.VISIBLE
            }else{
                binding?.normalExpressRadioGroup?.visibility = View.GONE
            }

            if (model.bKashStatus > 0 && paymentMethodList.paymentMethod == 1){
                binding?.informationText?.text = HtmlCompat.fromHtml("<font><b>${model.normalTime} ঘন্টার </b></font>মধ্যে অতিরিক্ত চার্জ ছাড়াই পেমেন্ট পাবেন", HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding?.informationText?.visibility = View.VISIBLE
            }else{
                binding?.informationText?.visibility = View.GONE
            }
            if (model.bKashStatus > 0 && paymentMethodList.paymentMethod == 1){
                binding?.informationText?.visibility = View.VISIBLE
            }else{
                binding?.informationText?.visibility = View.GONE
            }

            if (isPaymentTypeSelect && requestPaymentMethod == 1 && model.bKashStatus <= 0){
                showAccountEnableAlert(1)
            }
            if (isPaymentTypeSelect && requestPaymentMethod == 5 && model.nagadNoStatus <= 0){
                showAccountEnableAlert(5)
            }
            if (isPaymentTypeSelect && requestPaymentMethod == 3 && model.bankStatus <= 0){
                showAccountEnableAlert(3)
            }

        }

        binding?.normalExpressRadioGroup?.setOnCheckedChangeListener { group, checkedId ->

             when(checkedId){
                R.id.check_express->{
                    if (checkTime()){
                        isExpress = 1
                        binding?.checkExpress?.isChecked = true
                        if (checkDay()){
                            showAlert(messageLists.fridayAlert)
                        }
                    }else{
                        showAlert(messageLists.expressTimeOverAlert)
                        isExpress = 0
                        binding?.checkExpress?.isChecked = false
                    }
                }
                R.id.check_normal ->{
                    isExpress = 2
                }
                else->{
                    0
                }
            }

        }

    }

    private fun gotoChatActivity() {
        val firebaseCredential = FirebaseCredential(
            firebaseWebApiKey = BuildConfig.FirebaseWebApiKey
        )
        val senderData = ChatUserData(SessionManager.courierUserId.toString(), SessionManager.companyName, SessionManager.mobile,
            imageUrl = "https://static.ajkerdeal.com/delivery_tiger/profile/${SessionManager.courierUserId}.jpg",
            role = "dt",
            fcmToken = SessionManager.firebaseToken
        )
        val receiverData = ChatUserData("1444", "Accounts Team", "01200000000",
            imageUrl = "https://static.ajkerdeal.com/images/admin_users/dt/938.jpg",
            role = "retention"
        )
        ChatConfigure(
            "dt-retention",
            senderData,
            firebaseCredential = firebaseCredential,
            receiver = receiverData
        ).config(requireContext())
        dismiss()
    }

    private fun fetchData(){
        binding?.progressBar?.isVisible = true
        val requestBody = MerchantPayableReceiveableDetailRequest(SessionManager.courierUserId, 0)
        viewModel.getMerchantPayableDetailForInstantPayment(requestBody).observe(viewLifecycleOwner, Observer { data->

            binding?.progressBar?.isVisible = false
            if (data != null){
                this.model = data
                initData()
                paymentMethodLists.clear()
                data.optionImageUrl.forEach {
                    if (it.imageUrl.isNotEmpty()){
                        paymentMethodLists.add(it)
                    }
                }

                if (paymentMethodLists.isNotEmpty()){
                    dataAdapter.initData(paymentMethodLists)
                }
            }
        })

        viewModel.getMessageAlertForIP().observe(viewLifecycleOwner, Observer{ messageLists->
            this.messageLists = messageLists
        })

        viewModel.getInstantPaymentRate().observe(viewLifecycleOwner, { chargeList->
            if(chargeList.charge.isNotEmpty() && chargeList.discount.isNotEmpty()){
                this.chargeModel = chargeList
            }
        })

        viewModel.getEftPaymentRate().observe(viewLifecycleOwner, { expressChargeList->
            if(expressChargeList.charge.isNotEmpty() && expressChargeList.discount.isNotEmpty()){
                this.expressChargeModel = expressChargeList
            }
        })

        viewModel.getSuperEftPaymentRate().observe(viewLifecycleOwner, { superExpressChargeList->
            if(superExpressChargeList.charge.isNotEmpty() && superExpressChargeList.discount.isNotEmpty()){
                this.superExpressChargeModel = superExpressChargeList
            }
        })

    }

    private fun viewInstantPaymentRateDialog(chargeInfo: InstantPaymentRateModel, title: String, isGreen: Boolean) {

        val myDialog = LayoutInflater.from(context).inflate(R.layout.item_view_instant_payment_rate_dialog, null)
        val mBuilder = AlertDialog.Builder(requireActivity()).setView(myDialog)
        val mAlertDialog = mBuilder.show()

        mAlertDialog.window?.attributes?.width = (getDeviceMetrics(requireContext())?.widthPixels?.times(0.80))?.toInt()
        mAlertDialog.window?.setBackgroundDrawable( resources.getDrawable(R.drawable.bg_stroke3))

        val titleTv = mAlertDialog.findViewById<TextView>(R.id.titleTV)
        titleTv?.text = title

        if (isGreen){
            titleTv?.setBackgroundResource(R.color.colorPrimary)
        }else{
            titleTv?.setBackgroundResource(R.color.orange)
        }

        val instantRV = mAlertDialog.findViewById<RecyclerView>(R.id.instantPaymentRateRv)
        val infoAdapter = InstantPaymentRateAdapter()
        with(instantRV!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = infoAdapter
        }
        infoAdapter.initData(chargeInfo.charge as MutableList<String>, chargeInfo.discount as MutableList<String>)

    }

    private fun instantPaymentRequestAndTransfer( payType: Int){
        val progressDialog = progressDialog("আপনার পেমেন্টটি প্রসেসিং হচ্ছে। অনুগ্রহ করে অপেক্ষা করুন...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var charge = 0
        var paymentType = payType
        var paymentMethod = requestPaymentMethod
        var amount = 0
        when(payType){
            1->{
                when(requestPaymentMethod){
                    1,5->{
                        charge = 0
                        amount = model.payableAmount
                    }
                    3->{
                        when (isExpress) {
                            1 -> {

                                if(model.expressNetPayableAmount > 1000 && model.expressNetPayableAmount < model.bankLimit){
                                    paymentType = 2 // express only
                                    charge = model.expressCharge
                                    amount = model.expressNetPayableAmount
                                }else{
                                    charge = 0
                                    amount = 0
                                }
                            }
                            2 -> {
                                if(model.payableAmount > 1000){
                                    charge = 0
                                    amount = model.payableAmount
                                }else{
                                    charge = 0
                                    amount = 0
                                }
                            }
                        }
                    }
                }
            }
            2->{
                paymentMethod = 1
                charge = model.instantPaymentCharge
                amount = model.netPayableAmount
            }

        }

        if (amount > 0){
            val requestBody = MerchantInstantPaymentRequest(charge, SessionManager.courierUserId, amount, paymentType, paymentMethod)
            Timber.d("requestBodyDebug $requestBody")
            viewModel.instantOr24hourPayment(requestBody).observe(viewLifecycleOwner, Observer { responseModel->
                progressDialog.dismiss()
                dismiss()
                if (responseModel != null) {
                    when (paymentType) {
                        1 -> {
                            if (responseModel.message == 1 && responseModel.paymentMethod == 3){
                                showLocalNotification("রেগুলার পেমেন্ট","আপনার পেমেন্ট রিকোয়েস্টটি প্রসেসিং এর জন্য সাবমিট হয়েছে। আগামী ${model.normalTime} ঘন্টার মধ্যে ${model.payableAmount} টাকা আপনার ব্যাংক অ্যাকাউন্টে ট্রান্সফার হবে।", "")
                                alert("", "আপনার পেমেন্ট রিকোয়েস্টটি প্রসেসিং এর জন্য সাবমিট করা হয়েছে। আগামী ${model.normalTime} ঘন্টার মধ্যে ${model.payableAmount} টাকা আপনার ব্যাংক অ্যাকাউন্টে ট্রান্সফার হবে।",false, "ঠিক আছে") {
                                    UserLogger.logGenie("Regular_payment_request_Successfully")
                                    if (it == AlertDialog.BUTTON_POSITIVE) {
                                        onCloseBottomSheet?.invoke()
                                    }
                                }.show()
                            }else if(responseModel.message == 1 && responseModel.paymentMethod == 1){
                                showLocalNotification("রেগুলার পেমেন্ট","আপনার পেমেন্ট রিকোয়েস্টটি প্রসেসিং এর জন্য সাবমিট হয়েছে। আগামী ${model.normalTime} ঘন্টার মধ্যে ${model.payableAmount} টাকা আপনার বিকাশ অ্যাকাউন্টে ট্রান্সফার হবে।", "")
                                alert("", "আপনার পেমেন্ট রিকোয়েস্টটি প্রসেসিং এর জন্য সাবমিট করা হয়েছে। আগামী ${model.normalTime} ঘন্টার মধ্যে ${model.payableAmount} টাকা আপনার বিকাশ অ্যাকাউন্টে ট্রান্সফার হবে।",false, "ঠিক আছে") {
                                    UserLogger.logGenie("Regular_payment_request_Successfully")
                                    if (it == AlertDialog.BUTTON_POSITIVE) {
                                        onCloseBottomSheet?.invoke()
                                    }
                                }.show()
                            }else{
                                alert("", "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন।",false, "ঠিক আছে") {}.show()
                            }
                        }
                        2 -> {
                            if (responseModel.paymentType == 2 && responseModel.paymentMethod == 3){
                                if (responseModel.message == 1){
                                    showLocalNotification("এক্সপ্রেস পেমেন্ট","${model.expressTime} ঘন্টার মধ্যে আপনার ব্যাংক অ্যাকাউন্টে ট্র্যান্সফার হবে", "")
                                    alert("", "আপনার পেমেন্ট রিকোয়েস্টটি প্রসেসিং এর জন্য সাবমিট করা হয়েছে। আগামী ${model.expressTime} ঘন্টার মধ্যে ${model.expressNetPayableAmount} টাকা আপনার ব্যাংক অ্যাকাউন্টে ট্রান্সফার হবে।",false, "ঠিক আছে") {
                                        UserLogger.logGenie("Instant_payment_transfer_Successfully")
                                        if (it == AlertDialog.BUTTON_POSITIVE) {
                                            onCloseBottomSheet?.invoke()
                                        }
                                    }.show()
                                }else{
                                    showAlert("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন।")
                                }
                            }else if (responseModel.paymentType == 2 && responseModel.paymentMethod == 1){
                                if (responseModel.message == 1 && !responseModel.transactionId.isNullOrEmpty()){
                                    showLocalNotification("ইনস্ট্যান্ট পেমেন্ট","টাকা বিকাশে ট্রান্সফার হয়েছে", "")
                                    alert("", "আপনার লেনদেনটি সফলভাবে সম্পন্ন হয়েছে।",false, "ঠিক আছে") {
                                        UserLogger.logGenie("Instant_payment_transfer_Successfully")
                                        if (it == AlertDialog.BUTTON_POSITIVE) {
                                            onCloseBottomSheet?.invoke()
                                        }
                                    }.show()
                                }else if (responseModel.message == 1 && responseModel.transactionId.isNullOrEmpty()){
                                    alert("",  messageLists.transectionIdNullAlert,false, "ঠিক আছে") {
                                        if (it == AlertDialog.BUTTON_POSITIVE) {
                                            onCloseBottomSheet?.invoke()
                                        }
                                    }.show()
                                }else{
                                    showAlert("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন।")
                                }
                            }
                        }
                    }
                }else{
                    val message = "কোথাও কোনো সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন।"
                    context?.toast(message)
                    dismiss()
                }
            })
        }else{
            progressDialog.dismiss()
            dismiss()
            var msg = ""
            msg = if (paymentMethod == 3 && paymentType == 1){
                messageLists.bankTransferMinimumLimit
            }else if (paymentMethod == 3 && paymentType == 2){
                messageLists.bankTransferLimitAlert
            }else{
                messageLists.insufficiantBalanceAlert
            }
            alert("", msg,false, "ঠিক আছে") {}.show()
        }

    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        val metrics = resources.displayMetrics
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    BottomSheetBehavior.from(bottomSheet).peekHeight = metrics.heightPixels
                }
            }
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = false
            BottomSheetBehavior.from(bottomSheet)
                .addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(p0: View, p1: Float) {
                        BottomSheetBehavior.from(bottomSheet).state =
                            BottomSheetBehavior.STATE_EXPANDED
                    }

                    override fun onStateChanged(p0: View, p1: Int) {
                        /*if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                        }*/
                    }
                })
        }
    }

    private fun showLocalNotification(title: String, body: String, theData: String) {
        val notificationManager: NotificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannels(notificationManager)
        val builder = createNotification(
            getString(R.string.default_notification_channel_id),
            title, body, createLocalPendingIntent(theData)
        )

        notificationManager.notify(notificationId, builder.build())
    }

    private fun createLocalPendingIntent(theData: String): PendingIntent {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("data", theData)
        return PendingIntent.getActivity(
            requireContext(),
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotification(channelId: String, title: String, body: String, pendingIntent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(requireContext(), channelId).apply {
            setSmallIcon(R.drawable.ic_tiger)
            setContentTitle(title)
            setContentText(body)
            setAutoCancel(true)
            color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
        }
    }

    private fun validateRequest(): Boolean{

        if (!isPaymentTypeSelect){
            context?.toast("পেমেন্ট টাইপ নির্বাচন করুন")
            return false
        }

        if (isPaymentTypeSelect && requestPaymentMethod == 1 && model.bKashStatus <= 0){
            showAccountEnableAlert(1)
            return false
        }
        if (isPaymentTypeSelect && requestPaymentMethod == 5 && model.nagadNoStatus <= 0){
            showAccountEnableAlert(5)
            return false
        }
        if (isPaymentTypeSelect && requestPaymentMethod == 3 && model.bankStatus <= 0){
            showAccountEnableAlert(3)
            return false
        }

        if (requestPaymentMethod == 3 && isExpress == 0){
            context?.toast("পেমেন্ট স্পিড নির্বাচন করুন")
            return false
        }
        return true
    }

    private fun showAccountEnableAlert(method: Int){
        when (method) {
            3 -> {
                alert("", "আপনার ব্যাংক একাউন্টের তথ্য অ্যাড করা হয়নি। তথ্য অ্যাড করতে নিচের লিংকে ক্লিক করে ফরমেট দেখে নিন।", true, "ok", "ব্যাংক ট্রান্সফার নির্দেশনা") {
                    if (it == AlertDialog.BUTTON_NEGATIVE) {
                            val tag: String = AccountsMailFormatBottomSheet.tag
                            val dialog: AccountsMailFormatBottomSheet = AccountsMailFormatBottomSheet.newInstance()
                            dialog.show(childFragmentManager, tag)
                    }
                }.show()
            }
            5 -> {
                showAlert("আপনার নগদ একাউন্টের তথ্য অ্যাড করা নেই। তথ্য অ্যাড করার জন্য একাউন্ট ম্যানেজার এর সাথে যোগাযোগ করুন।")
            }
            1 -> {
                showAlert("আপনার বিকাশ একাউন্টের তথ্য অ্যাড করা নেই। তথ্য অ্যাড করার জন্য একাউন্টম্যানেজার এর সাথে যোগাযোগ করুন।")
            }
        }

    }

    private fun showAlert(message: String){
        alert ( "", message, false, "ঠিক আছে" ).show()
    }

    private fun createNotificationChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            val channelList: MutableList<NotificationChannel> = mutableListOf()
            val channel1 = NotificationChannel(getString(R.string.default_notification_channel_id_instant_payment), "Instant Payment", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Delivery Tiger Instant Payment"
                setShowBadge(true)
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            channelList.add(channel1)

            notificationManager.createNotificationChannels(channelList)
        }
    }

    private fun getDeviceMetrics(context: Context): DisplayMetrics? {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        display.getMetrics(metrics)
        return metrics
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}