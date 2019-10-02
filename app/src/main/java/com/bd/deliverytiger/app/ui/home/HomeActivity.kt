package com.bd.deliverytiger.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentTwo
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.billing_of_service.BillingofServiceFragment
import com.bd.deliverytiger.app.ui.charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.dashboard.DashboardFragment
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.features.DTFeaturesFragment
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.ui.notification.NotificationFragment
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.profile.ProfileFragment
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navViewRight: NavigationView
    private lateinit var addProductIV: ImageView
    private lateinit var toolbarTitleTV: TextView
    private lateinit var notificationIV: ImageView

    private var doubleBackToExitPressedOnce = false
    private var navId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navViewRight = findViewById(R.id.nav_view_2)
        addProductIV = findViewById(R.id.home_toolbar_add)
        toolbarTitleTV = findViewById(R.id.home_toolbar_title)
        notificationIV = findViewById(R.id.home_toolbar_notification)
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
        val headerPic: ImageView = headerView.findViewById(R.id.nav_header_image)
        val headerUserNameTV: TextView = headerView.findViewById(R.id.nav_header_title)
        val headerDesignationTV: TextView = headerView.findViewById(R.id.nav_header_sub_title)
        val profileEdit: ImageView = headerView.findViewById(R.id.nav_header_profile_edit)
        headerUserNameTV.text = SessionManager.companyName
        headerDesignationTV.text = SessionManager.mobile
        Glide.with(this)
            .load("https://deliverytiger.com.bd/assets/images/user.png")
            .apply(RequestOptions().placeholder(R.drawable.ic_account).circleCrop())
            .into(headerPic)
        profileEdit.setOnClickListener {
            navId = R.id.nav_header_profile_edit
            drawerLayout.closeDrawer(GravityCompat.START)

        }

        //addHomeFragment()
        addDashBoardFragment()

        addProductIV.setOnClickListener {
            addOrderFragment()
        }
        notificationIV.setOnClickListener {
            goToNotification()
        }

    }

    override fun onStart() {
        super.onStart()
        Timber.d("HomeActivityLog", "onStart Called!")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

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
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_menu)
            }
            val currentFragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
            if (currentFragment is AddOrderFragmentOne || currentFragment is AddOrderFragmentTwo || currentFragment is DistrictSelectFragment) {
                addProductBtnVisibility(false)
            } else {
                addProductBtnVisibility(true)
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
                is CODCollectionFragment -> {
                    currentFragment.onResume()
                }
                is OrderTrackingFragment -> {
                    currentFragment.onResume()
                }
                is ShipmentChargeFragment -> {
                    currentFragment.onResume()
                }
                is DTFeaturesFragment -> {
                    currentFragment.onResume()
                }
                is ProfileFragment -> {
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
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                manageNavigationItemSelection(navId)
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
                    addFragment(DashboardFragment.newInstance(), DashboardFragment.tag)
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

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is BillingofServiceFragment) {
                    Timber.d("tag", "BillingofServiceFragment already exist")
                } else {
                    addFragment(
                        BillingofServiceFragment.newInstance(),
                        BillingofServiceFragment.tag
                    )
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
            R.id.nav_report -> {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is BillingofServiceFragment) {
                    Timber.d("tag", "Fragment already exist")
                } else {

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
            R.id.nav_features -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is DTFeaturesFragment) {
                    Timber.d("tag", "DTFeaturesFragment already exist")
                } else {
                    addFragment(DTFeaturesFragment.newInstance(), DTFeaturesFragment.tag)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun setToolbarTitle(title: String) {
        toolbarTitleTV.text = title
    }

    fun addProductBtnVisibility(isVisible: Boolean) {
        if (isVisible) {
            addProductIV.visibility = View.VISIBLE
        } else {
            addProductIV.visibility = View.GONE
        }
    }

    private fun addHomeFragment() {

        val fragment = HomeFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, HomeFragment.tag)
        ft?.commit()
    }

    private fun addDashBoardFragment() {

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
}
