package com.bd.deliverytiger.app.ui.live.live_schedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ActivityLiveScheduleBinding
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.google.android.material.textfield.TextInputEditText

@SuppressLint("SetTextI18n")
class LiveScheduleActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLiveScheduleBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.navHostFragment)
        val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener(fallbackListener).build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private val fallbackListener = AppBarConfiguration.OnNavigateUpListener {
        onBackPressed()
        true
    }

    override fun onBackPressed() {
        finish()
        /*if (!navController.navigateUp()) { // When in start destination
            super.onBackPressed()
        }*/
    }

    fun updateToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is TextInputEditText || view is EditText) {
                if (!isPointInsideView(event.rawX, event.rawY, view)) {
                    view.clearFocus()
                    hideKeyboard(view)
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

}