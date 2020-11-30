package com.bd.deliverytiger.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.setStatusBarColor

class LoginActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        onBackStackChangeListener()

        val isSessionOut = intent.getBooleanExtra("isSessionOut", false)
        if (isSessionOut) {
            addLoginFragment(isSessionOut)
        } else {
            addUserRoleFragment()
        }
        intent.removeExtra("isSessionOut")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            return
        }

    }

    private fun onBackStackChangeListener() {

        supportFragmentManager.addOnBackStackChangedListener {

            /*if (supportFragmentManager.backStackEntryCount > 0) {
            } else {
            }*/

            val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.loginActivityContainer)
            if (currentFragment is OrderTrackingFragment) {
                setStatusBarColor()
            } else {
                setStatusBarColor(R.color.blur)
            }
        }
    }

    override fun onBackPressed() {

        //val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.loginActivityContainer)
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "অ্যাপটি বন্ধ করতে আবার প্রেস করুন", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000L)
        }
    }

    private fun addUserRoleFragment(){

        //val fragment = OrderTrackingFragment.newInstance("")
        val fragment = UserRoleSelectionFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, UserRoleSelectionFragment.tag)
        ft?.commit()
    }

    private fun addLoginFragment(isSessionOut: Boolean){

        val fragment = LoginFragment.newInstance(false, isSessionOut)
        val ft: FragmentTransaction? = supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        // ft?.addToBackStack(LoginFragment.getFragmentTag())
        ft?.commit()
    }

    /*private fun addSignUpFragment(){
        val fragment = SignUpFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager?.beginTransaction()
        ft?.add(R.id.loginActivityContainer, fragment, SignUpFragment.tag)
        // ft?.addToBackStack(LoginFragment.getFragmentTag())
        ft?.commit()
    }*/

    /*private fun addResetPasswordFragment(){
        val fragment = ResetPasswordFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager?.beginTransaction()
        ft?.add(R.id.loginActivityContainer, fragment, ResetPasswordFragment.tag)
        // ft?.addToBackStack(LoginFragment.tag)
        ft?.commit()
    }*/



}
