package com.bd.deliverytiger.app.ui.live.stream

import android.Manifest
import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.*
import android.util.Rational
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.EventListener
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.services.ConnectCheckerRtp
import com.bd.deliverytiger.app.api.model.firebase.FirebaseSettings
import com.bd.deliverytiger.app.api.model.live.firebase.LikeCount
import com.bd.deliverytiger.app.api.model.live.firebase.LiveProductEvent
import com.bd.deliverytiger.app.api.model.live.firebase.ViewCount
import com.bd.deliverytiger.app.api.model.live.live_channel_medialive.ChannelUpdateRequest
import com.bd.deliverytiger.app.api.model.live.live_started_notify.LiveStartedNotifyRequest
import com.bd.deliverytiger.app.api.model.live.live_status.LiveStatusUpdateRequest
import com.bd.deliverytiger.app.broadcast.ConnectivityReceiver
import com.bd.deliverytiger.app.databinding.ActivityLiveBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.live.chat.ChatAdapter
import com.bd.deliverytiger.app.ui.live.chat.ChatBoxBottomSheet
import com.bd.deliverytiger.app.ui.live.chat.model.ChatData
import com.bd.deliverytiger.app.ui.live.live_product_insert.quick_insert.ProductHighlightAdapter
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.encoder.input.video.CameraOpenException
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.util.BitrateAdapter
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@SuppressLint("SetTextI18n")
class LiveActivity : AppCompatActivity(),
    View.OnClickListener,
    SurfaceHolder.Callback,
    OnTouchListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityLiveBinding

    private lateinit var cameraBase: RtmpCamera2
    private var bitrateAdapter: BitrateAdapter? = null
    private var isFrontCameraDefault = false

    private val requestCodeCamera = 123
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var rmmpUrl = AppConstant.CHANNEL5

    private var resolutionWidth = 854
    private var resolutionHeight = 480
    private var isStreaming: Boolean = false
    //private var isRetryStreaming: Boolean = false

    // Live Group Chat
    private lateinit var fireBaseDataBase: FirebaseDatabase
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var chatRoomRef: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var dataAdapter: ChatAdapter
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    private val queryLimit = 15
    private var isLoading = false
    private val visibleThreshold = 6
    private var totalCount = 0

    private var snackBar: Snackbar? = null
    private var toast: Toast? = null
    private lateinit var connectivityReceiver: ConnectivityReceiver

    private var liveId: Int = 0
    private var liveTitle: String = ""
    private var cloudFrontUrl: String = ""
    private var archiveUrl: String = ""
    private var channelId: Int = 0
    private var liveDate: String = ""
    private var liveStartTime: String = ""
    private var liveEndTime: String = ""
    private var liveEndDateTime: String = ""
    private var facebook: Boolean = false
    private var facebookUrl: String = ""
    private var facebookStream: String = ""
    private var youtube: Boolean = false
    private var youtubeUrl: String = ""
    private var youtubeStream: String = ""
    private var instantLive: Boolean = false

    private var likeCount: Long = 0

    private var channelState: ChannelState = ChannelState.IDLE
    private var countDownTimer: CountDownTimer? = null

    // Live show
    private lateinit var dbFirestoreViews: CollectionReference
    private lateinit var dbFirestoreLikes: CollectionReference
    private var currentViewLister: ListenerRegistration? = null
    private var currentLikeLister: ListenerRegistration? = null

    private var liveStreamId: String = "0"
    private lateinit var productHighLightRef: DatabaseReference
    private lateinit var dataAdapterProduct: ProductHighlightAdapter

    // Dialog

    private var selectedTrackIndex: Int = 1

    private val viewModel: LiveActivityViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedTrackIndex = SessionManager.getSelectedVideoQualityTrackIndex

        liveId = intent?.getIntExtra("liveId", 0) ?: 0
        rmmpUrl = intent?.getStringExtra("rtmpUrl") ?: AppConstant.CHANNEL5
        liveTitle = intent?.getStringExtra("liveTitle") ?: "live"
        channelId = intent?.getIntExtra("channelId", 0) ?: 0
        liveDate = intent?.getStringExtra("liveDate") ?: ""
        liveStartTime = intent?.getStringExtra("liveStartTime") ?: ""
        liveEndTime = intent?.getStringExtra("liveEndTime") ?: ""
        liveEndDateTime = intent?.getStringExtra("liveEndDateTime") ?: ""
        facebook = intent?.getBooleanExtra("facebook", false) ?: false
        facebookUrl = intent?.getStringExtra("facebookUrl") ?: ""
        facebookStream = intent?.getStringExtra("facebookStream") ?: ""
        youtube = intent?.getBooleanExtra("youtube", false) ?: false
        youtubeUrl = intent?.getStringExtra("youtubeUrl") ?: ""
        youtubeStream = intent?.getStringExtra("youtubeStream") ?: ""
        instantLive = intent?.getBooleanExtra("instantLive", false) ?: false
        cloudFrontUrl = intent?.getStringExtra("cloudFrontUrl") ?: ""

        liveStreamId = liveId.toString()

        Timber.d("LiveDebug ${intent?.extras?.bundleToString()}")

        cameraBase = RtmpCamera2(binding.surfaceView, connectCheckerRtp).apply {
            setReTries(5)
        }
        if (isPermissions()) {
            initSurfaceView()
        }

        binding.streamBtn.setOnClickListener(this)
        binding.recordBtn.setOnClickListener(this)
        binding.toggleMicBtn.setOnClickListener(this)
        binding.toggleCameraBtn.setOnClickListener(this)
        binding.settingsBtn.setOnClickListener(this)

        initFireBaseDB()
        connectivityReceiver = ConnectivityReceiver()

        /*viewModel.viewState.observe(this, Observer { state ->
            when(state) {
                is ViewState.ProgressState -> {
                    if (state.isShow && state.type == 1) {
                        progressDialog.show()
                    } else {
                        progressDialog.dismiss()
                    }
                }
            }
        })*/
    }

    private fun initSurfaceView() {

        initCamera()
    }

    private val connectCheckerRtp = object : ConnectCheckerRtp {
        override fun onConnectionSuccessRtp() {
            runOnUiThread {
                //this@LiveActivity.toast("Stream connected")
                showToast("Stream connected")
                binding.streamBtn.setImageDrawable(ContextCompat.getDrawable(this@LiveActivity, R.drawable.ic_stop))
                isStreaming = true
                updateLiveStatus("live", cloudFrontUrl)
                liveTimeOut()
                showLiveStatus()
            }
            /*bitrateAdapter = BitrateAdapter(BitrateAdapter.Listener { bitrate ->
                camera2Base?.setVideoBitrateOnFly(bitrate)
            })
            bitrateAdapter?.setMaxBitrate(camera2Base?.bitrate!!)*/
            Timber.d("RTPService onConnectionSuccessRtp Stream started")
        }

        @SuppressLint("SetTextI18n")
        override fun onNewBitrateRtp(bitrate: Long) {
            //bitrateAdapter?.adaptBitrate(bitrate)
            runOnUiThread {
                updateBitRateUI(bitrate)
            }
            Timber.d("RTPService onNewBitrateRtp bitrate $bitrate")
        }

        override fun onConnectionFailedRtp(reason: String) {
            runOnUiThread {
                //this@LiveActivity.toast("Stream connection failed, retrying after 2s")
                showToast("Stream connection failed, Retrying")
                try {
                    if (cameraBase.reTry(2000, reason)) {
                        showToast("Stream connection failed, Retrying")
                    } else {
                        stopStreaming()
                        stopRecording()
                        showToast("Stream connection failed")
                        binding.streamBtn.setImageDrawable(ContextCompat.getDrawable(this@LiveActivity, R.drawable.ic_stream))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("Stream connection failed, Retrying")
                }
            }
            Timber.d("RTPService onConnectionFailedRtp $reason")
        }

        override fun onDisconnectRtp() {
            runOnUiThread {
                //this@LiveActivity.toast("Stream disconnected")
                showToast("Stream disconnected")
                binding.streamBtn.setImageDrawable(ContextCompat.getDrawable(this@LiveActivity, R.drawable.ic_stream))
                hideLiveStatus()
                updateLiveStatus("replay", archiveUrl)
            }
            stopRecording()
            isStreaming = false
            Timber.d("RTPService onDisconnectRtp disconnect")
        }

        override fun onAuthErrorRtp() {
            runOnUiThread {
                //this@LiveActivity.toast("Stream auth error")
                showToast("Stream auth error")
            }
            Timber.d("RTPService onAuthErrorRtp Stream auth error")
        }

        override fun onAuthSuccessRtp() {
            //this@LiveActivity.toast("Stream auth success")
            Timber.d("RTPService onAuthSuccessRtp Stream auth success")
        }
    }

    private fun initCamera() {
        val model = SessionManager.getStreamConfig()
        //val model = StreamSettingData()
        val resolution = if (model.resolutionId == -1) {
            var defaultIndex = cameraBase.resolutionsBack.indexOfLast { it.height == 720 }
            if (defaultIndex == -1) {
                defaultIndex = 0
            }
            SessionManager.updateResolutionId(defaultIndex)
            cameraBase.resolutionsBack[defaultIndex]
        } else {
            cameraBase.resolutionsBack[model.resolutionId]
        }

        //val width = resolution.width
        //val height = resolution.height
        resolutionWidth = SessionManager.getStreamConfig().resolutionWidth
        resolutionHeight = SessionManager.getStreamConfig().resolutionHeight

        //resolutionWidth = 720
        //resolutionHeight = 480
        val orientation = CameraHelper.getCameraOrientation(this)
        cameraBase.prepareVideo(resolutionWidth, resolutionHeight, model.fps, model.videoBitRate * 1000, orientation)
        Timber.d("RTPService startPreview orientation $orientation width $resolutionWidth height $resolutionHeight")
    }

    private fun stopPreview() {
        if (cameraBase.isOnPreview) cameraBase.stopPreview()
        Timber.d("RTPService stopPreview")
    }

    private fun switchCamera() {
        try {
            cameraBase.switchCamera()
        } catch (e: CameraOpenException) {
            Timber.d(e)
        }
        Timber.d("RTPService camera switch")
    }

    private fun toggleMic() {
        if (cameraBase.isAudioMuted) {
            cameraBase.enableAudio()
            binding.toggleMicBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mic))
            this.toast("Mute off")
        } else {
            cameraBase.disableAudio()
            binding.toggleMicBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mic_off))
            this.toast("Mute on")
        }
        Timber.d("RTPService mic toggle")
    }


    private fun startStreamRtp(endPoint: String) {

        if (!cameraBase.isStreaming) {
            //camera2Base!!.setAuthorization("user", "pass")
            if (cameraBase.isRecording || prepareEncoders()) {
                cameraBase.startStream(endPoint)
                if (isFrontCameraDefault) {
                    if (!cameraBase.isFrontCamera) {
                        cameraBase.switchCamera()
                    }
                }
            } else {
                Toast.makeText(this, "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT).show()
            }
        } else {
            this@LiveActivity.toast("You are already streaming")
        }
    }

    private fun prepareEncoders(): Boolean {

        //val model = SessionManager.getStreamConfig()
        val model = StreamSettingData()

        val isVideoPrepare = cameraBase.prepareVideo(resolutionWidth, resolutionHeight, model.fps, model.videoBitRate * 1000, CameraHelper.getCameraOrientation(this))
        val isAudioPrepare = cameraBase.prepareAudio(model.audioBitRate * 1000, model.audioSampleRate, model.isStereoChannel, model.isEchoCanceler, model.isNoiseSuppressor)

        return isVideoPrepare && isAudioPrepare
    }

    private fun stopStreaming() {
        if (cameraBase.isStreaming) {
            cameraBase.stopStream()
            Timber.d("RTPService streaming stopped")

            binding.chronometer.onChronometerTickListener = null
            binding.chronometer.stop()
            binding.chronometer.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.liveCountDown.visibility = View.GONE
            binding.viewCount.visibility = View.GONE
            hideLiveStatus()
        }
    }

    private fun recordStream() {

        if (cameraBase.isRecording) {

            val folder: File = File(Environment.getExternalStorageDirectory().absolutePath.toString() + "/LiveStreaming")
            if (!folder.exists()) {
                folder.mkdir()
            }
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val currentDateAndTime = sdf.format(Date())
            val path = folder.absolutePath + "/" + currentDateAndTime + ".mp4"
            if (cameraBase.isStreaming) {
                if (prepareEncoders()) {
                    cameraBase.startRecord(path)
                } else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT).show()
                }
            } else {
                cameraBase.startRecord(path)
            }
        } else {
            cameraBase.stopRecord()
        }
    }

    private fun stopRecording() {
        if (cameraBase.isRecording) {
            cameraBase.stopRecord()
            Timber.d("RTPService recording stopped")
        }
    }

    override fun onStart() {
        super.onStart()
        binding.surfaceView.holder.addCallback(this)
        Timber.d("RTPService onStart called")

        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onStop() {
        super.onStop()
        binding.surfaceView.holder.removeCallback(this)
        stopStreaming()
        stopRecording()
        Timber.d("RTPService onStop called")
        finishAndRemoveTask()
        chatRoomRef.removeEventListener(valueEventListener)

        ConnectivityReceiver.connectivityReceiverListener = null
        unregisterReceiver(connectivityReceiver)

        binding.chronometer.onChronometerTickListener = null
        binding.chronometer.stop()
        hideLiveCountDown()

        if (channelState == ChannelState.STARTING) {
            channelAction("emergency")
        } else if (channelState == ChannelState.RUNNING) {
            channelAction("stop")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentViewLister?.remove()
        currentLikeLister?.remove()
        productHighLightRef.setValue(LiveProductEvent(Date().time, ""))
    }

    // onClickListener
    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.streamBtn -> {
                //for testing purpose
                /*val tempData = sessionManager.streamConfig
                Timber.d("logTrack $tempData")*/

                binding.streamBtn.isEnabled = false
                binding.settingsBtn.isVisible = false
                onStream()
            }

            R.id.toggleCameraBtn -> {
                toggleCamera()
            }

            R.id.toggleMicBtn -> {
                toggleMic()
            }

            R.id.recordBtn -> {
                onRecord()
            }

            R.id.settingsBtn -> {
                onConfig()
            }
        }
    }

    private fun onStream() {

        if (cameraBase.isStreaming) {
            binding.streamBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stream))
            //this.toast("Stopping stream")
        } else {
            binding.streamBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop))
            //this.toast("Starting stream")
        }

        if (cameraBase.isStreaming) {
            channelAction("stop")
        } else {
            channelAction("start")
        }

        /*if (cameraBase.isStreaming) {
            stopStreaming()
        } else {
            startStreamRtp(rmmpUrl)
        }*/
    }

    private fun toggleCamera() {
        switchCamera()
        if (cameraBase.isFrontCamera) {
            binding.toggleCameraBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_camera_front))
            this.toast("Camera mode front")
            binding.surfaceView.setIsStreamHorizontalFlip(true)
        } else {
            binding.toggleCameraBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_camera_back))
            this.toast("Camera mode back")
            binding.surfaceView.setIsStreamHorizontalFlip(false)
        }
    }

    private fun onRecord() {
        if (cameraBase.isRecording) {
            stopRecording()
            binding.recordBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop_circle))
            this.toast("Recording stop")
        } else {
            recordStream()
            binding.recordBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_rec))
            this.toast("Recording start")
        }
    }

    private fun onConfig() {
        videoQualitySelectDialog()

        /*
        val tag = StreamSetting.tag
        val dialog = StreamSetting.newInstance(cameraBase)
        dialog.show(supportFragmentManager, tag)
        dialog.onSave = { settingData ->
            Timber.d("StreamSettingData $settingData")
        }*/
    }

    private fun videoQualitySelectDialog() {
        val resolutions: MutableList<String> = mutableListOf()
        resolutions.add("480p (16:9)")
        resolutions.add("360p (16:9)")
        resolutions.add("240p (16:9)")
        resolutions.add("480p (3:2)")
        resolutions.add("360p (3:2)")
        resolutions.add("240p (3:2)")

        var tempSelectedItem = selectedTrackIndex
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose resolution")
        builder.setSingleChoiceItems(resolutions.toTypedArray(), selectedTrackIndex+1) { dialog, which ->
            Timber.d("setSingleChoiceItems which $which")
            selectedTrackIndex = which - 1
        }
        builder.setPositiveButton("OK") { dialog, which ->
            Timber.d("logTrack $selectedTrackIndex")
            tempSelectedItem = selectedTrackIndex

            val model = when (selectedTrackIndex) {
                -1 -> {
                    StreamSettingData(0, 1000, 854, 480)
                }
                0 -> {
                    StreamSettingData(1, 800, 640, 360)
                }
                2 -> {
                    StreamSettingData(3, 1000, 720, 480)
                }
                3 -> {
                    StreamSettingData(4, 800, 540, 360)
                }
                4 -> {
                    StreamSettingData(5, 400, 360, 240)
                }
                else -> {
                    StreamSettingData(2, 400, 426, 240)
                }
            }

            Timber.d("logTrack $model")
            //Timber.d("logTrack ${SessionManager.getStreamConfig()}")

            SessionManager.getSelectedVideoQualityTrackIndex = selectedTrackIndex

            restartCamera()
        }
        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            selectedTrackIndex = tempSelectedItem
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        dialog.show()
        dialog.window?.decorView?.systemUiVisibility = window.decorView.systemUiVisibility
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    private fun restartCamera() {
        cameraBase.replaceView(binding.surfaceView)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            val action = it.action
            if (it.pointerCount > 1) {
                if (action == MotionEvent.ACTION_MOVE) {
                    cameraBase.setZoom(it)
                }
            } else {
                if (action == MotionEvent.ACTION_UP) {

                }
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (cameraBase.isStreaming) {
            binding.streamBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop))
        } else {
            binding.streamBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stream))
        }
    }


    private fun isPermissions(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

            return if (cameraPermission != PackageManager.PERMISSION_GRANTED || storagePermission != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED) {

                val cameraPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val audioPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                if (cameraPermissionRationale || storagePermissionRationale || audioPermissionRationale) {
                    requestPermissions(permissions, requestCodeCamera)
                } else {
                    requestPermissions(permissions, requestCodeCamera)
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
        if (requestCode == requestCodeCamera) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initSurfaceView()
                } else {
                    alert("Permission Required", "App required Camera & Audio permission to function properly. Please grand permission.", true, "Give Permission", "Cancel") {
                        if (it == AlertDialog.BUTTON_POSITIVE) {
                            ActivityCompat.requestPermissions(this, permissions, requestCodeCamera)
                        }
                    }.show()
                }
            }
        }
    }

    private fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun showLiveStatus() {
        binding.liveStatusLayout.isVisible = true
        binding.bitRateTV.text = "0 KB/s"
        binding.bitRateTV.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun hideLiveStatus() {
        binding.liveStatusLayout.isVisible = false
        binding.bitRateTV.text = "0 KB/s"
        binding.bitRateTV.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun updateBitRateUI(bitrate: Long) {
        val kbps = (bitrate.toFloat() / 8192).roundToLong()
        binding.bitRateTV.text = "$kbps KB/s"
        when {
            kbps >= 100 -> {
                binding.bitRateTV.setTextColor(ContextCompat.getColor(this, R.color.network_state_good))
            }
            kbps >= 70 -> {
                binding.bitRateTV.setTextColor(ContextCompat.getColor(this, R.color.network_state_moderate))
            }
            else -> {
                binding.bitRateTV.setTextColor(ContextCompat.getColor(this, R.color.network_state_poor))
            }
        }
    }

    private fun channelAction(action: String) {


        when (action) {
            "start" -> {
                showLiveCountDown("কয়েক সেকেন্ডের মধ্যে লাইভ শুরু হবে। আপনি লাইভের জন্য প্রস্তুতি নিন।")
                channelState = ChannelState.STARTING
                UserLogger.logGenie("LiveStream_Live_Start")
            }
            "stop" -> {
                updateLiveStatus("replay", archiveUrl)
                if (cameraBase.isStreaming) {
                    stopStreaming()
                }
                showLiveCountDown("চ্যানেল বন্ধ হচ্ছে। অনুগ্রহপূর্বক অপেক্ষা করুন")
                channelState = ChannelState.STOPPING
            }
            "emergency" -> {
                if (cameraBase.isStreaming) {
                    stopStreaming()
                }
                channelState = ChannelState.STOPPING
            }
        }

        // ToDO: remove after test
        /*if (action == "start") {
            startStreamRtp(rmmpUrl)
        }
        return*/

        val title = if (isUrlSafeString(liveTitle)) liveTitle else "live"

        if (instantLive) {

            var userId = SessionManager.courierUserId.toString()
            val requestBody1 = ChannelUpdateRequest(
                userId, liveId.toString(), title,
                facebook, youtube,
                facebookUrl, facebookStream,
                youtubeUrl, youtubeStream,
                action, channelId.toString(),
                "2",
                action == "emergency",
                "400",
                liveEndDateTime
            )
            viewModel.channelUpdateAction(requestBody1).observe(this, Observer { model ->
                hideLiveCountDown()
                if (model.status) {
                    channelId = model.channelId?.toIntOrNull() ?: 0
                    rmmpUrl = model.inputData?.first()?.url ?: ""
                    cloudFrontUrl = model.cloudfront ?: ""
                    archiveUrl = model.archiveUrl ?: ""
                    if (action == "start") {
                        channelState = ChannelState.RUNNING
                        startStreamRtp(rmmpUrl)
                        binding.streamBtn.isEnabled = true
                        hideLiveCountDown()
                        sendLiveStartedNotification()
                    } else {
                        channelState = ChannelState.IDLE
                    }
                    Timber.d("$model")
                }
                this.toast(model.msg)
            })
        } else {

            var userId = SessionManager.courierUserId.toString()
            val requestBody1 = ChannelUpdateRequest(
                userId, liveId.toString(), title,
                facebook, youtube,
                facebookUrl, facebookStream,
                youtubeUrl, youtubeStream,
                action, channelId.toString(),
                "1",
                action == "emergency",
                "400",
                liveEndDateTime
            )
            viewModel.channelUpdateAction(requestBody1).observe(this, Observer { model ->
                hideLiveCountDown()
                if (model.status) {
                    channelId = model.channelId?.toIntOrNull() ?: 0
                    rmmpUrl = model.inputData?.first()?.url ?: ""
                    cloudFrontUrl = model.cloudfront ?: ""
                    archiveUrl = model.archiveUrl ?: ""
                    if (action == "start") {
                        channelState = ChannelState.RUNNING
                        startStreamRtp(rmmpUrl)
                        binding.streamBtn.isEnabled = true
                        hideLiveCountDown()
                        sendLiveStartedNotification()
                    } else {
                        channelState = ChannelState.IDLE
                    }
                    Timber.d("$model")
                }
                this.toast(model.msg)
            })
        }

        when (action) {
            "stop" -> {
                super.onBackPressed()
            }
        }

        /*val requestBody = ChannelActionRequest(
                channelId.toString(),
                action,
                SessionManager.userId.toString(),
                liveId.toString(),
                title
            )
            viewModel.channelAction(requestBody).observe(this, Observer { model ->
                hideLiveCountDown()
                if (model.status) {
                    archiveUrl = model.archiveUrl ?: ""
                    if (action == "start") {
                        channelState = ChannelState.RUNNING
                        startStreamRtp(rmmpUrl)
                    } else {
                        channelState = ChannelState.IDLE
                    }
                }
                this.toast(model.msg)
            })*/

    }

    private fun updateLiveStatus(status: String, url: String) {
        // status: live, upcoming, latest
        val requestBody = LiveStatusUpdateRequest(liveId, status, url)
        viewModel.updateLiveStatus(requestBody).observe(this, Observer { flag ->
        })
    }

    //##################################### SurfaceHolder Callback #####################################//

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Timber.d("RTPService surfaceChanged")
        Timber.d("RTPService format $format width $width height $height")
        if (isFrontCameraDefault) {
            cameraBase.startPreview(CameraHelper.Facing.FRONT)
        } else {
            cameraBase.startPreview()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Timber.d("RTPService surfaceDestroyed")
        stopPreview()
    }

    //##################################### PIP Mode #####################################//

    override fun onBackPressed() {
        //super.onBackPressed()
        if (cameraBase.isStreaming) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                showPictureInPicture()
            } else {
                customAlert("নির্দেশনা", "আপনি কি লাইভটি বন্ধ করতে চান?", "ক্যানসেল", "হ্যাঁ, বন্ধ করবো") {
                    if (it == 1) {
                        super.onBackPressed()
                    }
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (cameraBase.isStreaming) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                showPictureInPicture()
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            viewInPIPMode()
        } else {
            viewOutPIPMode()
        }
    }

    private fun showPictureInPicture() {

        /*val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        if (appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).uid, packageName) == AppOpsManager.MODE_ALLOWED){
            //Picture in Picture is enabled, yay!
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val display = windowManager.defaultDisplay
                val point = Point()
                display.getSize(point)
                val pipParams = PictureInPictureParams.Builder().apply {
                    setAspectRatio(Rational(point.x, point.y))
                }
                enterPictureInPictureMode(pipParams.build())
            } else {
                enterPictureInPictureMode()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                /*or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION*/
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun viewInPIPMode() {
        hideLiveStatus()
        binding.viewCount.visibility = View.GONE
        binding.chronometer.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.liveCountDown.visibility = View.GONE
        binding.chatLayout.isVisible = false
        binding.controlLayout.visibility = View.GONE
    }

    private fun viewOutPIPMode() {
        if (cameraBase.isStreaming) {
            showLiveStatus()
            binding.viewCount.visibility = View.VISIBLE
            binding.chronometer.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.liveCountDown.visibility = View.VISIBLE
        }
        binding.chatLayout.isVisible = true
        binding.controlLayout.visibility = View.VISIBLE
    }

    // Live Group Chat

    private fun initFireBaseDB() {

        Firebase.database.getReference(resources.getString(R.string.app_name)).child("firebaseSettings").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //Timber.tag("chatDebug").d("Firebase settings $snapshot")
                    val settings = snapshot.getValue(FirebaseSettings::class.java)
                    if (settings != null) {

                        try {
                            val firebaseApp = FirebaseApp.getInstance("AjkerdealCustomer")
                            fireBaseDataBase = FirebaseDatabase.getInstance(firebaseApp)
                            firebaseFirestore = FirebaseFirestore.getInstance(firebaseApp)
                        } catch (e: Exception) {
                            val options = FirebaseOptions.Builder()
                                .setApplicationId(settings.applicationId)
                                .setApiKey(settings.apiKey)
                                .setDatabaseUrl(settings.databaseUrl)
                                .setProjectId(settings.projectId)
                                .build()
                            FirebaseApp.initializeApp(applicationContext, options, "AjkerdealCustomer")
                            val firebaseApp = FirebaseApp.getInstance("AjkerdealCustomer")
                            fireBaseDataBase = FirebaseDatabase.getInstance(firebaseApp)
                            firebaseFirestore = FirebaseFirestore.getInstance(firebaseApp)
                        }

                        // init services
                        initChat()
                        initLiveCount()
                        initLikeCount()
                        valueEventLister()
                        initProductHighlight()
                    } else {
                        Timber.tag("chatDebug").d("Firebase settings not found")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag("chatDebug").d("chatRoomReference ${error.message}")
            }
        })
    }

    private fun initChat() {

        dataAdapter = ChatAdapter()
        val linearLayoutManager = LinearLayoutManager(this)
        //linearLayoutManager.stackFromEnd = true
        with(binding.recyclerView) {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = dataAdapter
        }

        val dbRef = fireBaseDataBase.getReference("LiveShow")
        chatRoomRef = dbRef.child("chat").child("groupChat").child(liveId.toString())
        //chatRoomRef.removeValue()
        chatRoomRef.orderByKey().limitToLast(queryLimit).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //Timber.tag("chatDebug").d("init load $snapshot")
                    val historyList: MutableList<ChatData> = mutableListOf()
                    snapshot.children.forEach { dataSnapshot ->
                        val model = dataSnapshot.getValue(ChatData::class.java)
                        model?.let {
                            historyList.add(it)
                        }
                    }
                    //historyList.reverse()
                    Timber.tag("chatDebug").d("init load $historyList")
                    dataAdapter.initLoad(historyList)
                    binding.recyclerView.postDelayed({
                        binding.recyclerView.smoothScrollToPosition(dataAdapter.itemCount)
                    }, 200L)

                } else {
                    //binding?.emptyView?.visibility = View.VISIBLE
                    Timber.tag("chatDebug").d("chatRoomReference $snapshot")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //binding?.progressBar?.visibility = View.GONE
                //binding?.emptyView?.visibility = View.VISIBLE
                Timber.tag("chatDebug").d("chatRoomReference ${error.message}")
            }
        })

        //valueEventLister() resume called

        binding.chatBox.setOnClickListener {
            val dialog = ChatBoxBottomSheet.newInstance()
            val tag = ChatBoxBottomSheet.tag
            dialog.show(supportFragmentManager, tag)
            dialog.onItemClicked = { message ->
                dialog.dismiss()
                Timber.d(message)
                hideKeyboard()
                sendChatMessage(message)
            }
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    Timber.d("onScrolled ${!isLoading} $currentItemCount <= ${firstVisibleItem + visibleThreshold}")
                    if (!isLoading && firstVisibleItem < 5 && currentItemCount > queryLimit - 1 /*&& currentItemCount < totalCount*/) {
                        isLoading = true
                        val endKey = dataAdapter.firstItem().key
                        fetchMoreHistory(endKey)

                    }
                }
            }
        })

    }

    private fun sendChatMessage(msg: String) {
        val key = chatRoomRef.push().key ?: ""
        val date = Date().time
        val model = ChatData(key, SessionManager.courierUserId.toString(), SessionManager.companyName, SessionManager.profileImgUri, msg, sdf.format(date), date.toString(), true)
        chatRoomRef.child(key).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.d("Msg send successfully")
            } else {
                Timber.d("Msg send error. ${it.exception?.message}")
                Timber.d(it.exception)
            }
        }
    }

    private fun valueEventLister() {

        valueEventListener = chatRoomRef.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.tag("chatDebug").d(snapshot.toString())
                val historyList: MutableList<ChatData> = mutableListOf()
                snapshot.children.forEach { dataSnapshot ->
                    val model = dataSnapshot.getValue(ChatData::class.java)
                    model?.let {
                        if (!dataAdapter.isDataExist(it)) {
                            historyList.add(it)
                        }
                    }
                }
                //historyList.reverse()
                if (historyList.isNotEmpty()) {
                    dataAdapter.addNewData(historyList)
                    binding.recyclerView.smoothScrollToPosition(dataAdapter.itemCount)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag("chatDebug").d("agentHistory valueEventListener ${error.message}")
            }
        })

    }

    private fun fetchMoreHistory(endKey: String?) {
        Timber.tag("chatDebug").d("fetchMoreHistory $endKey")
        chatRoomRef.orderByKey().endAt(endKey).limitToLast(queryLimit).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    isLoading = false
                    Timber.tag("chatDebug").d("lazy load $snapshot")
                    val historyList: MutableList<ChatData> = mutableListOf()
                    snapshot.children.forEach { dataSnapshot ->
                        val model = dataSnapshot.getValue(ChatData::class.java)
                        model?.let {
                            historyList.add(it)
                        }
                    }
                    //historyList.reverse()
                    Timber.tag("chatDebug").d("lazy load $historyList")
                    if (historyList.isNotEmpty()) {
                        historyList.removeLast()
                    }
                    dataAdapter.pagingLoadReverse(historyList)
                    if (historyList.size < queryLimit - 1) {
                        isLoading = true
                    }

                } else {
                    isLoading = true
                }

            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = true
                Timber.tag("chatDebug").d("agentHistory fetchMoreHistory ${error.message}")
            }

        })
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Timber.d("RTPService onNetworkConnectionChanged $isConnected")
        if (!isConnected) {
            snackBar = Snackbar.make(binding.parent, "ইন্টারনেট সংযোগ নেই", Snackbar.LENGTH_LONG)
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }

    private fun liveTimeOut() {

        try {
            liveDate = liveDate?.split("T")?.first() ?: ""
            val liveStartDateTime = "$liveDate $liveStartTime" // 2020-12-04 10:00:00
            val liveEndDateTime = "$liveDate $liveEndTime"

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val liveStartStamp: Date? = sdf.parse(liveStartDateTime)
            val liveEndStamp: Date? = sdf.parse(liveEndDateTime)
            val currentTimeStamp: Date = Date()

            val liveTimeOut = (liveEndStamp?.time ?: 0) - currentTimeStamp.time

            binding.chronometer.apply {
                visibility = View.VISIBLE
                base = SystemClock.elapsedRealtime() + liveTimeOut
                binding.progressBar.max = liveTimeOut.toInt()
                binding.progressBar.progress = liveTimeOut.toInt()
                binding.progressBar.visibility = View.VISIBLE
                start()
            }
            binding.chronometer.setOnChronometerTickListener { chronometer ->
                val elapsed = chronometer.base - SystemClock.elapsedRealtime()
                binding.progressBar.progress = elapsed.toInt()
                if (elapsed <= 0L) {
                    chronometer.stop()
                    chronometer.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.liveCountDown.visibility = View.GONE
                    if (cameraBase.isStreaming) {
                        channelAction("stop")
                    }
                }
                //Timber.d("chronometerTickListener  $elapsed")
            }
            binding.liveCountDown.visibility = View.VISIBLE

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initLiveCount() {
        val dbFirestore = firebaseFirestore.collection("liveshow").document("views")
        dbFirestoreViews = dbFirestore.collection(liveStreamId)
        dbFirestoreViews.document("totalView").get().addOnSuccessListener { document ->
            if (!document.exists()) {
                Timber.tag("firebaseDebug").d("document do not exist")
                dbFirestoreViews.document("currentView").set(ViewCount(0))
                dbFirestoreViews.document("totalView").set(ViewCount(0))
            } else {
                Timber.tag("firebaseDebug").d("${document.data}")
                val viewerCount = document.getLong("view") ?: 0L
                val viewerCountStr = if (viewerCount >= 1000L) {
                    "${DigitConverter.formatDecimal(viewerCount / 1000f)}k"
                } else {
                    viewerCount.toString()
                }
                binding.viewCount.text = "${DigitConverter.toBanglaDigit(viewerCountStr)} জন দেখছেন"
                Timber.tag("firebaseDebug").d("totalView $viewerCountStr")
                if (binding.viewCount.visibility == View.GONE) {
                    binding.viewCount.visibility = View.VISIBLE
                }
            }
        }.addOnFailureListener {
            Timber.tag("firebaseDebug").d(it)
        }

        currentViewLister = dbFirestoreViews.document("currentView").addSnapshotListener(currentViewEventListener)
    }

    private val currentViewEventListener = EventListener<DocumentSnapshot> { value, error ->
        if (value != null && value.exists()) {
            val viewerCount = value.getLong("view") ?: 0L
            val viewerCountStr = if (viewerCount >= 1000L) {
                "${DigitConverter.formatDecimal(viewerCount / 1000f)}k"
            } else {
                if (viewerCount > 0L) {
                    viewerCount.toString()
                } else {
                    "0"
                }
            }
            binding.viewCount.text = "${DigitConverter.toBanglaDigit(viewerCountStr)} জন দেখছেন"
            Timber.tag("firebaseDebug").d("currentView $viewerCountStr")
            if (binding.viewCount.visibility == View.GONE) {
                binding.viewCount.visibility = View.VISIBLE
            }
        }
    }

    private fun initLikeCount() {
        val dbFirestore = firebaseFirestore.collection("liveshow").document("likes")
        dbFirestoreLikes = dbFirestore.collection(liveStreamId)
        dbFirestoreLikes.document("totalLike").get().addOnSuccessListener { document ->
            if (!document.exists()) {
                Timber.tag("firebaseDebug").d("document do not exist")
                dbFirestoreLikes.document("totalLike").set(LikeCount(0))
            } else {
                Timber.tag("firebaseDebug").d("${document.data}")
            }
        }.addOnFailureListener {
            Timber.tag("firebaseDebug").d(it)
        }
        currentLikeLister = dbFirestoreLikes.document("totalLike").addSnapshotListener(currentLikeEventListener)
    }

    private val currentLikeEventListener = EventListener<DocumentSnapshot> { value, error ->
        if (value != null && value.exists()) {
            val currentLikeCount = value.getLong("like") ?: 0L
            if (currentLikeCount > likeCount) {
                likeCount = currentLikeCount
                //binding?.reactionLottie?.setAnimation(R.raw.like_react)
                binding.reactionLottie.playAnimation()
            }
            Timber.tag("firebaseDebug").d("totalLike $currentLikeCount")
        }
    }

    private fun showLiveCountDown(msg: String) {
        val countUpTo = 90 * 1000L
        binding.circularProgress.setSpeed(0f)
        binding.circularProgressLayout.visibility = View.VISIBLE
        binding.progressMsg.text = msg
        countDownTimer = object : CountDownTimer(countUpTo, 1000L) {
            override fun onTick(millisUntilFinished: Long) {

                val limit = ((countUpTo - millisUntilFinished) / 1000f).roundToInt()
                binding.circularProgress.setSpeed(limit * 0.133f)
                //Timber.d("circularProgress onTick $millisUntilFinished")
                //Timber.d("circularProgress $limit")
            }
            override fun onFinish() {
                //Timber.d("circularProgress Finished")
                binding.circularProgress.setSpeed(10f)
                //binding.circularProgressLayout.visibility = View.GONE
            }
        }.start()
    }

    private fun hideLiveCountDown() {
        countDownTimer?.cancel()
        binding.circularProgressLayout.visibility = View.GONE
    }

    private fun sendLiveStartedNotification() {
        val requestBody = LiveStartedNotifyRequest(SessionManager.courierUserId, liveId.toString())
        viewModel.liveStartedNotify(requestBody)
    }

    private fun initProductHighlight() {

        val dbRef = fireBaseDataBase.getReference("LiveShow")
        productHighLightRef = dbRef.child("productHighlight").child(liveStreamId)

        dataAdapterProduct = ProductHighlightAdapter()
        with(binding.recyclerViewProduct) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@LiveActivity, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = null
            adapter = dataAdapterProduct
        }
        dataAdapterProduct.onItemClick = { model1, position, isSelected ->
            val model: LiveProductEvent? = if (isSelected) {
                this.toast("Product showing to viewer")
                LiveProductEvent(Date().time, model1.coverPhoto, model1.productPrice, model1.id)
            } else {
                this.toast("Product hidden from viewer")
                LiveProductEvent(Date().time, "", 0)
            }
            productHighLightRef.setValue(model)
        }

        var toggleFlag = false
        binding.cart.setOnClickListener {
            if (!toggleFlag) {
                binding.recyclerViewProduct.isVisible = true
                toggleFlag = true
            } else {
                binding.recyclerViewProduct.isVisible = false
                toggleFlag = false
            }
            loadProduct()
        }

    }

    private fun loadProduct() {
        viewModel.fetchLiveProducts(liveId).observe(this, Observer { list ->
            if (list.isNotEmpty()) {
                dataAdapterProduct.initList(list)
            }
        })
    }

}