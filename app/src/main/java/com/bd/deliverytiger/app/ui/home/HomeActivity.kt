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
import com.bd.deliverytiger.app.ui.billing_of_service.BillingofServiceFragment
import com.bd.deliverytiger.app.ui.charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.features.DTFeaturesFragment
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
import com.google.android.material.navigation.NavigationView


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navViewRight: NavigationView
    private lateinit var addProductIV: ImageView
    private lateinit var toolbarTitleTV: TextView

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

        addHomeFragment()

        addProductIV.setOnClickListener {
            addOrderFragment()
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
            drawerLayout.isDrawerOpen(GravityCompat.START) || drawerLayout.isDrawerOpen(GravityCompat.END)-> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                if (drawerLayout.isDrawerOpen(GravityCompat.END)){
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
                toolbar.setNavigationIcon(R.drawable.ic_arrow_left)
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_menu)
            }
            val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
            if (currentFragment is AddOrderFragmentOne || currentFragment is AddOrderFragmentTwo || currentFragment is DistrictSelectFragment) {
                addProductBtnVisibility(false)
            } else {
                addProductBtnVisibility(true)
            }

            when(currentFragment){
                is AddOrderFragmentOne -> {
                    currentFragment.onResume()
                }
                is AddOrderFragmentTwo -> {
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

    fun openRightDrawer(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        drawerLayout.openDrawer(GravityCompat.END)
    }

    private fun manageNavigationItemSelection(id: Int) {
        when (id) {
            R.id.nav_orders -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is BillingofServiceFragment){
                    Timber.d("tag", "Fragment already exist")
                } else {

                }
            }
            R.id.nav_bill -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is BillingofServiceFragment){
                    Timber.d("tag", "Fragment already exist")
                } else {
                    addFragment(BillingofServiceFragment.newInstance())
                }
            }
            R.id.nav_cod_collection -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is CODCollectionFragment){
                    Timber.d("tag", "Fragment already exist")
                } else {
                    addFragment(CODCollectionFragment.newInstance())
                }

            }
            R.id.nav_order_tracking -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is OrderTrackingFragment){
                    Timber.d("tag", "Fragment already exist")
                } else {
                    addFragment(OrderTrackingFragment.newInstance(""))
                }

            }
            R.id.nav_report -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is BillingofServiceFragment){
                    Timber.d("tag", "Fragment already exist")
                } else {

                }
            }
            R.id.nav_shipment_change -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is ShipmentChargeFragment){
                    Timber.d("tag", "Fragment already exist")
                } else {
                    addFragment(ShipmentChargeFragment.newInstance())
                }
            }
            R.id.nav_features -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
                if (currentFragment is DTFeaturesFragment){
                    Timber.d("tag", "Fragment already exist")
                } else {
                    addFragment(DTFeaturesFragment.newInstance())
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

    private fun addFragment(fragment: Fragment){
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainActivityContainer, fragment, fragment.tag)
        ft.addToBackStack(fragment.tag)
        ft.commit()
    }
}
