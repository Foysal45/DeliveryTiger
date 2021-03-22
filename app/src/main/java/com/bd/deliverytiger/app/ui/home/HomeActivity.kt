package com.bd.deliverytiger.app.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.broadcast.ConnectivityReceiver
import com.bd.deliverytiger.app.fcm.FCMData
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.services.LocationUpdatesService
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.balance_load.BalanceLoadFragment
import com.bd.deliverytiger.app.ui.bill_pay.ServiceBillPayFragment
import com.bd.deliverytiger.app.ui.bill_pay_history.ServiceBillPayHistoryFragment
import com.bd.deliverytiger.app.ui.charge_calculator.DeliveryChargeCalculatorFragment
import com.bd.deliverytiger.app.ui.chat.ChatActivity
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.collection_history.CollectionHistoryFragment
import com.bd.deliverytiger.app.ui.collector_tracking.MapFragment
import com.bd.deliverytiger.app.ui.complain.ComplainFragment
import com.bd.deliverytiger.app.ui.dashboard.DashboardFragment
import com.bd.deliverytiger.app.ui.dialog.PopupDialog
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.district.v2.DistrictThanaAriaSelectFragment
import com.bd.deliverytiger.app.ui.filter.FilterFragment
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.ui.notification.NotificationFragment
import com.bd.deliverytiger.app.ui.notification.NotificationPreviewFragment
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.payment_details.PaymentDetailsFragment
import com.bd.deliverytiger.app.ui.payment_request.PaymentStatementMenuFragment
import com.bd.deliverytiger.app.ui.payment_statement.PaymentStatementFragment
import com.bd.deliverytiger.app.ui.payment_statement.details.PaymentStatementDetailFragment
import com.bd.deliverytiger.app.ui.profile.ProfileFragment
import com.bd.deliverytiger.app.ui.quick_order.QuickOrderFragment
import com.bd.deliverytiger.app.ui.referral.ReferralFragment
import com.bd.deliverytiger.app.ui.return_statement.ReturnStatementFragment
import com.bd.deliverytiger.app.ui.return_statement.details.ReturnStatementDetailsFragment
import com.bd.deliverytiger.app.ui.service_charge.ServiceChargeFragment
import com.bd.deliverytiger.app.ui.shipment_charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.ui.survey.SurveyActivity
import com.bd.deliverytiger.app.ui.unpaid_cod.UnpaidCODFragment
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class HomeActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

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
    private lateinit var parent: CoordinatorLayout
    private lateinit var actionBtn: TextView

    private val viewModel: HomeViewModel by inject()
    private var doubleBackToExitPressedOnce = false
    private var navId: Int = 0

    //AppUpdateManager
    private lateinit var appUpdateManager: AppUpdateManager
    private val requestCodeAppUpdate = 21720

    //Connectivity
    private var snackBar: Snackbar? = null
    private lateinit var connectivityReceiver: ConnectivityReceiver

    // Location
    private val PERMISSION_REQUEST_CODE = 8620
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private lateinit var gpsUtils: GpsUtils
    private var isGPS: Boolean = false

    // Location Service
    private lateinit var receiver: MyReceiver
    private var foregroundService: LocationUpdatesService? = null
    private var mBound: Boolean = false
    private var currentLocation: Location? = null

    @SuppressLint("SetTextI18n")
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
        parent = findViewById(R.id.parent)
        actionBtn = findViewById(R.id.actionBtn)
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
        val nearbyHub: LinearLayout = headerView.findViewById(R.id.nearbyHub)
        //val referralET: EditText = headerView.findViewById(R.id.referralET)
        //val referralApply: ImageView = headerView.findViewById(R.id.referralApply)
        //val merchantCredit: TextView = headerView.findViewById(R.id.merchantCredit)
        //val merchantAdvancePayment: TextView = headerView.findViewById(R.id.merchantAdvancePayment)
        headerUserNameTV.text = SessionManager.companyName
        headerDesignationTV.text = SessionManager.mobile
        //merchantCredit.text = "ক্রেডিট লিমিট: ৳ ${DigitConverter.toBanglaDigit(SessionManager.credit, true)}"
        //merchantAdvancePayment.text = "অ্যাডভান্স পেমেন্ট: ৳ ${DigitConverter.toBanglaDigit(0, true)}"

        profileEdit.setOnClickListener {
            navId = R.id.nav_header_profile_edit
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        headerPic.setOnClickListener {
            navId = R.id.nav_header_profile_edit
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        nearbyHub.setOnClickListener {
            navId = R.id.nav_nearby_hub
            drawerLayout.closeDrawer(GravityCompat.START)
            //goToNearByHubMap()
        }
        /*referralApply.setOnClickListener {
            hideKeyboard()
            val referCode = referralET.text.toString().trim()
            if (referCode.isNotEmpty()) {
                timber.log.Timber.d("Refer code: $referCode ")
            } else {
                this.toast("রেফার কোড লিখুন")
            }
        }*/

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
            goToAllOrder(true)
        }
        downloadTV.setOnClickListener {
            val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
            if (currentFragment is PaymentStatementDetailFragment) {
                currentFragment.downloadFile()
            }
        }
        actionBtn.setOnClickListener {
            goToAllOrder(false)
        }

        //Timber.d("BundleLog ${intent.extras?.bundleToString()}")
        onNewIntent(intent)

        addOrderFab.setOnClickListener {
            addOrderFragment()
        }

        viewModel.getBannerInfo().observe(this, Observer { model ->

            val popupModel = model.popUpModel
            if (popupModel.showPopUp) {
                // No frequency show all time
                if (popupModel.popUpFrequency == 0) {
                    showPopupDialog(popupModel.popUpUrl)
                } else {
                    val calender = Calendar.getInstance()
                    val dayOfYear = calender.get(Calendar.DAY_OF_YEAR)
                    if (SessionManager.popupDateOfYear != dayOfYear) {
                        SessionManager.popupShowCount = 0
                    }
                    if (popupModel.popUpFrequency > SessionManager.popupShowCount) {
                        SessionManager.popupShowCount = SessionManager.popupShowCount + 1
                        SessionManager.popupDateOfYear = dayOfYear
                        showPopupDialog(popupModel.popUpUrl)
                    }
                }
            }
        })

        initService()
        appUpdateManager()
        UserLogger.logAppOpen()


        //facebookHash()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            Timber.d("BundleLog ${intent.extras?.bundleToString()}")
            val model: FCMData? = intent.getParcelableExtra("data")
            if (model != null) {
                goToNotificationPreview(model)
                intent.removeExtra("data")
            } else {
                val bundleExt = intent.extras
                if (bundleExt != null) {
                    val notificationType = bundleExt.getString("notificationType")
                    if (!notificationType.isNullOrEmpty()) {
                        val fcmModel: FCMData = FCMData(
                            bundleExt.getString("notificationType"),
                            bundleExt.getString("title"),
                            bundleExt.getString("body"),
                            bundleExt.getString("imageUrl"),
                            "",
                            bundleExt.getString("bigText"),
                        )
                        Timber.d("BundleLog FCMData $fcmModel")
                        goToNotificationPreview(fcmModel)
                    }
                }
                intent.removeExtra("notificationType")
            }
        }
    }

    private fun goToNotificationPreview(model: FCMData) {
        val fragment = NotificationPreviewFragment.newInstance(model)
        val tag = NotificationPreviewFragment.fragmentTag
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.mainActivityContainer, fragment, tag)
        ft.addToBackStack(tag)
        ft.commit()
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
                currentFragment is QuickOrderFragment ||
                currentFragment is ProfileFragment ||
                currentFragment is DistrictSelectFragment || currentFragment is DistrictThanaAriaSelectFragment ||
                currentFragment is MapFragment ||
                currentFragment is ServiceBillPayFragment || currentFragment is ServiceBillPayHistoryFragment ||
                currentFragment is PaymentStatementFragment || currentFragment is PaymentStatementDetailFragment ||
                currentFragment is BalanceLoadFragment ||
                currentFragment is ComplainFragment ||
                currentFragment is UnpaidCODFragment ||
                currentFragment is ReferralFragment ||
                currentFragment is CollectionHistoryFragment ||
                currentFragment is OrderTrackingFragment ||
                currentFragment is ReturnStatementFragment || currentFragment is ReturnStatementDetailsFragment
            ) {
                addProductBtnVisibility(false)
            } else {
                addProductBtnVisibility(true)
            }

            if (currentFragment is NotificationPreviewFragment) {
                trackingIV.visibility = View.GONE
                searchIV.visibility = View.GONE
                separetor.visibility = View.GONE
                addProductBtnVisibility(false)
            }
            /*if (currentFragment is ServiceBillPayFragment) {
                addOrderFab.hide()
            }*/
            if (currentFragment is PaymentStatementDetailFragment) {
                downloadTV.visibility = View.VISIBLE
            } else {
                downloadTV.visibility = View.GONE
            }
            if (currentFragment is DashboardFragment) {
                //logoIV.visibility = View.VISIBLE
                searchIV.visibility = View.VISIBLE
                separetor.visibility = View.VISIBLE
                actionBtn.visibility = View.VISIBLE
                trackingIV.visibility = View.GONE
                //moveFabBy(100f)
            } else {
                //logoIV.visibility = View.GONE
                searchIV.visibility = View.GONE
                separetor.visibility = View.GONE
                actionBtn.visibility = View.GONE
                trackingIV.visibility = View.VISIBLE
                //moveFabBy(24f)
            }
            if (currentFragment is OrderTrackingFragment) {
                trackingIV.visibility = View.GONE
            } /*else {
                trackingIV.visibility = View.VISIBLE
            }*/
            if (currentFragment is WebViewFragment) {
                trackingIV.visibility = View.GONE
                searchIV.visibility = View.GONE
                separetor.visibility = View.GONE
                addProductBtnVisibility(false)
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
                is ServiceChargeFragment -> {
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
                is MapFragment -> {
                    currentFragment.onResume()
                }
                is PaymentDetailsFragment -> {
                    currentFragment.onResume()
                }
                is ComplainFragment -> {
                    currentFragment.onResume()
                }
                is DeliveryChargeCalculatorFragment -> {
                    currentFragment.onResume()
                }
                is ServiceBillPayHistoryFragment -> {
                    currentFragment.onResume()
                }
                is BalanceLoadFragment -> {
                    currentFragment.onResume()
                }
                is ReferralFragment -> {
                    currentFragment.onResume()
                }
                is CollectionHistoryFragment -> {
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
                //Timber.d("HomeActivityLog", "onDrawerClosed called")
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
                    Timber.d("ProfileFragment already exist")
                } else {
                    addFragment(ProfileFragment.newInstance(), ProfileFragment.tag)
                }
            }
            R.id.nav_dashboard -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is DashboardFragment) {
                    Timber.d("DashboardFragment already exist")
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
                    //Timber.d( "AddOrderFragmentOne already exist")
                } else {
                    addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
                }
            }
            R.id.nav_orders -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is AllOrdersFragment) {
                    Timber.d("AllOrdersFragment already exist")
                } else {
                    addFragment(AllOrdersFragment.newInstance(), AllOrdersFragment.tag)
                }
            }
            R.id.nav_bill -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ServiceChargeFragment) {
                    Timber.d("BillingofServiceFragment already exist")
                } else {
                    addFragment(ServiceChargeFragment.newInstance(), ServiceChargeFragment.tag)
                }
            }
            R.id.nav_bill_pay -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ServiceBillPayFragment) {
                    Timber.d("ServiceBillPayFragment already exist")
                } else {
                    addFragment(ServiceBillPayFragment.newInstance(), ServiceBillPayFragment.tag)
                }
            }
            R.id.nav_bill_pay_history -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ServiceBillPayHistoryFragment) {
                    Timber.d("ServiceBillPayHistoryFragment already exist")
                } else {
                    addFragment(ServiceBillPayHistoryFragment.newInstance(), ServiceBillPayHistoryFragment.tag)
                }
            }
            R.id.nav_cod_collection -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is CODCollectionFragment) {
                    Timber.d("CODCollectionFragment already exist")
                } else {
                    addFragment(CODCollectionFragment.newInstance(), CODCollectionFragment.tag)
                }

            }
            R.id.nav_order_tracking -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is OrderTrackingFragment) {
                    Timber.d("OrderTrackingFragment already exist")
                } else {
                    addFragment(OrderTrackingFragment.newInstance(""), OrderTrackingFragment.tag)
                }

            }
            R.id.nav_payment_history -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is PaymentStatementFragment) {
                    Timber.d("PaymentHistory already exist")
                } else {
                    addFragment(PaymentStatementFragment.newInstance(), PaymentStatementFragment.tag)
                }
            }
            R.id.nav_payment_request -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is PaymentStatementMenuFragment) {
                    Timber.d("PaymentStatementMenuFragment already exist")
                } else {
                    addFragment(PaymentStatementMenuFragment.newInstance(), PaymentStatementMenuFragment.tag)
                }
            }
            R.id.nav_shipment_change -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ShipmentChargeFragment) {
                    Timber.d("ShipmentChargeFragment already exist")
                } else {
                    addFragment(ShipmentChargeFragment.newInstance(), ShipmentChargeFragment.tag)
                }
            }
            R.id.nav_profile -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ProfileFragment) {
                    Timber.d("ProfileFragment already exist")
                } else {
                    addFragment(ProfileFragment.newInstance(), ProfileFragment.tag)
                }
            }
            R.id.nav_pickup -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ProfileFragment) {
                    Timber.d("ProfileFragment already exist")
                } else {
                    addFragment(ProfileFragment.newInstance(true), ProfileFragment.tag)
                }
            }
            R.id.nav_change_calculator -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is DeliveryChargeCalculatorFragment) {
                    Timber.d("DeliveryChargeCalculatorFragment already exist")
                } else {
                    addFragment(DeliveryChargeCalculatorFragment.newInstance(true), DeliveryChargeCalculatorFragment.tag)
                }

                /*val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is WebViewFragment) {
                    Timber.d( "WebViewFragment already exist")
                } else {
                    try {
                        val fragment = WebViewFragment.newInstance(AppConstant.CHARGE_CALCULATOR, "ডেলিভারি চার্জ ক্যালকুলেটর")
                        addFragment(fragment, WebViewFragment.tag)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }*/
            }
            R.id.nav_map -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is MapFragment) {
                    Timber.d("MapFragment already exist")
                } else {
                    addFragment(MapFragment.newInstance(null), MapFragment.tag)
                }
            }
            R.id.nav_nearby_hub -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is MapFragment) {
                    Timber.d("MapFragment already exist")
                } else {
                    val bundle = bundleOf(
                        "isNearByHubView" to true
                    )
                    addFragment(MapFragment.newInstance(bundle), MapFragment.tag)
                }
            }
            R.id.nav_complain -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ComplainFragment) {
                    Timber.d("ComplainFragment already exist")
                } else {
                    addFragment(ComplainFragment.newInstance(), ComplainFragment.tag)
                }
            }
            R.id.nav_referral -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ReferralFragment) {
                    Timber.d("ComplainFragment already exist")
                } else {
                    addFragment(ReferralFragment.newInstance(), ReferralFragment.tag)
                }
            }
            R.id.nav_express_service -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is WebViewFragment) {
                    Timber.d("WebViewFragment already exist")
                } else {

                    try {
                        val fragment = WebViewFragment.newInstance(AppConstant.EXPRESS_SERVICE_URL, getString(R.string.expressService))
                        addFragment(fragment, WebViewFragment.tag)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            R.id.nav_terms -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is WebViewFragment) {
                    Timber.d("WebViewFragment already exist")
                } else {

                    try {
                        val fragment = WebViewFragment.newInstance(AppConstant.TERMS_URL, getString(R.string.termsCondition))
                        addFragment(fragment, WebViewFragment.tag)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            R.id.nav_privacy -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is WebViewFragment) {
                    Timber.d("WebViewFragment already exist")
                } else {

                    try {
                        val fragment = WebViewFragment.newInstance(AppConstant.POLICY_URL, "প্রাইভেসি পলিসি")
                        addFragment(fragment, WebViewFragment.tag)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            R.id.nav_communication -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is WebViewFragment) {
                    Timber.d("WebViewFragment already exist")
                } else {
                    try {
                        val fragment = WebViewFragment.newInstance(AppConstant.COMMUNICATION_URL, "যোগাযোগ")
                        addFragment(fragment, WebViewFragment.tag)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            R.id.nav_chat -> {
                startActivity(Intent(this, ChatActivity::class.java))
            }
            R.id.nav_return_statement -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ReturnStatementFragment) {
                    Timber.d("ReturnStatementFragment already exist")
                } else {
                    addFragment(ReturnStatementFragment.newInstance(), ReturnStatementFragment.tag)
                }
            }
            R.id.nav_survey -> {
                startActivity(Intent(this, SurveyActivity::class.java))
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
        actionBtn.visibility = View.VISIBLE
        trackingIV.visibility = View.GONE

        val fragment = DashboardFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, DashboardFragment.tag)
        ft?.commit()
    }

    private fun addOrderFragment() {

        UserLogger.logGenie("DetailOrder")
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

    private fun goToNotification() {

        openRightDrawer()

        Handler().postDelayed({

            val currentFragment = supportFragmentManager.findFragmentById(R.id.container_drawer)
            if (currentFragment is NotificationFragment) {
                Timber.d("NotificationFragment already exist")
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

    private fun goToAllOrder(shouldOpenFilter: Boolean) {

        val fragment = AllOrdersFragment.newInstance(shouldOpenFilter)
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
        ft.addToBackStack(AllOrdersFragment.tag)
        ft.commit()
    }

    private fun goToNearByHubMap() {
        val bundle = bundleOf(
            "isNearByHubView" to true
        )
        addFragment(MapFragment.newInstance(bundle), MapFragment.tag)
    }

    fun goToBalanceLoad() {
        onBackPressed()
        addFragment(BalanceLoadFragment.newInstance(), BalanceLoadFragment.tag)
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainActivityContainer, fragment, tag)
        ft.addToBackStack(tag)
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

    //################################################## App Update Manager ########################################//

    private fun appUpdateManager() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, requestCodeAppUpdate)
            }
        }
    }

    private fun checkStalledUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            // For IMMEDIATE
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, requestCodeAppUpdate)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this

        Timber.d("HomeActivityLog onStart Called!")
        if (SessionManager.profileImgUri.isNotEmpty()) {
            Timber.d("HomeActivityLog 1 ${SessionManager.profileImgUri}")
            setProfileImgUrl(SessionManager.profileImgUri)
        } else {
            headerPic.setImageResource(R.drawable.ic_account)
        }
    }

    override fun onStop() {
        ConnectivityReceiver.connectivityReceiverListener = null
        unregisterReceiver(connectivityReceiver)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(LocationUpdatesService.ACTION_BROADCAST))

        checkStalledUpdate()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onDestroy() {
        foregroundService?.removeLocationUpdates()
        super.onDestroy()
    }

    //################################################## Location & Connectivity ########################################//

    fun fetchCurrentLocation() {
        if (isLocationPermission()) {
            val intent = Intent(this, LocationUpdatesService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            timber.log.Timber.tag("LocationLog").d("fetchCurrentLocation")
        }
    }

    private fun initService() {
        gpsUtils = GpsUtils(this)
        turnOnGPS()
        connectivityReceiver = ConnectivityReceiver()
        receiver = MyReceiver()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationUpdatesService.LocalBinder
            foregroundService = binder.getServerInstance()
            mBound = true
            // fetch update
            foregroundService?.setLocationInterval(20)
            foregroundService?.setLocationDifference(20)
            foregroundService?.requestLocationUpdates()
            timber.log.Timber.tag("LocationLog").d("serviceConnection connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            foregroundService = null
            mBound = false
            timber.log.Timber.tag("LocationLog").d("serviceConnection disconnected")
        }
    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val location: Location? = intent?.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION)
            if (location != null) {
                timber.log.Timber.tag("LocationLog").d("current location broadcast ${location.latitude},${location.longitude}")
                currentLocation = location
                viewModel.currentLocation.value = location
                foregroundService?.removeLocationUpdates()
                foregroundService = null
                mBound = false

                unbindService(serviceConnection)
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(parent, "ইন্টারনেট সংযোগ নেই", Snackbar.LENGTH_LONG)
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }

    private fun turnOnGPS() {

        gpsUtils.turnGPSOn {
            isGPS = it
        }
    }

    private fun isLocationPermission(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            return if (permission1 != PackageManager.PERMISSION_GRANTED) {
                val permission1Rationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                if (permission1Rationale) {
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
                } else {
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
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
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        fetchCurrentLocation()
                    } else {
                        val permission1Rationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        if (permission1Rationale) {
                            parent.snackbar("Location permission is needed for core functionality", actionName = "Ok") {
                                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
                            }.show()
                        } else {
                            parent.snackbar("Permission was denied, but is needed for core functionality", actionName = "Settings") {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                startActivity(intent)
                            }.show()
                        }
                    }
                }
            }
        }
    }

    //@SuppressLint("PackageManagerGetSignatures")
    /*private fun facebookHash() {
        try {
            val info = packageManager.getPackageInfo("com.bd.deliverytiger.app", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Timber.d("KeyHash $hash")
                Log.d("KeyHash", hash)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }*/

}
