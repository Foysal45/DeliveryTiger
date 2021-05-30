package com.bd.deliverytiger.app.ui.live.live_product_insert

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductInsertData
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductTemp
import com.bd.deliverytiger.app.databinding.FragmentLiveProductInsertBinding
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class LiveProductInsertFragment(): Fragment() {

    private var binding: FragmentLiveProductInsertBinding? = null
    private val viewModel: LiveProductInsertViewModel by inject()
    private lateinit var productAdapter: ProductAdapter

    private var liveId: Int = 0
    private var suggestedPrice: String = "'"
    private var listPosition: Int = -1

    companion object {
        fun newInstance(liveId: Int): LiveProductInsertFragment = LiveProductInsertFragment().apply {
            this.liveId = liveId
        }
        val tag: String = LiveProductInsertFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLiveProductInsertBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liveId = arguments?.getInt("liveId", 0) ?: 0
        suggestedPrice = arguments?.getString("suggestedPrice") ?: ""
        Timber.d("LiveProductInsertDebug $liveId $suggestedPrice")

        initProductList()
        initClickLister()
    }

    private fun initClickLister() {

        binding?.addProductBtn?.setOnClickListener {
            hideKeyboard()
            val model = productAdapter.getList().last()
            if (model.imageUrl.isNotEmpty() && model.price > 0) {
                productAdapter.addItem(LiveProductTemp())
            } else {
                context?.toast("প্রোডাক্টের সম্পূর্ণ তথ্য দিন")
            }

        }
        binding?.uploadBtn?.setOnClickListener {
            hideKeyboard()
            insertProduct(liveId)
        }

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

    private fun initProductList() {
        productAdapter = ProductAdapter()
        productAdapter.addItem(LiveProductTemp())
        with(binding?.recyclerView!!) {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
        productAdapter.onItemRemove = { model, position ->
            hideKeyboard()
            if (productAdapter.getList().isEmpty()){
                binding?.recyclerView?.visibility = View.GONE
                //binding?.productListTitle?.visibility = View.GONE
            }
        }
        productAdapter.onImagePick = { model, position ->
            listPosition = position
            pickUpImage()
        }
        productAdapter.onMsg = { model, position ->
            context?.toast("কমপক্ষে একটি প্রোডাক্ট থাকতে হবে")
        }
    }

    private fun showAddProductDialog() {
        val dialog = LiveProductBottomSheet.newInstance()
        val tag = LiveProductBottomSheet.tag
        dialog.show(childFragmentManager, tag)
        dialog.onProductAddClicked = { model ->
            dialog.dismiss()
            Timber.tag("LiveScheduleFragment").d("onProductAddClicked $model")
            //productAdapter.addItem(model)
            if (productAdapter.getList().isNotEmpty()){
                binding?.recyclerView?.visibility = View.VISIBLE
                //binding?.productListTitle?.visibility = View.VISIBLE
            }
        }
    }


    private fun insertProduct(liveId: Int) {
        val productList: MutableList<LiveProductInsertData> = mutableListOf()

        var userId = SessionManager.courierUserId
        productAdapter.getList().forEach { model ->
            if (model.imageUrl.isNotEmpty() && model.price > 0) {
                productList.add(
                    LiveProductInsertData(
                        model.price,
                        model.price,
                        "",
                        model.imageUrl,
                        userId,
                        liveId
                    )
                )
            }
        }
        if (productList.isEmpty()) {
            context?.toast("প্রোডাক্ট যোগ করুন")
            return
        }
        binding?.uploadBtn?.text = "আপলোডিং"
        binding?.uploadBtn?.isEnabled = false
        binding?.addProductBtn?.isEnabled = false
        viewModel.insertLiveProducts(requireContext(), productList).observe(viewLifecycleOwner, Observer { uploadStatus ->
            binding?.uploadBtn?.text = "অ্যাড করুন"
            binding?.addProductBtn?.isEnabled = true
            binding?.uploadBtn?.isEnabled = true
            if (uploadStatus) {
                context?.toast("লাইভ প্রোডাক্ট আপলোড সাকসেসফুল")
                Timber.tag("LiveScheduleFragment").d("insertLiveProducts")
                productAdapter.clearAndAddTemp(LiveProductTemp())
            }
        })
    }

    private fun quickUpload(liveId: Int) {
        //TODO: Remove this 7
        /*Intent(requireContext(), QuickLiveProductInsertActivity::class.java).apply {
            putExtra("liveId", liveId)
            putExtra("suggestedPrice", suggestedPrice)
        }.also {
            startActivity(it)
        }*/
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
            productAdapter.updateImage(actualPath, listPosition)
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}