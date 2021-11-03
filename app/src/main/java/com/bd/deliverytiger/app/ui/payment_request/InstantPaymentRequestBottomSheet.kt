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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.instant_payment_rate.InstantPaymentRateModel
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantInstantPaymentRequest
import com.bd.deliverytiger.app.api.model.payment_receieve.MerchantPayableReceiveableDetailRequest
import com.bd.deliverytiger.app.databinding.FragmentInstantPaymentRequestBottomSheetBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

class InstantPaymentRequestBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentInstantPaymentRequestBottomSheetBinding? = null

    private val viewModel: InstantPaymentUpdateViewModel by inject()

    private var model: InstantPaymentRateModel = InstantPaymentRateModel()

    private val notificationId: Int = 100032

    var onCloseBottomSheet: (() -> Unit)? = null


    private var instantPayableAmount: Int = 0
    private var payableAmount: Int = 0
    private var instantTransferCharge: Int = 0

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

        binding?.paymentAmount?.text = HtmlCompat.fromHtml("<font color='#626366'>৳ </font> <font color='#f05a2b'>${DigitConverter.toBanglaDigit(payableAmount, true)}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding?.instantPaymentTransferCharge?.text =HtmlCompat.fromHtml("( <font color='#EC6639'>${DigitConverter.toBanglaDigit(instantTransferCharge, true)}</font> টাকা চার্জ প্রযোজ্য )", HtmlCompat.FROM_HTML_MODE_LEGACY)

        if (payableAmount == 0){
            binding?.transferBtnLayout?.isEnabled = false
            binding?.requestBtnLayout?.isEnabled = false
        }else{
            binding?.transferBtnLayout?.isEnabled = true
            binding?.requestBtnLayout?.isEnabled = true
        }
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

        binding?.requestBtnLayout?.setOnClickListener {
            alert("",  HtmlCompat.fromHtml("<font><b>আপনি কি রেগুলার পেমেন্ট রিকোয়েস্ট করতে চান?</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY),true, "হ্যাঁ", "না") {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    UserLogger.logGenie("Regular_payment_Request_Click")
                    instantPaymentRequestAndTransfer(1) // for transfer balance 1
                }
            }.show()
        }

        binding?.chargeInfo?.setOnClickListener {
            if (model.charge.isNotEmpty() && model.discount.isNotEmpty()){
                viewInstantPaymentRateDialog(model)
            } else{
                context?.toast("কোন তথ্য পাওয়া যায়নি!")
            }
        }
    }

    private fun fetchData(){
        val requestBody = MerchantPayableReceiveableDetailRequest(SessionManager.courierUserId, 0)
        viewModel.getMerchantPayableDetailForInstantPayment(requestBody).observe(viewLifecycleOwner, Observer { model->
            if (model != null){
                payableAmount = model.payableAmount
                instantPayableAmount = model.netPayableAmount
                instantTransferCharge = model.instantPaymentCharge
                initData()
            }
        })

        viewModel.getInstantPaymentRate().observe(viewLifecycleOwner, { model->
            if(model.charge.isNotEmpty() && model.discount.isNotEmpty()){
                this.model = model
            }
        })
    }

    private fun viewInstantPaymentRateDialog(model: InstantPaymentRateModel) {

        val myDialog = LayoutInflater.from(context).inflate(R.layout.item_view_instant_payment_rate_dialog, null)
        val mBuilder = AlertDialog.Builder(requireActivity()).setView(myDialog)
        val mAlertDialog = mBuilder.show()

        mAlertDialog.window?.attributes?.width = (getDeviceMetrics(requireContext())?.widthPixels?.times(0.80))?.toInt()
        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val instantRV = mAlertDialog.findViewById<RecyclerView>(R.id.instantPaymentRateRv)
        val infoAdapter = InstantPaymentRateAdapter()
        with(instantRV!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = infoAdapter
        }
        infoAdapter.initData(model.charge as MutableList<String>, model.discount as MutableList<String>)

    }

    private fun instantPaymentRequestAndTransfer( paymentType: Int){
        val progressDialog = progressDialog("আপনার পেমেন্টটি প্রসেসিং হচ্ছে। অনুগ্রহ করে অপেক্ষা করুন...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var charge = instantTransferCharge
        var amount = 0
        if (paymentType == 1) {
            charge = 0
            amount = payableAmount
        } else if (paymentType == 2){
            amount = instantPayableAmount
            charge = instantTransferCharge
        }
        if (amount > 0){
            val requestBody = MerchantInstantPaymentRequest(charge, SessionManager.courierUserId, amount, paymentType)
            viewModel.instantOr24hourPayment(requestBody).observe(viewLifecycleOwner, Observer { model->
                progressDialog.dismiss()
                dismiss()
                if (model != null) {
                    when (paymentType) {
                        1 -> {
                            if (model.message == 1){
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
                            if (model.message == 1 && !model.transactionId.isNullOrEmpty()){
                                showLocalNotification("ইনস্ট্যান্ট পেমেন্ট","টাকা বিকাশে ট্রান্সফার হয়েছে", "")
                                alert("", "আপনার লেনদেনটি সফলভাবে সম্পন্ন হয়েছে।",false, "ঠিক আছে") {
                                    UserLogger.logGenie("Instant_payment_transfer_Successfully")
                                    if (it == AlertDialog.BUTTON_POSITIVE) {
                                        onCloseBottomSheet?.invoke()
                                    }
                                }.show()
                            }else if (model.message == 1 && model.transactionId.isNullOrEmpty()){
                                alert("", "অনুগ্রহ পূর্বক ডেলিভারি টাইগার এর একাউন্টস ডিপার্মেন্ট এর সাথে যোগাযোগ করুন।",false, "ঠিক আছে") {
                                    if (it == AlertDialog.BUTTON_POSITIVE) {
                                        onCloseBottomSheet?.invoke()
                                    }
                                }.show()
                            }else{
                                alert("", "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন।",false, "ঠিক আছে") {}.show()
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