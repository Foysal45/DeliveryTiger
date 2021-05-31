package com.bd.deliverytiger.app.ui.live.live_product_insert

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductInsertData
import com.bd.deliverytiger.app.databinding.FragmentLiveProductBottomSheetBinding
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import kotlin.concurrent.thread

class LiveProductBottomSheet(): BottomSheetDialogFragment() {

    private var binding: FragmentLiveProductBottomSheetBinding? = null
    var onProductAddClicked: ((model: LiveProductInsertData) -> Unit)? = null

    private var coverUrl: String = ""

    companion object {
        fun newInstance(): LiveProductBottomSheet = LiveProductBottomSheet().apply {

        }
        val tag: String = LiveProductBottomSheet::class.java.name
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
        //dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return FragmentLiveProductBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.coverPhotoUpload?.setOnClickListener {
            pickUpImage()
        }
        binding?.coverPhotoEdit?.setOnClickListener {
            pickUpImage()
        }
        binding?.coverPhoto?.setOnClickListener {
            pickUpImage()
        }

        binding?.addBtn?.setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct() {

        val price = binding?.productPrice?.text?.toString()?.trim()?.toIntOrNull() ?: 0
        if (price <= 0) {
            context?.toast("অনুগ্রহপূর্বক প্রোডাক্টের দাম লিখুন")
            binding?.productPrice?.requestFocus()
            return
        }
        if (coverUrl.isEmpty()) {
            context?.toast("অনুগ্রহপূর্বক প্রোডাক্টের ছবি যোগ করুন")
            return
        }

        val model = LiveProductInsertData(price, price,"", coverUrl, SessionManager.courierUserId)

        onProductAddClicked?.invoke(model)

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

            binding?.coverPhotoUpload?.visibility = View.GONE
            binding?.coverPhotoEdit?.visibility = View.VISIBLE
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

    override fun onCancel(dialog: DialogInterface) {
        hideKeyboard()
        super.onCancel(dialog)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}