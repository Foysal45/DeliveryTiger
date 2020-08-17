package com.bd.deliverytiger.app.ui.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.fcm.FCMData
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentTwo
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.billing_of_service.BillingofServiceFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.dashboard.DashboardFragment
import com.bd.deliverytiger.app.ui.dialog.PopupDialog
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.district.v2.DistrictThanaAriaSelectFragment
import com.bd.deliverytiger.app.ui.filter.FilterFragment
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.ui.notification.NotificationFragment
import com.bd.deliverytiger.app.ui.notification.NotificationPreviewFragment
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.payment_statement.PaymentStatementFragment
import com.bd.deliverytiger.app.ui.payment_statement.details.PaymentStatementDetailFragment
import com.bd.deliverytiger.app.ui.profile.ProfileFragment
import com.bd.deliverytiger.app.ui.service_bill_pay.ServiceBillPayFragment
import com.bd.deliverytiger.app.ui.shipment_charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navViewRight: NavigationView
    private lateinit var addProductIV: ImageView
    private lateinit var logoIV: ImageView
    private lateinit var toolbarTitleTV: TextView
    private lateinit var notificationIV: ImageView
    private lateinit var trackingIV: ImageView
    private lateinit var searchIV: ImageView
    private lateinit var downloadTV: ImageView
    private lateinit var headerPic: ImageView
    private lateinit var separetor: View
    private lateinit var addOrderFab: FloatingActionButton

    private val viewModel: HomeViewModel by inject()
    private var doubleBackToExitPressedOnce = false
    private var navId: Int = 0

    private lateinit var appUpdateManager: AppUpdateManager
    private val requestCodeAppUpdate = 21720

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navViewRight = findViewById(R.id.nav_view_2)
        logoIV = findViewById(R.id.home_toolbar_logo)
        addProductIV = findViewById(R.id.home_toolbar_add)
        toolbarTitleTV = findViewById(R.id.home_toolbar_title)
        notificationIV = findViewById(R.id.home_toolbar_notification)
        trackingIV = findViewById(R.id.home_toolbar_tracking)
        searchIV = findViewById(R.id.home_toolbar_search)
        downloadTV = findViewById(R.id.home_toolbar_download)
        separetor = findViewById(R.id.home_toolbar_separator)
        addOrderFab = findViewById(R.id.addOrderFab)
        navView.setNavigationItemSelectedListener(this)


        /*val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ActivityCompat.getColor(this, R.color.black_80)*/

        toolbar.setNavigationIcon(R.drawable.ic_menu)
        toolbar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                onBackPressed()
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, navViewRight)
        drawerListener()
        onBackStackChangeListener()

        val headerView = navView.getHeaderView(0)
        headerPic = headerView.findViewById(R.id.nav_header_image)
        val headerUserNameTV: TextView = headerView.findViewById(R.id.nav_header_title)
        val headerDesignationTV: TextView = headerView.findViewById(R.id.nav_header_sub_title)
        val profileEdit: ImageView = headerView.findViewById(R.id.nav_header_profile_edit)
        headerUserNameTV.text = SessionManager.companyName
        headerDesignationTV.text = SessionManager.mobile

        profileEdit.setOnClickListener {
            navId = R.id.nav_header_profile_edit
            drawerLayout.closeDrawer(GravityCompat.START)

        }

        FirebaseMessaging.getInstance().subscribeToTopic("DeliveryTigerTopic")

        //addHomeFragment()
        addDashBoardFragment()

        addProductIV.setOnClickListener {
            addOrderFragment()
        }
        notificationIV.setOnClickListener {
            goToNotification()
        }
        trackingIV.setOnClickListener {
            goToOrderTracking()
        }
        searchIV.setOnClickListener {
            goToAllOrder()
        }
        downloadTV.setOnClickListener {
            val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
            if (currentFragment is PaymentStatementDetailFragment) {
                currentFragment.downloadFile()
            }
        }

        onNewIntent(intent)

        addOrderFab.setOnClickListener {
            addOrderFragment()
        }

        viewModel.getBannerInfo().observe(this, Observer { model ->

            val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
            if (currentFragment is DashboardFragment) {
                currentFragment.showBanner(model.showBanner, model.bannerUrl)
            }
            if (model.showPopupBanner) {
                // No frequency show all time
                if (model.popupFrequency == 0) {
                    showPopupDialog(model.popupBannerUrl)
                } else {
                    val calender = Calendar.getInstance()
                    val dayOfYear = calender.get(Calendar.DAY_OF_YEAR)
                    if (SessionManager.popupDateOfYear != dayOfYear) {
                        SessionManager.popupShowCount = 0
                    }
                    if (model.popupFrequency > SessionManager.popupShowCount) {
                        SessionManager.popupShowCount = SessionManager.popupShowCount + 1
                        SessionManager.popupDateOfYear = dayOfYear
                        showPopupDialog(model.popupBannerUrl)
                    }
                }
            }
        })

        appUpdateManager()
    }

    override fun onStart() {
        super.onStart()
        Timber.d("HomeActivityLog", "onStart Called!")

        if (SessionManager.profileImgUri.isNotEmpty()) {
            Timber.d("HomeActivityLog 1 ", SessionManager.profileImgUri)
            setProfileImgUrl(SessionManager.profileImgUri)
        } else {
            headerPic.setImageResource(R.drawable.ic_account)
        }
    }

    override fun onResume() {
        super.onResume()
        checkStalledUpdate()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val model: FCMData? = intent.getParcelableExtra("data")
            if (model != null) {
                val fragment = NotificationPreviewFragment.newInstance(model)
                val tag = NotificationPreviewFragment.fragmentTag

                val ft = supportFragmentManager.beginTransaction()
                ft.add(R.id.mainActivityContainer, fragment, tag)
                ft.addToBackStack(tag)
                ft.commit()

                intent.removeExtra("data")
            }
        }
    }

    override fun onBackPressed() {

        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) || drawerLayout.isDrawerOpen(
                GravityCompat.END
            ) -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
            supportFragmentManager.backStackEntryCount > 0 -> {
                supportFragmentManager.popBackStack()
            }
            else -> {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    return
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Press again to Exit", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000L)
            }
        }
    }

    private fun onBackStackChangeListener() {

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                logoIV.visibility = View.GONE

            } else {
                toolbar.setNavigationIcon(R.drawable.ic_menu)
                logoIV.visibility = View.VISIBLE
                setToolbarTitle("")
            }
            val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
            if (currentFragment is DashboardFragment ||
                currentFragment is AddOrderFragmentOne ||
                currentFragment is AddOrderFragmentTwo ||
                currentFragment is ProfileFragment ||
                currentFragment is DistrictSelectFragment ||
                currentFragment is DistrictThanaAriaSelectFragment) {
                addProductBtnVisibility(false)
            } else {
                addProductBtnVisibility(true)
            }
            if (currentFragment is OrderTrackingFragment) {
                trackingIV.visibility = View.GONE
            } else {
                trackingIV.visibility = View.VISIBLE
            }
            if (currentFragment is WebViewFragment) {
                trackingIV.visibility = View.GONE
                searchIV.visibility = View.GONE
                separetor.visibility = View.GONE
                addProductBtnVisibility(false)
            }
            if (currentFragment is NotificationPreviewFragment) {
                trackingIV.visibility = View.GONE
                searchIV.visibility = View.GONE
                separetor.visibility = View.GONE
                addProductBtnVisibility(false)
            }
            if (currentFragment is ServiceBillPayFragment) {
                addOrderFab.hide()
            }
            if (currentFragment is PaymentStatementDetailFragment) {
                downloadTV.visibility = View.VISIBLE
            } else {
                downloadTV.visibility = View.GONE
            }
            if (currentFragment is DashboardFragment) {
                //logoIV.visibility = View.VISIBLE
                searchIV.visibility = View.VISIBLE
                separetor.visibility = View.VISIBLE
                //moveFabBy(100f)
            } else {
                //logoIV.visibility = View.GONE
                searchIV.visibility = View.GONE
                separetor.visibility = View.GONE
                //moveFabBy(24f)
            }

            when (currentFragment) {
                is DashboardFragment -> {
                    currentFragment.onResume()
                }
                is AllOrdersFragment -> {
                    currentFragment.onResume()
                }
                is AddOrderFragmentOne -> {
                    currentFragment.onResume()
                }
                is AddOrderFragmentTwo -> {
                    currentFragment.onResume()
                }
                is BillingofServiceFragment -> {
                    currentFragment.onResume()
                }
                is ServiceBillPayFragment -> {
                    currentFragment.onResume()
                }
                is CODCollectionFragment -> {
                    currentFragment.onResume()
                }
                is OrderTrackingFragment -> {
                    currentFragment.onResume()
                }
                is PaymentStatementFragment -> {
                    currentFragment.onResume()
                }
                is ShipmentChargeFragment -> {
                    currentFragment.onResume()
                }
                is ProfileFragment -> {
                    currentFragment.onResume()
                }
                is WebViewFragment -> {
                    currentFragment.onResume()
                }
                is NotificationPreviewFragment -> {
                    currentFragment.onResume()
                }
            }

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // go to manageNavigationItemSelection method
        navId = item.itemId
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun drawerListener() {


        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        // Drawer started opening

                    } else {
                        // Drawer started closing
                        val currentFragment = supportFragmentManager.findFragmentById(R.id.container_drawer)
                        if (currentFragment is FilterFragment) {
                            currentFragment.forceHideKeyboard()
                        }
                    }
                }
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                /*if (slideOffset == 1.0F && drawerLayout.isDrawerOpen(GravityCompat.END)){

                }*/
            }

            override fun onDrawerClosed(drawerView: View) {
                manageNavigationItemSelection(navId)
                Timber.d("HomeActivityLog", "onDrawerClosed called")
                //val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                //inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }

            override fun onDrawerOpened(drawerView: View) {
            }
        })
    }

    fun openRightDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        drawerLayout.openDrawer(GravityCompat.END)
    }

    private fun manageNavigationItemSelection(id: Int) {
        when (id) {
            R.id.nav_header_profile_edit -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ProfileFragment) {
                    Timber.d("tag", "ProfileFragment already exist")
                } else {
                    addFragment(ProfileFragment.newInstance(), ProfileFragment.tag)
                }
            }
            R.id.nav_dashboard -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is DashboardFragment) {
                    Timber.d("tag", "DashboardFragment already exist")
                } else {
                    //addFragment(DashboardFragment.newInstance(), DashboardFragment.tag)
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        val first = supportFragmentManager.getBackStackEntryAt(0)
                        supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                }
            }
            R.id.nav_new_order -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is AddOrderFragmentOne) {
                    Timber.d("tag", "AddOrderFragmentOne already exist")
                } else {
                    addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
                }
            }
            R.id.nav_orders -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is AllOrdersFragment) {
                    Timber.d("tag", "AllOrdersFragment already exist")
                } else {
                    addFragment(AllOrdersFragment.newInstance(), AllOrdersFragment.tag)
                }
            }
            R.id.nav_bill -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is BillingofServiceFragment) {
                    Timber.d("tag", "BillingofServiceFragment already exist")
                } else {
                    addFragment(BillingofServiceFragment.newInstance(), BillingofServiceFragment.tag)
                }
            }
            R.id.nav_bill_pay -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ServiceBillPayFragment) {
                    Timber.d("tag", "ServiceBillPayFragment already exist")
                } else {
                    addFragment(ServiceBillPayFragment.newInstance(), ServiceBillPayFragment.tag)
                }
            }
            R.id.nav_cod_collection -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is CODCollectionFragment) {
                    Timber.d("tag", "CODCollectionFragment already exist")
                } else {
                    addFragment(CODCollectionFragment.newInstance(), CODCollectionFragment.tag)
                }

            }
            R.id.nav_order_tracking -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is OrderTrackingFragment) {
                    Timber.d("tag", "OrderTrackingFragment already exist")
                } else {
                    addFragment(OrderTrackingFragment.newInstance(""), OrderTrackingFragment.tag)
                }

            }
            R.id.nav_payment_history -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is PaymentStatementFragment) {
                    Timber.d("tag", "PaymentHistory already exist")
                } else {
                    addFragment(PaymentStatementFragment.newInstance(), PaymentStatementFragment.tag)
                }
            }
            R.id.nav_shipment_change -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ShipmentChargeFragment) {
                    Timber.d("tag", "ShipmentChargeFragment already exist")
                } else {
                    addFragment(ShipmentChargeFragment.newInstance(), ShipmentChargeFragment.tag)
                }
            }
            R.id.nav_profile -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ProfileFragment) {
                    Timber.d("tag", "ProfileFragment already exist")
                } else {
                    addFragment(ProfileFragment.newInstance(), ProfileFragment.tag)
                }
            }
            R.id.nav_privacy -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is WebViewFragment) {
                    Timber.d("tag", "WebViewFragment already exist")
                } else {

                    try {
                        val fragment = WebViewFragment.newInstance(AppConstant.POLICY_URL, "প্রাইভেসি পলিসি")
                        addFragment(fragment, WebViewFragment.tag)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            R.id.nav_logout -> {

                SessionManager.clearSession()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        navId = 0
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is TextInputEditText || view is EditText) {
                if (!isPointInsideView(event.rawX, event.rawY, view)) {
                    view.clearFocus()
                    callFragmentHideKeyboard()
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


    private fun callFragmentHideKeyboard() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
        currentFragment?.hideKeyboard()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun setToolbarTitle(title: String) {
        toolbarTitleTV.text = title
    }

    private fun addProductBtnVisibility(isVisible: Boolean, withFab: Boolean = true) {
        if (isVisible) {
            addProductIV.visibility = View.VISIBLE
            if (withFab) {
                addOrderFab.show()
            }
        } else {
            addProductIV.visibility = View.GONE
            if (withFab) {
                addOrderFab.hide()
            }
        }
    }


    private fun addDashBoardFragment() {
        logoIV.visibility = View.VISIBLE
        addProductBtnVisibility(false)
        searchIV.visibility = View.VISIBLE

        val fragment = DashboardFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, DashboardFragment.tag)
        ft?.commit()
    }

    private fun addOrderFragment() {

        val fragment = AddOrderFragmentOne.newInstance()
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.mainActivityContainer, fragment, AddOrderFragmentOne.tag)
        ft.addToBackStack(AddOrderFragmentOne.tag)
        ft.commit()

        /*val fragment = AddOrderFragmentTwo.newInstance(null)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainActivityContainer, fragment, AddOrderFragmentTwo.tag)
        ft.addToBackStack(AddOrderFragmentTwo.tag)
        ft.commit()*/
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainActivityContainer, fragment, tag)
        ft.addToBackStack(tag)
        ft.commit()
    }

    private fun goToNotification() {

        openRightDrawer()

        Handler().postDelayed({

            val currentFragment = supportFragmentManager.findFragmentById(R.id.container_drawer)
            if (currentFragment is NotificationFragment) {
                Timber.d("tag", "NotificationFragment already exist")
            } else {
                val fragment = NotificationFragment.newInstance()
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.replace(R.id.container_drawer, fragment, NotificationFragment.tag)
                ft.commit()
            }
        }, 300L)

    }

    private fun goToOrderTracking() {

        val fragment = OrderTrackingFragment.newInstance("")
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.mainActivityContainer, fragment, OrderTrackingFragment.tag)
        ft.addToBackStack(OrderTrackingFragment.tag)
        ft.commit()
    }

    private fun goToAllOrder() {

        val fragment = AllOrdersFragment.newInstance(true)
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
        ft.addToBackStack(AllOrdersFragment.tag)
        ft.commit()
    }

    private fun setProfileImgUrl(imageUri: String?) {
        try {
            val imgFile = File(imageUri + "")
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                headerPic.setImageDrawable(VariousTask.getCircularImage(this, myBitmap))
            }
        } catch (e: Exception) {
        }
    }

    private fun showPopupDialog(imageUrl: String?) {
        val tag = PopupDialog.tag
        val dialog = PopupDialog.newInstance(imageUrl)
        dialog.show(supportFragmentManager, tag)
    }

    /*private fun moveFabBy(value: Float) {
        timber.log.Timber.d("moveFabBy: $value")
        val param = addOrderFab.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0,0, this.dpToPx(24f), this.dpToPx(value))
        addOrderFab.layoutParams = param
    }*/

    private fun appUpdateManager() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE,this,requestCodeAppUpdate)
            }
        }
    }

    private fun checkStalledUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            // For IMMEDIATE
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,this,requestCodeAppUpdate)
            }
        }
    }
}
