package com.bd.deliverytiger.app.ui.live.live_product_insert.quick_insert

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.InputType
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.firebase.FirebaseSettings
import com.bd.deliverytiger.app.api.model.live.firebase.LiveProductEvent
import com.bd.deliverytiger.app.api.model.live.live_product_insert.LiveProductInsertData
import com.bd.deliverytiger.app.api.model.live.live_product_list.LiveProductData
import com.bd.deliverytiger.app.databinding.ActivityQuickLiveProductInsertBinding
import com.bd.deliverytiger.app.ui.live.live_product_insert.LiveProductInsertViewModel
import com.bd.deliverytiger.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Mode
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.FileOutputStream
import java.util.*

@SuppressLint("SetTextI18n")
class QuickLiveProductInsertActivity : AppCompatActivity(), BottomSheetImagePicker.OnImagesSelectedListener {

    private lateinit var binding: ActivityQuickLiveProductInsertBinding
    private lateinit var camera: CameraView

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val permissionStorage = 9875

    private val productList: MutableList<LiveProductInsertData> = mutableListOf()
    private lateinit var dataAdapter: ProductPriceAdapter
    private lateinit var dataAdapterProduct: ProductOverviewAdapter

    private lateinit var fireBaseDataBase: FirebaseDatabase
    private lateinit var productUpdateRef: DatabaseReference

    private lateinit var sessionManager: SessionManager

    private var liveId: Int = 0
    private var suggestedPrice = ""
    private var price: Int = 0
    private var productImage: String = ""
    private var isUploading: Boolean = false
    private var isGalleryPicked: Boolean = false

    private val priceList: MutableList<String> = mutableListOf()

    private val fileUtils = FileUtils(this)

    private val viewModel: LiveProductInsertViewModel by inject()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuickLiveProductInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        liveId = intent?.getIntExtra("liveId", 0) ?: 0
        suggestedPrice = intent?.getStringExtra("suggestedPrice") ?: ""
        camera = binding.camera


        /*if (BuildConfig.DEBUG) {
            liveId = 1
            suggestedPrice = "10,20,30"
        }*/

