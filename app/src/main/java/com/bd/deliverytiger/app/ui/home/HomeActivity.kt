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
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
import com.google.android.material.navigation.NavigationView


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
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
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawer(GravityCompat.START)
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
            val currentFragment = supportFragmentManager.findFragmentById(R.id.mainActivityContainer)
            if (currentFragment != null) {

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

    private fun manageNavigationItemSelection(id: Int) {
        when (id) {
            R.id.nav_orders -> {

            }
            R.id.nav_bill -> {

            }
            R.id.nav_cod_collection -> {

            }
            R.id.nav_order_tracking -> {

            }
            R.id.nav_report -> {

            }
            R.id.nav_shipment_change -> {

            }
            R.id.nav_features -> {

            }
            R.id.nav_logout -> {

                SessionManager.isLogin = false
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
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

    private fun addHomeFragment(){

        val fragment = HomeFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, HomeFragment.tag)
        ft?.commit()
    }

    private fun addOrderFragment(){

        val fragment = AddOrderFragmentOne.newInstance()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainActivityContainer, fragment, AddOrderFragmentOne.tag)
        ft.addToBackStack(AddOrderFragmentOne.tag)
        ft.commit()
    }
}
