package com.bd.deliverytiger.app.ui.add_order

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.ProgressRequestBody
import com.bd.deliverytiger.app.api.model.image_upload.ClassifiedImageData
import com.bd.deliverytiger.app.api.model.location.LocationData
import com.bd.deliverytiger.app.api.model.product_upload.ProductUploadRequest
import com.bd.deliverytiger.app.databinding.FragmentAddProductBottomSheetBinding
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationSelectionDialog
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.concurrent.thread


class AddProductBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentAddProductBottomSheetBinding? = null
    var onProductUploaded: ((dealId: Int, offerType: Int) -> Unit)? = null

    private val productUploadRequest = ProductUploadRequest()
    private var imagePath: String = ""

    private var selectedDistrictId: Int = 0
    private var districtName: String = ""

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
                uploadProduct()
            }
        }

        binding?.etDistrict?.setOnClickListener {

            viewModel.fetchAllDistricts().observe(viewLifecycleOwner, Observer { response ->
                val districtList = response.districtInfo
                val locationList: MutableList<LocationData> = mutableListOf()
                districtList.forEach { model ->
                    locationList.add(
                        LocationData(
                            model.districtId,
                            model.districtBng,
                            model.district,
                            "",
                            model.district.toLowerCase(Locale.US)
                        )
                    )
                }

                val dialog = LocationSelectionDialog.newInstance(locationList)
                dialog.show(childFragmentManager, LocationSelectionDialog.tag)
                dialog.onLocationPicked = { model ->
                    selectedDistrictId = model.id
                    districtName = model.displayNameBangla!!
                    binding?.etDistrict?.setText(districtName)
                    Timber.d("districtName: $districtName districtId: $selectedDistrictId")
                }
            })

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
        val districtName = binding?.etDistrict?.text?.toString() ?: ""

        if (productTitle.trim().isEmpty()) {
            context?.toast("প্রোডাক্টের টাইটেল ইংলিশে লিখুন ")
            return false
        }
        if (districtName.trim().isEmpty()) {
            context?.toast("জেলা নির্বাচন করুন")
            return false
        }
        if (!isEnglishLetterOnly(productTitle)) {
            context?.toast("প্রোডাক্টের টাইটেল ইংলিশে লিখুন অথবা স্পেশাল ক্যারেক্টর বাদ দিন")
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

        val currentAppVersion: String = appVersion()

        productUploadRequest.apply {
            this.productTitle = productTitle
            productTitleEng = productTitle
            this.productPrice = productPriceNumber
            this.productDescription = productDescription
            this.districtId = selectedDistrictId
            mobileNumber = SessionManager.mobile
            customerId = SessionManager.courierUserId
            appVersion = currentAppVersion
            Timber.d("productUploadRequest $productUploadRequest")
        }

        return true
    }

    private fun pickUpImage() {
        ImagePicker.with(this)
            .cropSquare()
            //.compress(512)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private fun uploadProduct() {

        val progressDialog = progressDialog()
        progressDialog.show()
        val jsonModel = gson.toJson(productUploadRequest)
        binding?.uploadBtn?.isEnabled = false
        viewModel.uploadProductInfo(jsonModel.toRequestBody(mediaTypeText)).observe(viewLifecycleOwner, Observer { model ->

            val imageData = ClassifiedImageData(
                    model.productTitle,
                    model.folderName,
                    model.dealId,
                    1
            )
            val imageDataJson = gson.toJson(imageData)
            val imageDataRequestBody = imageDataJson.toRequestBody(mediaTypeText)

            val partList: MutableList<MultipartBody.Part> = mutableListOf()
            val imageFile = File(imagePath)
            if (imageFile.exists()) {

                    val bigFile = scaleImage(imagePath, AppConstant.BIG_IMAGE_WIDTH, AppConstant.BIG_IMAGE_HEIGHT)
                    val smallFile = scaleImage(imagePath, AppConstant.SMALL_IMAGE_WIDTH, AppConstant.SMALL_IMAGE_HEIGHT)
                    val miniFile = scaleImage(imagePath, AppConstant.MINI_IMAGE_WIDTH, AppConstant.MINI_IMAGE_HEIGHT)

                    if (!bigFile.isNullOrEmpty()) {
                        //val compressedFile = Compressor.compress(context, File(bigFile))
                        val compressedFile = File(bigFile)
                        val requestFile = compressedFile.asRequestBody(mediaTypeMultipart)
                        val part = MultipartBody.Part.createFormData("file1", "1.jpg", requestFile)
                        partList.add(part)
                    }

                    if (!smallFile.isNullOrEmpty()) {
                        //val compressedFile = Compressor.compress(context, File(smallFile))
                        val compressedFile = File(smallFile)
                        val requestFile = compressedFile.asRequestBody(mediaTypeMultipart)
                        val partNew = MultipartBody.Part.createFormData("fileSmall1", "smallimage1.jpg", requestFile)
                        partList.add(partNew)
                    }

                    if (!miniFile.isNullOrEmpty()) {
                        //val compressedFile = Compressor.compress(context, File(miniFile))
                        val compressedFile = File(miniFile)
                        val requestFile = compressedFile.asRequestBody(mediaTypeMultipart)
                        val partNew = MultipartBody.Part.createFormData("fileMini1", "miniimage1.jpg", requestFile)
                        partList.add(partNew)
                    }


                lifecycleScope.launch(Dispatchers.IO) {

                    val imageUploadResponse = appRepository.uploadProductImage(imageDataRequestBody, partList)
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

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data!!

            imagePath = uri.path ?: ""
            binding?.image?.let { view ->
                Glide.with(requireContext())
                    .load(imagePath)
                    .apply(RequestOptions().placeholder(R.drawable.ic_banner_place))
                    .into(view)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            context?.toast(ImagePicker.getError(data))
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image: Image? = ImagePicker.getFirstImageOrNull(data)
            if (image != null) {
                imagePath = image.path
                binding?.image?.let { view ->
                    Glide.with(requireContext()).load(imagePath).apply(RequestOptions().placeholder(R.drawable.ic_banner_place)).into(view)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }*/

    private fun scaleImage(path: String, pictureWidth: Int, pictureHeight: Int, imageName: String? = null): String? {
        var imagePath: String? = null
        var scaledBitmap: Bitmap? = null
        try {
            // Part 1: Decode image
            val unscaledBitmap = ScalingUtilities.decodeFile(path, pictureWidth, pictureHeight, ScalingUtilities.ScalingLogic.FIT)

            if (unscaledBitmap.width > pictureWidth || unscaledBitmap.height > pictureHeight) {
                // Part 2: Scale image

                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, pictureWidth, pictureHeight, ScalingUtilities.ScalingLogic.FIT)
                //Log.e("scaleProblem", "scaledBitmap: width: " + scaledBitmap.getWidth() + " height: " + scaledBitmap.getHeight() );

                if (scaledBitmap.width > pictureWidth || scaledBitmap.height > pictureHeight) {
                    scaledBitmap = ScalingUtilities.createScaledBitmap(scaledBitmap, pictureWidth, pictureHeight, ScalingUtilities.ScalingLogic.FIT)
                    //Log.e("scaleProblem", "scaledBitmap2: width: " + scaledBitmap.getWidth() + " height: " + scaledBitmap.getHeight() );
                    if (scaledBitmap.width > pictureWidth || scaledBitmap.height > pictureHeight) {
                        scaledBitmap = ScalingUtilities.createScaledBitmap(scaledBitmap, pictureWidth, pictureHeight, ScalingUtilities.ScalingLogic.FIT)
                        //Log.e("scaleProblem", "scaledBitmap3: width: " + scaledBitmap.getWidth() + " height: " + scaledBitmap.getHeight() );
                        scaledBitmap = drawCanvas(scaledBitmap, pictureWidth, pictureHeight)
                    } else {
                        scaledBitmap = drawCanvas(scaledBitmap, pictureWidth, pictureHeight)
                    }

                } else {
                    scaledBitmap = drawCanvas(scaledBitmap, pictureWidth, pictureHeight)
                }

            } else if (unscaledBitmap.width < pictureWidth || unscaledBitmap.height < pictureHeight) {
                scaledBitmap = drawCanvas(unscaledBitmap, pictureWidth, pictureHeight)
            } else {
                scaledBitmap = unscaledBitmap
            }

            // Store to tmp file
            val file: File? = createNewFile(requireContext(), FileType.Picture)
            file?.outputStream()?.use {
                scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 75, it)
                it.flush()
                it.close()
            }
            scaledBitmap!!.recycle()
            imagePath = file?.absolutePath
        } catch (e: Throwable) {
            Timber.d(e)
        }

        return imagePath ?: path
    }

    private fun drawCanvas(unscaledBitmap1: Bitmap, dstWidth: Int, dstHeight: Int): Bitmap {

        val bitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val canvasWidth = (dstWidth - unscaledBitmap1.width) / 2
        val canvasHeight = (dstHeight - unscaledBitmap1.height) / 2

        //Log.e("scaleProblem", "canvasPadding: width: " + canvasWidth + " height: " + canvasHeight );

        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(
                unscaledBitmap1,
                canvasWidth.toFloat(),
                canvasHeight.toFloat(),
                null
        )
        return bitmap
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}