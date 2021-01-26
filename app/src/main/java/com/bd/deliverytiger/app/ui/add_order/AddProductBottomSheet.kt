package com.bd.deliverytiger.app.ui.add_order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.product_upload.ProductUploadRequest
import com.bd.deliverytiger.app.databinding.FragmentAddProductBottomSheetBinding
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import java.io.File
import kotlin.concurrent.thread

class AddProductBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentAddProductBottomSheetBinding? = null
    var onProductUploaded: ((dealId: Int, offerType: Int) -> Unit)? = null

    private val productUploadRequest = ProductUploadRequest()
    private var imagePath: String = ""

    private val mediaTypeText = "text/plain".toMediaTypeOrNull()
    private val mediaTypeMultipart = "multipart/form-data".toMediaTypeOrNull()
    private val mediaTypeOctet = "application/octet".toMediaTypeOrNull()
    private val gson: Gson by inject()
    private val viewModel: AddProductViewModel by inject()
    private val appRepository: AppRepository by inject()

    private var offerType = 0

    companion object {

        fun newInstance(offerType: Int): AddProductBottomSheet = AddProductBottomSheet().apply {
            this.offerType = offerType
        }

        val tag: String = AddProductBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()

        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(false)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        //val metrics = resources.displayMetrics

        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parent?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                //state = BottomSheetBehavior.STATE_COLLAPSED
                skipCollapsed = true
                isHideable = false

            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentAddProductBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.image?.setOnClickListener {
            pickUpImage()
        }

        binding?.uploadBtn?.setOnClickListener {

            hideKeyboard()
            if (validate()) {

                val progressDialog = progressDialog()
                progressDialog.show()
                val jsonModel = gson.toJson(productUploadRequest)
                binding?.uploadBtn?.isEnabled = false
                viewModel.uploadProductInfo(jsonModel.toRequestBody(mediaTypeText)).observe(viewLifecycleOwner, Observer { model ->

                    val uploadLocation = "images/deals/${model.folderName}"
                    val imageFile = File(imagePath)
                    if (imageFile.exists()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val compressedFile = Compressor.compress(requireContext(), imageFile)
                            val body = compressedFile.readBytes().toRequestBody(mediaTypeOctet)
                            //val requestFile = compressedFile.asRequestBody(mediaTypeMultipart)
                            //val body = MultipartBody.Part.createFormData("file", compressedFile.name, requestFile)
                            /*viewModel.uploadProductImage(uploadLocation, productUploadRequest.productTitle, body).observe(viewLifecycleOwner, Observer {
                                binding?.uploadBtn?.isEnabled = true
                                onProductUploaded?.invoke(model.dealId, offerType)
                            })*/
                            val imageUploadResponse = appRepository.uploadProductImage(uploadLocation, productUploadRequest.productTitle, body)
                            withContext(Dispatchers.Main) {
                                progressDialog.dismiss()
                                if (imageUploadResponse is NetworkResponse.Success) {
                                    binding?.uploadBtn?.isEnabled = true
                                    onProductUploaded?.invoke(model.dealId, offerType)
                                } else {
                                    //context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
                                    binding?.uploadBtn?.isEnabled = true
                                    onProductUploaded?.invoke(model.dealId, offerType)
                                }
                            }
                        }
                    }
                })
            }
        }

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

    private fun validate(): Boolean {

        val productTitle = binding?.productTitleTV?.text?.toString() ?: ""
        if (productTitle.trim().isEmpty()) {
            context?.toast("প্রোডাক্টের টাইটেল লিখুন")
            return false
        }

        val productPrice = binding?.productPriceTV?.text?.toString() ?: "0"
        if (productPrice.trim().isEmpty()) {
            context?.toast("প্রোডাক্টের দাম লিখুন")
            return false
        }
        val productPriceNumber = productPrice.toInt()
        if (productPriceNumber <= 0) {
            context?.toast("প্রোডাক্টের সঠিক দাম লিখুন")
            return false
        }

        val productDescription = binding?.productDescriptionTV?.text?.toString() ?: ""
        if (productDescription.trim().isEmpty()) {
            context?.toast("প্রোডাক্টের বিবরণ লিখুন")
            return false
        }

        if (imagePath.isEmpty()) {
            context?.toast("প্রোডাক্টের ছবি অ্যাড করুন")
            return false
        }

        productUploadRequest.apply {
            this.productTitle = productTitle
            productTitleEng = productTitle
            this.productPrice = productPriceNumber
            this.productDescription = productDescription
            mobileNumber = SessionManager.mobile
            customerId = SessionManager.courierUserId
        }

        return true
    }

    private fun pickUpImage() {
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL)
            .language("bn")
            .toolbarImageTitle("ছবি নির্বাচন করুন")
            .includeVideo(false)
            .single()
            .folderMode(true)
            .toolbarFolderTitle("ফোল্ডার নির্বাচন করুন")
            .theme(R.style.ImagePickerTheme)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)){

            val image: Image? = ImagePicker.getFirstImageOrNull(data)
            if (image != null) {
                imagePath = image.path
                binding?.image?.let { view ->
                    Glide.with(requireContext())
                        .load(imagePath)
                        .apply(RequestOptions().placeholder(R.drawable.ic_banner_place))
                        .into(view)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}