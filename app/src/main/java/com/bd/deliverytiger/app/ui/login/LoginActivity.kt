package com.bd.deliverytiger.app.ui.login

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R

class LoginActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        addLoginFragment()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
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

    private fun addLoginFragment(){
        val fragment = LoginFragment.newInstance(false)
        val ft: FragmentTransaction? = supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
       // ft?.addToBackStack(LoginFragment.getFragmentTag())
        ft?.commit()
    }


    private fun addSignUpFragment(){
        val fragment = SignUpFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager?.beginTransaction()
        ft?.add(R.id.loginActivityContainer, fragment, SignUpFragment.tag)
        // ft?.addToBackStack(LoginFragment.getFragmentTag())
        ft?.commit()
    }

    private fun addResetPasswordFragment(){
        val fragment = ResetPasswordFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager?.beginTransaction()
        ft?.add(R.id.loginActivityContainer, fragment, ResetPasswordFragment.tag)
        // ft?.addToBackStack(LoginFragment.tag)
        ft?.commit()
    }


}
