package com.bd.deliverytiger.app.ui.live.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.profile.ProfileData
import com.bd.deliverytiger.app.databinding.FragmentLiveProfileBinding
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import org.koin.android.ext.android.inject
import timber.log.Timber


class LiveProfileFragment(): Fragment() {

    private var binding: FragmentLiveProfileBinding? = null
    private val viewModel: LiveProfileFragmentViewModel by inject()

    private var isFacebook: Boolean = false
    private var isYoutube: Boolean = false
    private var profileImgUrl: String = ""

    companion object {
        fun newInstance(): LiveProfileFragment = LiveProfileFragment()
        val tag: String = LiveProfileFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLiveProfileBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickLister()

        if (SessionManager.facebookPageLinkEnable) {
            binding?.fbPageLinkLayout?.isVisible = true
        }

        if (SessionManager.profileImgUri.isNotEmpty()) {
            binding?.profilePic?.let { view ->
                Glide.with(view)
                    .load(SessionManager.profileImgUri)
                    .apply(RequestOptions().placeholder(R.drawable.ic_person_circle).error(R.drawable.ic_person_circle).circleCrop())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(view)

            }

            /*val userPic: ImageView = (binding?.profilePic ?: R.drawable.ic_person_circle) as ImageView
            binding?.profilePic?.let { view ->
                Glide.with(this)
                    .load(sessionManager.profileImage)
                    .apply(RequestOptions().placeholder(R.drawable.ic_person_circle).error(R.drawable.ic_person_circle).circleCrop())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(userPic)

            }*/
        }
        fetchProfileData()

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
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

    private fun initClickLister() {

        /*binding?.profilePicEdit?.setOnClickListener {
            pickUpImage()
        }*/

        binding?.facebookGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.facebookNo -> {
                    isFacebook = false
                    binding?.fbStreamLayout?.isVisible = false
                }
                R.id.facebookYes -> {
                    isFacebook = true
                    binding?.fbStreamLayout?.isVisible = true
                }
            }
        }

