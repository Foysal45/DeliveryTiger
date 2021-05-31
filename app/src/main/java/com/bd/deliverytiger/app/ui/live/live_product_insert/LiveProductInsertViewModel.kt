package com.bd.deliverytiger.app.ui.live.live_product_insert

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductInsertData
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductData
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductRequest
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.*
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File

class LiveProductInsertViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)
    private val mediaTypeMultipart = "multipart/form-data".toMediaTypeOrNull()
    private val mediaTypeText = "text/plain".toMediaTypeOrNull()

    fun insertLiveProducts(context: Context, requestBody: List<LiveProductInsertData>): LiveData<Boolean> {

        val responseData: MutableLiveData<Boolean> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {

            //Loop through products
            requestBody.forEachIndexed { index,  model ->
                val productList = mutableListOf<LiveProductInsertData>()
                productList.add(model)
                val response = repository.insertLiveProducts(productList)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.data != null) {
                            val productId = response.body.data!!.productId
                            val folderName = response.body.data!!.folderName ?: ""

                            val productIdRequestBody = productId.toString().toRequestBody(mediaTypeText)
                            val folderNameRequestBody = folderName.toRequestBody(mediaTypeText)

                            val partList: MutableList<MultipartBody.Part> = mutableListOf()
                            val file = File(model.imageLink)
                            if (file.exists()) {
                                val bigFile = scaleImage(context, model.imageLink, AppConstant.LIVE_BIG_IMAGE_WIDTH, AppConstant.LIVE_BIG_IMAGE_WIDTH)
                                val smallFile = scaleImage(context, model.imageLink, AppConstant.LIVE_SMALL_IMAGE_WIDTH, AppConstant.LIVE_SMALL_IMAGE_WIDTH)

                                if (!bigFile.isNullOrEmpty()) {
                                    val compressedFile = File(bigFile)
                                    val requestFile = compressedFile.asRequestBody(mediaTypeMultipart)
                                    val part = MultipartBody.Part.createFormData("file1", "1.jpg", requestFile)
                                    partList.add(part)
                                }

                                if (!smallFile.isNullOrEmpty()) {
                                    val compressedFile = File(smallFile)
                                    val requestFile = compressedFile.asRequestBody(mediaTypeMultipart)
                                    val part = MultipartBody.Part.createFormData("files1", "s1.jpg", requestFile)
                                    partList.add(part)
                                }
                            }

                            val response1 = repository.uploadProductPhoto(productIdRequestBody, folderNameRequestBody, partList)
                            when (response1) {
                                is NetworkResponse.Success -> {
                                    if (response1.body.data != null) {
                                        //responseData.value = response1.body.data!!
                                        if (index == requestBody.lastIndex) {
                                            responseData.postValue(response1.body.data!!)
                                            viewState.postValue(ViewState.ProgressState(false))
                                        }
                                    }
                                }
                                is NetworkResponse.ServerError -> {
                                    val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                                    viewState.postValue(ViewState.ShowMessage(message))
                                    responseData.postValue(false)
                                }
                                is NetworkResponse.NetworkError -> {
                                    val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                                    viewState.postValue(ViewState.ShowMessage(message))
                                    responseData.postValue(false)
                                }
                                is NetworkResponse.UnknownError -> {
                                    val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                                    viewState.postValue(ViewState.ShowMessage(message))
                                    Timber.d(response1.error)
                                    responseData.postValue(false)
                                }
                            }

                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        responseData.value = false
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                        responseData.value = false
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                        responseData.value = false
                    }
                }

            }
        }
        return responseData
    }

    fun fetchLiveProducts(liveId: Int): LiveData<List<LiveProductData>> {

        val catalogList: MutableLiveData<List<LiveProductData>> = MutableLiveData()

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchLiveProducts(LiveProductRequest(liveId))
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val dataList = response.body.data
                        if (!dataList.isNullOrEmpty()) {
                            catalogList.value = dataList!!
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }
        return catalogList
    }

    fun deleteProductFromLive(productId: Int): LiveData<Boolean> {

        val responseData: MutableLiveData<Boolean> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.deleteProductFromLive(productId)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.data != null) {
                            responseData.value = response.body.data!! == 1
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }
        return responseData
    }

    private fun scaleImage(context: Context, path: String, pictureWidth: Int, pictureHeight: Int, imageName: String? = null): String? {
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
            val file: File? = createNewFile(context, FileType.Picture)
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
}