        initFirebase()
        initListens()
        manageUploadedProducts()
        fetchProductsByLive()
    }

    private fun initListens() {

        with(camera) {
            setLifecycleOwner(this@QuickLiveProductInsertActivity)
            mode = Mode.PICTURE

            /*val width = SizeSelectors.maxWidth(512)
            val height = SizeSelectors.maxHeight(512)
            val dimensions = SizeSelectors.and(width, height)
            val ratio = SizeSelectors.aspectRatio(AspectRatio.of(1, 1), 0f)
            val result = SizeSelectors.or(
                SizeSelectors.and(ratio, dimensions),
                ratio,
                SizeSelectors.smallest()
            )
            setPictureSize(result)*/
            /*setPictureSize { list ->
                val sizeList: MutableList<Size> = mutableListOf()
                list.add(Size(512,512))
                list
            }
            setPreviewStreamSize { list ->
                val sizeList: MutableList<Size> = mutableListOf()
                list.add(Size(512,512))
                list
            }*/
        }
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                createNewFile(this@QuickLiveProductInsertActivity, FileType.Picture)?.let { file ->
                    result.toBitmap(512,512) { bitmap ->
                        FileOutputStream(file).use {
                            bitmap?.compress(Bitmap.CompressFormat.JPEG, 85, it)
                        }

                        productImage = file.absolutePath
                        insertProduct()
                    }

                    /*result.toFile(file) {
                        Timber.d("onPictureTaken ${file.absolutePath}")
                        //this@QuickLiveProductInsertActivity.toast("Picture saved")
                        Glide.with(this@QuickLiveProductInsertActivity)
                            .load(file.absolutePath)
                            .apply(RequestOptions().placeholder(R.drawable.ad_placeholder_220).error(R.drawable.ad_placeholder_220))
                            .into(binding.preview)
                        productImage = file.absolutePath
                        insertProduct()
                    }*/
                }
                Timber.d("pictureSize ${camera.pictureSize.toString()}")
            }
        })

        binding.uploadBtn.setOnClickListener {
            if (!isUploading) {
                priceSelectionDialog() {
                    if (it==1) {
                        capturePicture()
                    }
                }
            }
        }

        binding.galleryUploadBtn.setOnClickListener {
            if (!isUploading) {
                priceSelectionDialog() {
                    if (it==1) {
                        showGallery()
                    }
                }
            }
        }

        viewModel.viewState.observe(this, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    this.toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                /*is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }*/
            }
        })

    }

    private fun priceSelectionDialog(listener: ((type: Int) -> Unit)? = null) {
        val builder = MaterialAlertDialogBuilder(this)
        val view = layoutInflater.inflate(R.layout.dialog_product_price, null)
        builder.setView(view)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        dataAdapter = ProductPriceAdapter()
        if (suggestedPrice.isNotEmpty()) {
            val list = suggestedPrice.split(",").toMutableList()
            list.sortWith { p0, p1 -> (p0?.toIntOrNull() ?: 0) - (p1?.toIntOrNull() ?: 0) }
            list.add("✚")
            dataAdapter.initList(list)
            priceList.clear()
            priceList.addAll(list)
        }
        with(recyclerView) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@QuickLiveProductInsertActivity, 4, GridLayoutManager.VERTICAL, false)
            adapter = dataAdapter
        }
        dataAdapter.onAddItemClick = { model, position ->
            showPriceDialog()
        }
        val dialog = builder.create()
        dialog.show()
        dataAdapter.onItemClick = { model, position ->
            price = model.toIntOrNull() ?: 0
            dialog.dismiss()
            listener?.invoke(1)
            //binding.price.text = "দাম: ${DigitConverter.toBanglaDigit(price, true)}৳"
        }
    }

    private fun showPriceDialog() {
        val taskEditText = EditText(this).apply {
            imeOptions = EditorInfo.IME_ACTION_DONE
            inputType = InputType.TYPE_CLASS_NUMBER
            setPadding(this@QuickLiveProductInsertActivity.dpToPx(24f),
                0,
                this@QuickLiveProductInsertActivity.dpToPx(24f),
                this@QuickLiveProductInsertActivity.dpToPx(12f))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(this@QuickLiveProductInsertActivity.dpToPx(24f), 0, this@QuickLiveProductInsertActivity.dpToPx(24f), 0)
            }
            layoutParams = params
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("নতুন প্রোডাক্টের দাম")
            .setMessage("প্রোডাক্টের দাম লিখুন")
            .setView(taskEditText)
            .setPositiveButton("অ্যাড") { dialog, which ->
                val priceData = taskEditText.text.toString().trim()
                val priceInt = priceData.toIntOrNull() ?: 0
                if (priceInt > 0) {
                    dataAdapter.insert(priceData)
                    //price = priceInt
                } else {
                    this.toast("অনুগ্রহপূর্বক প্রোডাক্টের দাম লিখুন")
                }
            }
            .setNegativeButton("ক্যানসেল", null)
            .create()
        dialog.setOnDismissListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            taskEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }, 200L)
    }

    private fun manageUploadedProducts() {

        dataAdapterProduct = ProductOverviewAdapter()
        with(binding.recyclerViewProduct) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@QuickLiveProductInsertActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = dataAdapterProduct
        }
        dataAdapterProduct.onItemClick = { model, position ->
            pictureDialog(model)
        }
        dataAdapterProduct.onDeleteItemClick = { model, position ->
            alert("নির্দেশনা", "আপনি কি প্রোডাক্টটি ডিলিট করতে চান?", true, "হ্যাঁ, ডিলিট", "না") {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    dataAdapterProduct.removeAt(position)
                    viewModel.deleteProductFromLive(model.id).observe(this, Observer { flag ->
                        if (flag) {
                            this.toast("Deleted")
                        }
                    })
                    if (dataAdapterProduct.itemCount() == 0) {
                        binding.productPreviewLayout.isVisible = false
                    }
                }
            }.show()
        }

        fetchProductsByLive()
    }

    private fun fetchProductsByLive(broadCastEvent: Boolean = false) {
        viewModel.fetchLiveProducts(liveId).observe(this, Observer { list ->
            if (list.isNotEmpty()) {
                dataAdapterProduct.initList(list)
                if (broadCastEvent) {
                    broadCastProductUpdateEvent(list.first().coverPhoto ?: "")
                }
                binding.productPreviewLayout.isVisible = true
            }
        })
    }

    private fun capturePicture() {
        if (isStoragePermissions()) {
            if (price <= 0) {
                this.toast("প্রোডাক্টের দাম সিলেক্ট করুন")
                return
            }
            camera.takePicture()
        }
    }

    private fun insertProduct() {

        if (productImage.isEmpty()) {
            this.toast("অনুগ্রহপূর্বক প্রোডাক্টের ছবি যোগ করুন")
            return
        }

        isUploading = true
        if (isGalleryPicked) {
            binding.galleryUploadBtn.text = "আপলোডিং"
            binding.progressBar1.visibility = View.VISIBLE
        } else {
            binding.uploadBtn.text = "আপলোডিং"
            binding.progressBar.visibility = View.VISIBLE
        }

        productList.clear()
        productList.add(LiveProductInsertData(price, price, "", productImage, SessionManager.courierUserId, liveId))

        viewModel.insertLiveProducts(this@QuickLiveProductInsertActivity, productList).observe(this, Observer { uploadStatus ->
            if (uploadStatus) {
                if (isGalleryPicked) {
                    binding.progressBar1.visibility = View.GONE
                    binding.galleryUploadBtn.text = "গ্যালারি থেকে নিন"
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.uploadBtn.text = "ছবি তুলে আপলোড"
                }
                isUploading = false
                isGalleryPicked = false

                //binding.price.text = "দাম: ০৳"

                /*Glide.with(this@QuickLiveProductInsertActivity)
                    .load(R.drawable.ad_placeholder_220)
                    .into(binding.preview)*/
                price = 0
                productImage = ""
                dataAdapter.clearSelection()

                this.toast("লাইভ প্রোডাক্ট আপলোড হয়েছে")
                Timber.tag("LiveScheduleFragment").d("insertLiveProducts")
                fetchProductsByLive(true)

            }
        })
    }

    private fun broadCastProductUpdateEvent(image: String) {

        val model = LiveProductEvent(Date().time, image)
        productUpdateRef.setValue(model)
    }

    private fun initFirebase() {

        Firebase.database.getReference(resources.getString(R.string.app_name)).child("firebaseSettings").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //Timber.tag("chatDebug").d("Firebase settings $snapshot")
                    val settings = snapshot.getValue(FirebaseSettings::class.java)
                    if (settings != null) {

                        try {
                            val firebaseApp = FirebaseApp.getInstance("AjkerdealCustomer")
                            fireBaseDataBase = FirebaseDatabase.getInstance(firebaseApp)
                            //firebaseFirestore = FirebaseFirestore.getInstance(firebaseApp)
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
                            //firebaseFirestore = FirebaseFirestore.getInstance(firebaseApp)
                        }

                        // init services
                        productUpdateRef = fireBaseDataBase.getReference("LiveShow").child("productUpdate").child(liveId.toString())
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

    private fun pictureDialog(model: LiveProductData, listener: ((type: Int) -> Unit)? = null) {
        val builder = MaterialAlertDialogBuilder(this)
        val view = layoutInflater.inflate(R.layout.dialog_product_overview, null)
        builder.setView(view)
        val title: TextView = view.findViewById(R.id.title)
        val productImage: ImageView = view.findViewById(R.id.image)
        val close: ImageView = view.findViewById(R.id.close)

        title.text = "প্রোডাক্ট কোড: ${model.id} দাম: ${DigitConverter.toBanglaDigit(model.productPrice, true)}৳"
        Glide.with(productImage)
            .load(model.coverPhoto)
            .apply(RequestOptions().placeholder(R.drawable.ic_logo_ad1))
            .into(productImage)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3000000")))
        dialog.show()
        close.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {

        //window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        //window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.decorView.systemUiVisibility = (
                // Enables regular immersive mode.
                // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
                // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        //or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //Hide the nav bar
                        or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                )
    }

    private fun isStoragePermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (storagePermissionRationale) {
                    ActivityCompat.requestPermissions(this, permissions, permissionStorage)
                } else {
                    ActivityCompat.requestPermissions(this, permissions, permissionStorage)
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
        when (requestCode) {
            permissionStorage -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        capturePicture()
                    } else {
                        val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        if (storagePermissionRationale) {
                            alert("Permission Required", "App required Storage permission to function properly. Please grand permission.", true, "Give Permission", "Cancel") {
                                if (it == AlertDialog.BUTTON_POSITIVE) {
                                    ActivityCompat.requestPermissions(this, permissions, permissionStorage)
                                }
                            }.show()
                        } else {
                            alert("Permission Required", "Please go to Settings to enable Storage permission. (Settings-apps--permissions)", true, "Settings", "Cancel") {
                                if (it == AlertDialog.BUTTON_POSITIVE) {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${packageName}")).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    startActivity(intent)
                                }
                            }.show()
                        }
                    }
                }
            }
        }
    }

    private fun showGallery() {
        if (price <= 0) {
            this.toast("প্রোডাক্টের দাম সিলেক্ট করুন")
            return
        }
        BottomSheetImagePicker.Builder(getString(R.string.file_provider))
            .cameraButton(ButtonType.None)
            .galleryButton(ButtonType.None)
            .requestTag("single")
            .singleSelectTitle(R.string.pick_single)
            //.peekHeight(R.dimen.peekHeight)
            //.columnSize(R.dimen.columnSize)
            .show(supportFragmentManager)
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        Timber.d("Result from tag: $tag")
        Timber.d("Uris $uris")
        if (uris.isNotEmpty()) {
            isGalleryPicked = true
            val uri = uris.first()
            Timber.d("Uris $uri")
            /*Glide.with(this@QuickLiveProductInsertActivity)
                .load(uri)
                .apply(RequestOptions().placeholder(R.drawable.ad_placeholder_220).error(R.drawable.ad_placeholder_220))
                .into(binding.preview)*/

            productImage = fileUtils.getPath(uri)
            Timber.d("productImage $productImage")
            insertProduct()
        }


    }

}