        binding?.youtubeGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.youtubeNo -> {
                    isYoutube = false
                    binding?.youtubeStreamLayout?.isVisible = false
                }
                R.id.youtubeYes -> {
                    isYoutube = true
                    binding?.youtubeStreamLayout?.isVisible = true
                }
            }
        }

        binding?.saveBtn?.setOnClickListener {
            if (validation()) {
                updateUserProfile()
            }
        }

    }

    private fun validation(): Boolean {
        if (isFacebook) {
            val fbStreamUrl = binding?.fbStreamUrl?.text?.toString()?: ""
            val fbStreamKey = binding?.fbStreamKey?.text?.toString()?: ""

            if (fbStreamUrl.isEmpty() && fbStreamKey.isEmpty()) {
                context?.toast("Please Enter \"FB Stream Url\" and \"FB Stream Key\"")
                return false
            } else if (fbStreamUrl.isEmpty()) {
                context?.toast("Please Enter \"FB Stream Url\"")
                return false
            } else if (fbStreamKey.isEmpty()) {
                context?.toast("Please Enter \"FB Stream Key\"")
                return false
            } else {
                SessionManager.fbStreamURL = fbStreamUrl
                SessionManager.fbStreamKey = fbStreamKey
            }
        } else {
            SessionManager.fbStreamURL = ""
            SessionManager.fbStreamKey = ""
        }

        if (isYoutube) {
            val youtubeStreamUrl = binding?.youtubeStreamUrl?.text?.toString()?: ""
            val youtubeStreamKey = binding?.youtubeStreamKey?.text?.toString()?: ""

            if (youtubeStreamUrl.isEmpty() && youtubeStreamKey.isEmpty()) {
                context?.toast("Please Enter \"Youtube Stream Url\" and \"Youtube Stream Key\"")
                return false
            } else if (youtubeStreamUrl.isEmpty()) {
                context?.toast("Please Enter \"Youtube Stream Url\"")
                return false
            } else if (youtubeStreamKey.isEmpty()) {
                context?.toast("Please Enter \"Youtube Stream Key\"")
                return false
            } else {
                SessionManager.youtubeStreamURL = youtubeStreamUrl
                SessionManager.youtubeStreamKey = youtubeStreamKey
            }
        } else {
            SessionManager.youtubeStreamURL = ""
            SessionManager.youtubeStreamKey = ""
        }

        return true
    }

    private fun pickUpImage() {
        if (!isStoragePermissions()) {
            return
        }
        try {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
            }.also {
                //getImages.launch(it)
            }
        } catch (e: Exception) {
            Timber.d(e)
            context?.toast("No Application found to handle this action")
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

    /*private val getImages = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val fileUri = result?.data?.data
            Timber.d("FileUri: $fileUri")
            val actualPath = FileUtils(requireContext()).getPath(fileUri)
            Timber.d("FilePath: $actualPath")
            profileImgUrl = actualPath

            binding?.profilePic?.let { view ->
                Glide.with(view)
                    .load(actualPath)
                    .apply(RequestOptions().placeholder(R.drawable.ic_person_circle).error(R.drawable.ic_person_circle).circleCrop())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(view)
            }
            sessionManager.profileImage = actualPath
        }

    }*/

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

    private fun fetchProfileData() {

        binding?.userPhoneNumber?.setText(SessionManager.mobile)
        // API is called here

        var userId = SessionManager.courierUserId
        viewModel.fetchUserProfile(userId).observe(viewLifecycleOwner, Observer {
            //Toast.makeText(requireContext(), ""+it, Toast.LENGTH_SHORT).show()

            binding?.userName?.setText(it.name)
            binding?.fbPageLink?.setText(it.fbPageUrl)

            if ((it.fbStreamUrl?.isNotEmpty() == true) && (it.fbStreamKey?.isNotEmpty() == true)) {
                binding?.facebookYes?.isChecked = true
                binding?.fbStreamUrl?.setText(it.fbStreamUrl)
                binding?.fbStreamKey?.setText(it.fbStreamKey)
            } else {
                binding?.facebookNo?.isChecked = true
                binding?.fbStreamUrl?.setText("")
                binding?.fbStreamKey?.setText("")
            }

            if ((it.youtubeStreamUrl?.isNotEmpty() == true) && (it.youtubeStreamKey?.isNotEmpty() == true)) {
                binding?.youtubeYes?.isChecked = true
                binding?.youtubeStreamUrl?.setText(it.youtubeStreamUrl)
                binding?.youtubeStreamKey?.setText(it.youtubeStreamKey)
            } else {
                binding?.youtubeNo?.isChecked = true
                binding?.youtubeStreamUrl?.setText("")
                binding?.youtubeStreamKey?.setText("")
            }

        })
    }

    private fun updateUserProfile() {

        val userName = binding?.userName?.text.toString()
        val customerId = 0

        var userId = SessionManager.courierUserId
        val facebookPageLinkEnable = SessionManager.facebookPageLinkEnable
        val fbPageLink = binding?.fbPageLink?.text?.toString()?.trim() ?: ""
        val fbStreamUrl = binding?.fbStreamUrl?.text?.toString()?.trim() ?: ""
        val fbStreamKey = binding?.fbStreamKey?.text?.toString()?.trim() ?: ""
        val youtubeStreamUrl = binding?.youtubeStreamUrl?.text?.toString()?.trim() ?: ""
        val youtubeStreamKey = binding?.youtubeStreamKey?.text?.toString()?.trim() ?: ""

        val updateProfileModel = ProfileData(userName, userId, customerId, facebookPageLinkEnable, fbPageLink, fbStreamUrl, fbStreamKey, youtubeStreamUrl, youtubeStreamKey)

        //no more needed here I guess
        /*if (profileImgUrl.isNotEmpty()) {
            val progressDialog = progressDialog("প্রোফাইল আপডেট হচ্ছে, অপেক্ষা করুন")
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            viewModel.uploadProfilePhoto(requireContext(), profileImgUrl).observe(viewLifecycleOwner, Observer { model ->
                Timber.d("ProfileImageUpload ${model.message}")
                if (model.message) {
                    progressDialog.dismiss()
                    Timber.tag("ProfileFragment").d("ProfileImageUpload")
                    context?.toast("প্রোফাইলের ছবি সফলভাবে আপডেট হয়েছে")
                    if (model.imageUrl != null) sessionManager.profileImage = model.imageUrl!!
                }
            })
        }*/

        viewModel.updateUserProfile(updateProfileModel).observe(viewLifecycleOwner, Observer { flag ->
            if (flag) {
                context?.toast("প্রোফাইল সফলভাবে আপডেট হয়েছে")
                SessionManager.updateProfile(ProfileData(userName,0, SessionManager.courierUserId, facebookPageLinkEnable, fbPageLink, fbStreamUrl, fbStreamKey, youtubeStreamUrl, youtubeStreamKey))
            }

        })

    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

}