package com.bd.deliverytiger.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
import com.google.android.material.navigation.NavigationView


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private var doubleBackToExitPressedOnce = false
    private var navId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        drawerListener()

        addHomeFragment()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    private fun addHomeFragment(){

        val fragment = HomeFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, HomeFragment.tag)
        ft?.commit()
    }


}
