package com.bd.deliverytiger.app.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onStart() {
        super.onStart()

        addLoginFragment()

    }

    private fun addLoginFragment(){
        val fragment = LoginFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager?.beginTransaction()
        ft?.add(R.id.loginActivityContainer, fragment, LoginFragment.getFragmentTag())
       // ft?.addToBackStack(LoginFragment.getFragmentTag())
        ft?.commit()
    }


    private fun addSignUpFragment(){
        val fragment = SignUpFragment.newInstance()
        val ft: FragmentTransaction? = supportFragmentManager?.beginTransaction()
        ft?.add(R.id.loginActivityContainer, fragment, SignUpFragment.getFragmentTag())
        // ft?.addToBackStack(LoginFragment.getFragmentTag())
        ft?.commit()
    }


}
