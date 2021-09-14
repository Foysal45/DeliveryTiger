package com.bd.deliverytiger.app.ui.recorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentRecordBottomSheetBinding
import com.bd.deliverytiger.app.utils.FileType
import com.bd.deliverytiger.app.utils.createNewFilePath
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import java.io.File
import java.io.IOException
import kotlin.concurrent.thread

class RecordBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentRecordBottomSheetBinding? = null
    var onRecordingComplete: ((url: String) -> Unit)? = null

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    private var isAudioRecording: Boolean = false
    private var audioFilePath: String = ""
    private var isPlaying: Boolean = false

    companion object {
        fun newInstance(): RecordBottomSheet = RecordBottomSheet().apply {}
        val tag: String = RecordBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentRecordBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickLister()
    }

    private fun initClickLister() {

        binding?.recordBtn?.setOnClickListener {
            if (isAudioPermissions()) {
                if (isAudioRecording) {
                    stopRecording()
                } else {
                    startRecording()
                }
            }
        }

        binding?.playBtn?.setOnClickListener {
            if (!isPlaying) {
                if (audioFilePath.isNotEmpty() && File(audioFilePath).exists()) {
                    playRecording(audioFilePath)
                    binding?.playBtn?.setIconResource(R.drawable.ic_stop)
                    isPlaying = true
                } else {
                    context?.toast("অডিও রেকর্ড করুন")
                }
            } else {
                binding?.playBtn?.setIconResource(R.drawable.ic_play)
                isPlaying = false
                mediaPlayer?.run {
                    pause()
                }
            }
        }

        binding?.saveAudioBtn?.setOnClickListener {
            if (audioFilePath.isNotEmpty() && File(audioFilePath).exists()) {
                onRecordingComplete?.invoke(audioFilePath)
            } else {
                context?.toast("অডিও রেকর্ড করুন")
            }
        }

        binding?.cancelBtn?.setOnClickListener {
            dismiss()
        }

    }

    private fun playRecording(audioFilePath: String) {
        try {
            val manager: AudioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (manager.isMusicActive) {
                context?.toast("Another recording is just playing! Wait until it's finished!")
            } else {
                try {
                    mediaPlayer = MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource(requireContext(), Uri.parse(audioFilePath))
                        prepare()
                        start()
                    }
                    mediaPlayer?.setOnCompletionListener {
                        it.pause()
                        binding?.playBtn?.setIconResource(R.drawable.ic_play)
                        isPlaying = false
                    }

                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopPlayer() {
        try {
            mediaPlayer?.run {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startRecording() {
        try {
            audioFilePath = createNewFilePath(requireContext(), FileType.Audio)
            Timber.d("debugAudio $audioFilePath")
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }
            isAudioRecording = true
            binding?.chronometer?.let { chronometer ->
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
            }
            binding?.recordBtn?.setIconResource(R.drawable.ic_stop)
            binding?.recordBtn?.setIconTintResource(R.color.red)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        try {
            if (isAudioRecording) {
                mediaRecorder?.run {
                    stop()
                    release()
                }
                isAudioRecording = false
                binding?.recordBtn?.setIconResource(R.drawable.ic_mic)
                binding?.chronometer?.stop()
                context?.toast("Audio file saved")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopMedia() {
        stopPlayer()
        stopRecording()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMedia()
    }

    private fun isAudioPermissions(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
        val permission2 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return when {
            permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED -> {
                true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                false
            }
            else -> {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                false
            }
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var isPermission1: Boolean = false
        var isPermission2: Boolean = false
        permissions.entries.forEach { permission ->
            if (permission.key == Manifest.permission.RECORD_AUDIO) {
                isPermission1 = permission.value
            }
            if (permission.key == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                isPermission2 = permission.value
            }
        }
        if (isPermission1) {
            binding?.recordBtn?.performClick()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(false)
        val bottomSheet: FrameLayout? =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parentLayout?.height ?: 300
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = false

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}