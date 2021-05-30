package com.bd.deliverytiger.app.ui.live.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bd.deliverytiger.app.R
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.bd.deliverytiger.app.databinding.ActivityLiveHomeBinding
import com.bd.deliverytiger.app.utils.AppConstant
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.alert
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@SuppressLint("SetTextI18n")
class LiveHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var doubleBackToExitPressedOnce = false
    private var navigationMenuId: Int = 0
    private var menuItem: MenuItem? = null

    private lateinit var appUpdateManager: AppUpdateManager
    private val viewModel: LiveHomeActivityViewModelNew by inject()
    private val requestCodeAppUpdate = 21720

    private val requestCodeCamera = 123
    private var displayUserId = 0
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBar.toolbar)

        SessionManager.init(this)

        navController = findNavController(R.id.navHostFragment)
        //val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener(fallbackListener).build()
        //binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_live_schedule_list), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navigationView.setupWithNavController(navController)
        /*NavigationUI.setupWithNavController(binding.appBar.bottomNavigation, navController)
        binding.appBar.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == navController.currentDestination?.id) {
                return@setOnNavigationItemSelectedListener true
            }
            return@setOnNavigationItemSelectedListener when (item.itemId) {
                *//*R.id.nav_live_list -> {
                    item.onNavDestinationSelected(navController)
                }*//*
                R.id.nav_live_schedule_list -> {
                    item.onNavDestinationSelected(navController)
                }
                R.id.nav_insert_live_schedule -> {
                    item.onNavDestinationSelected(navController)
                    true
                }

                *//*R.id.nav_live -> {
                    goToLiveStream()
                    false
                }*//*

                *//*R.id.nav_catalog_make -> {
                    if (SessionManager.isLogin) {
                        item.onNavDestinationSelected(navController)
                    } *//**//*else {
                        val intent = Intent(this, LoginActivity::class.java)
                        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                            if (it.resultCode == Activity.RESULT_OK) {
                                item.onNavDestinationSelected(navController)
                                binding.appBar.bottomNavigation.selectedItemId = R.id.nav_catalog_make
                            }
                        }.launch(intent)
                    }*//**//*
                    true
                }*//*
                *//*R.id.nav_record -> {
                    val videoList: MutableList<CatalogData> = mutableListOf()
                    videoList.add(CatalogData(0,"AWS CloudFront Stream", "", "", AppConstant.STREAM_AWS_CLOUD_FRONT))
                    //videoList.add(CatalogData(0,"AWS MediaPackage Stream", "", "", AppConstant.STREAM_AWS_MEDIA_PACKAGE))
                    val intent = Intent(this, VideoPagerActivity::class.java).apply {
                        putExtra("playIndex", 0)
                        putParcelableArrayListExtra("videoList", videoList as java.util.ArrayList<out Parcelable>)
                        putExtra("noCache", true)
                        putExtra("isLiveShow", true)
                    }
                    startActivity(intent)
                    false
                }*//*
                else -> false
            }
        }*/
        /*binding.navigationView.setNavigationItemSelectedListener { item ->
            navigationMenuId = item.itemId
            menuItem = item
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            val handled = NavigationUI.onNavDestinationSelected(item, navController)
            if (handled) {
                val parent = binding.navigationView.parent
                if (parent is DrawerLayout) {
                    parent.closeDrawer(binding.navigationView)
                }
            } else {

                *//*if(item.itemId == R.id.nav_help) {
                    startActivity(Intent(this, OnBoardingActivity::class.java))
                }
                if(item.itemId == R.id.nav_product_lists) {
                    toast("nav_product_lists")
                }*//*
            }
            return@setNavigationItemSelectedListener true
        }*/
        drawerListener()
        navHeaderData()
        appUpdateManager()
        updateSessionManager()

        //binding.appVersion.text = getPackageInfo()
        //facebookHash()

        /*binding.appVersion.setOnClickListener {
            displayUserId+=1
            if (displayUserId == 7) {
                toast("${sessionManager.userId}")
                displayUserId = -2
            }
            Handler(Looper.myLooper()!!).postDelayed(Runnable { displayUserId = 0 }, 1000)
        }*/

        /*navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                *//*R.id.nav_live_product_add -> {
                    binding.appBar.bottomNavigation.isVisible = false
                }
                R.id.nav_live_schedule_product_list -> {
                    binding.appBar.bottomNavigation.isVisible = false
                }
                R.id.nav_product_lists -> {
                    binding.appBar.bottomNavigation.isVisible = false
                }*//*
                R.id.nav_order_management -> {
                    binding.appBar.bottomNavigation.isVisible = false
                }
                R.id.nav_profile -> {
                    binding.appBar.bottomNavigation.isVisible = false
                }
                else -> {
                    binding.appBar.bottomNavigation.isVisible = true
                }
            }
        }*/
    }

    private val fallbackListener = AppBarConfiguration.OnNavigateUpListener {
        onBackPressed()
        true
    }

    override fun onResume() {
        super.onResume()
        checkStalledUpdate()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun drawerListener() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                when(navigationMenuId) {
                    R.id.nav_profile -> {
                        navController.navigate(R.id.nav_profile)
                        menuItem?.isChecked = true
                    }
                }
                navigationMenuId = 0
            }
        })
    }

    // I don't think it's needed here
    private fun navHeaderData() {
        val navHeaderView = binding.navigationView.getHeaderView(0)
        val parentHeader: ConstraintLayout = navHeaderView.findViewById(R.id.parent)
        val userPic: ImageView = navHeaderView.findViewById(R.id.userPic)
        val userName: TextView = navHeaderView.findViewById(R.id.name)
        val userPhone: TextView = navHeaderView.findViewById(R.id.phone)

        userName.text = SessionManager.companyName
        userPhone.text = SessionManager.mobile
        Glide.with(this)
            .load(SessionManager.profileImgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_person_circle).error(R.drawable.ic_person_circle).circleCrop())
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(userPic)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is TextInputEditText || view is EditText) {
                if (!isPointInsideView(event.rawX, event.rawY, view)) {
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun isPointInsideView(x: Float, y: Float, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]

        // point is inside view bounds
        return ((x > viewX && x < (viewX + view.width)) && (y > viewY && y < (viewY + view.height)))
    }

    private fun appUpdateManager() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, requestCodeAppUpdate)
            }
        }
    }

    private fun checkStalledUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, requestCodeAppUpdate)
            }
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
                    ActivityCompat.requestPermissions(this, permissions, requestCodeCamera)
                } else {
                    ActivityCompat.requestPermissions(this, permissions, requestCodeCamera)
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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    goToLiveStream()
                } else {
                    alert("Permission Required", "App required Camera, Audio & Storage permission to function properly. Please grand permission.", true, "Give Permission", "Cancel") {
                        if (it == AlertDialog.BUTTON_POSITIVE) {
                            ActivityCompat.requestPermissions(this, permissions, requestCodeCamera)
                        }
                    }.show()
                }
            }
        }
    }

    private fun goToLiveStream() {
        if (isPermissions()) {
            Intent(this, LiveHomeActivity::class.java).apply {
                putExtra("liveId", 0)
                putExtra("rtmpUrl", AppConstant.CHANNEL6)
                putExtra("liveTitle", "Test live")
                putExtra("channelId", "3232712")
                putExtra("liveDate", "22/02/2021")
                putExtra("liveStartTime", "16:00:00")
                putExtra("liveEndTime", "20:00:00")
            }.also {
                startActivity(it)
            }
        }
    }

    private fun getPackageInfo(): String {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        return pInfo.versionName
    }


    private fun updateSessionManager() {
        val userId = SessionManager.courierUserId
        //userId = 328702
        //userId = 1100
        viewModel.fetchLiveUserProfile(userId, this).observe(this, { data->
            Timber.d("requestBody $data")
            SessionManager.updateProfile(data)
        })
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun facebookHash() {
        try {
            val info = packageManager.getPackageInfo("com.ajkerdeal.app.liveplaza", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Timber.d("KeyHash $hash")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    fun updateToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

}