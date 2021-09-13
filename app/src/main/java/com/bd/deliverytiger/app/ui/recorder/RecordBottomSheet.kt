package com.bd.deliverytiger.app.ui.recorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentRecordBottomSheetBinding
import com.bd.deliverytiger.app.utils.FileType
import com.bd.deliverytiger.app.utils.createNewFile
import com.bd.deliverytiger.app.utils.createNewFilePath
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class RecordBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentRecordBottomSheetBinding? = null

    var onRecordingComplete: ((url: String) -> Unit)? = null

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

    private var fileName: String = ""

    private var isPlaying: Boolean = false

    companion object {

        fun newInstance(): RecordBottomSheet = RecordBottomSheet().apply {}

        val tag: String = RecordBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRecordBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickLister()
        initData()
    }

    private fun initView() {
        if (isAudioRecordingPermission()) {
            output = createNewFilePath(requireContext(), FileType.Audio)
            fileName = output!!
            Timber.d("debugAudio $output")
            try {
                mediaRecorder = MediaRecorder()
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder?.setOutputFile(output)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun initClickLister() {
        binding?.recordBtn?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
            } else {
                if (state) {
                    stopRecording()
                } else {
                    startRecording()
                }
            }
        }

        binding?.sendAudioBtn?.setOnClickListener {
            if (!output.isNullOrEmpty()) {
                onRecordingComplete?.invoke(output ?: "")
                dialog?.dismiss()
            }
            else{
                context?.toast("No Audio Found, Record First")
            }
        }

        binding?.playBtn?.setOnClickListener {
            if (!isPlaying) {
                if (fileName.isNotEmpty()) {
                    playRecording(requireContext(), fileName)
                    binding?.playBtn?.setIconResource(R.drawable.ic_stop)
                    isPlaying = true
                }
            } else {
                binding?.playBtn?.setIconResource(R.drawable.ic_play)
                isPlaying = false
                stopPlayer()
            }
        }
    }

    private fun initData() {

    }

    private fun playRecording(context: Context, title: String) {

        val path = output
        val manager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (manager.isMusicActive) {
            context?.toast("Another recording is just playing! Wait until it's finished!")
        } else {
            MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(context, Uri.parse(path))
                prepare()
                start()
            }
        }

    }

    private fun stopPlayer() {
        MediaPlayer().apply {
            stop()
            release()
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
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
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
            binding?.recordBtn?.setIconResource(R.drawable.ic_mic)
            binding?.chronometer?.stop()
            context?.toast("$output")

        } else {
            context?.toast("You are not recording right now!")
        }
    }

    private fun isAudioRecordingPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission1 = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            )
            permission1 == PackageManager.PERMISSION_GRANTED
        } else {
            true
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