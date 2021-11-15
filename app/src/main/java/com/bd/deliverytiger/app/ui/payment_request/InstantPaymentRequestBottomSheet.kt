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
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.instant_payment_rate.InstantPaymentRateModel
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantInstantPaymentRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceivableDetailResponse
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceiveableDetailRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.OptionImageUrl
import com.bd.deliverytiger.app.databinding.FragmentInstantPaymentRequestBottomSheetBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import timber.log.Timber
import javax.annotation.meta.When
import kotlin.concurrent.thread

class InstantPaymentRequestBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentInstantPaymentRequestBottomSheetBinding? = null

    private val viewModel: InstantPaymentUpdateViewModel by inject()

    private var dataAdapter: RequestPaymentMethodAdapter = RequestPaymentMethodAdapter()

    private var model: MerchantPayableReceivableDetailResponse = MerchantPayableReceivableDetailResponse()

    private var chargeModel: InstantPaymentRateModel = InstantPaymentRateModel()
    private var expressChargeModel: InstantPaymentRateModel = InstantPaymentRateModel()

    private val notificationId: Int = 100032

    var onCloseBottomSheet: (() -> Unit)? = null

    private var isExpress: Int = 0

    private var requestPaymentMethod: Int = 0
    private var isPaymentTypeSelect: Boolean = false

    private var paymentMethodLists: MutableList<OptionImageUrl> = mutableListOf()

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
                layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
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

        binding?.checkExpress?.setTextWith("${model.express} ঘন্টা", "EXPRESS", HtmlCompat.fromHtml("(<font color='#E84545'>${DigitConverter.toBanglaDigit(model.expressCharge, true)}</font> টাকা চার্জ প্রযোজ্য)", HtmlCompat.FROM_HTML_MODE_LEGACY),0)
        binding?.checkNormal?.setTextWith("${model.normalTime} ঘন্টা", "NORMAL", "(অতিরিক্ত চার্জ নেই)",1)

    }

    private fun initClickLister(){

        binding?.transferBtnLayout?.setOnClickListener {
            alert("", HtmlCompat.fromHtml("<font><b>আপনি কি এখনই টাকা আপনার বিকাশ একাউন্টে (${SessionManager.bkashNumber}) নিতে চান?</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY),true, "হ্যাঁ", "না") {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    UserLogger.logGenie("Instant_payment_transfer_clicked")
                    instantPaymentRequestAndTransfer(2) // for transfer balance 2
                }
            }.show()

        }

        binding?.requestBtn?.setOnClickListener {

            if (validateRequest()){
                alert("",  HtmlCompat.fromHtml("<font><b>আপনি কি রেগুলার পেমেন্ট রিকোয়েস্ট করতে চান?</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY),true, "হ্যাঁ", "না") {
                    if (it == AlertDialog.BUTTON_POSITIVE) {
                        UserLogger.logGenie("Regular_payment_Request_Click")
                        instantPaymentRequestAndTransfer(1) // for request
                    }
                }.show()
            }
        }

        binding?.chargeInfo?.setOnClickListener {
            if (chargeModel.charge.isNotEmpty() && chargeModel.discount.isNotEmpty()){
                viewInstantPaymentRateDialog(chargeModel, "ইনস্ট্যান্ট ট্রান্সফার চার্জ")
            } else{
                context?.toast("কোন তথ্য পাওয়া যায়নি!")
            }
        }

        binding?.expressChargeInfo?.setOnClickListener {
            if (expressChargeModel.charge.isNotEmpty() && expressChargeModel.discount.isNotEmpty()){
                viewInstantPaymentRateDialog(expressChargeModel, "এক্সপ্রেস ট্রান্সফার চার্জ")
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
        }

        binding?.normalExpressRadioGroup?.setOnCheckedChangeListener { group, checkedId ->

            isExpress = when(checkedId){
                R.id.check_express->{
                    1
                }
                R.id.check_normal ->{
                    2
                }
                else->{
                    0
                }
            }
        }

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

    }

    private fun viewInstantPaymentRateDialog(chargeInfo: InstantPaymentRateModel, title: String) {

        val myDialog = LayoutInflater.from(context).inflate(R.layout.item_view_instant_payment_rate_dialog, null)
        val mBuilder = AlertDialog.Builder(requireActivity()).setView(myDialog)
        val mAlertDialog = mBuilder.show()

        mAlertDialog.window?.attributes?.width = (getDeviceMetrics(requireContext())?.widthPixels?.times(0.80))?.toInt()
        mAlertDialog.window?.setBackgroundDrawable( resources.getDrawable(R.drawable.bg_stroke3))

        val titleTv = mAlertDialog.findViewById<TextView>(R.id.titleTV)
        titleTv?.text = title

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
                                paymentType = 2 // express only
                                charge = model.expressCharge
                                amount = model.expressNetPayableAmount
                            }
                            2 -> {
                                charge = 0
                                amount = model.payableAmount
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
            progressDialog.dismiss()
            /*viewModel.instantOr24hourPayment(requestBody).observe(viewLifecycleOwner, Observer { responseModel->
                progressDialog.dismiss()
                dismiss()
                if (responseModel != null) {
                    when (paymentType) {
                        1 -> {
                            if (responseModel.message == 1){
                                showLocalNotification("রেগুলার পেমেন্ট","আপনার রিকোয়েস্ট টি সফল হয়েছে।", "")
                                alert("", "আপনার রিকোয়েস্ট টি সফল হয়েছে।",false, "ঠিক আছে") {
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
                                    showLocalNotification("এক্সপ্রেস পেমেন্ট","২৪ ঘন্টার মধ্যে আপনার ব্যাংক অ্যাকাউন্টে ট্র্যান্সফার হবে", "")
                                    alert("", "আপনার পেমেন্ট রিকোয়েস্টটি প্রসেসিং এর জন্য সাবমিট হয়েছে।  আগামী ${model.expressCharge} ঘন্টার মধ্যে ${model.expressNetPayableAmount} টাকা ট্রান্সফার হবে।",false, "ঠিক আছে") {
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
                                    alert("", "অনুগ্রহ পূর্বক ডেলিভারি টাইগার এর একাউন্টস ডিপার্মেন্ট এর সাথে যোগাযোগ করুন।",false, "ঠিক আছে") {
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
            })*/
        }else{
            progressDialog.dismiss()
            dismiss()
            alert("", "আপনার ট্রান্সফার করার মতো ব্যালান্স নেই।",false, "ঠিক আছে") {}.show()
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
            context?.toast("সার্ভিস টাইপ নির্বাচন করুন")
            return false
        }
        return true
    }

    private fun showAccountEnableAlert(method: Int){
        when (method) {
            3 -> {
                showAlert("আপনার ব্যাংক একাউন্টের তথ্য অ্যাড করা হয়নি।তথ্য  অ্যাড করতে নিচের লিংকে ক্লিক করে ফরমেট দেখে নিন।")
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