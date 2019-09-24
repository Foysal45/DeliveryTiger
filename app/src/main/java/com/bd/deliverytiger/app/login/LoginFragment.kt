package com.bd.deliverytiger.app.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.google.android.material.button.MaterialButton

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment private constructor(): Fragment() {

    private val logTag = "LoginFragmentTag"

    private lateinit var mobileET: EditText
    private lateinit var passwordET: EditText
    private lateinit var loginBtn: MaterialButton
    private lateinit var forgotPasswordTV: TextView
    private lateinit var signUpTV: TextView

    companion object{
        fun newInstance():LoginFragment = LoginFragment().apply {}
        val tag = LoginFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mobileET = view.findViewById(R.id.etLoginMobileNo)
        passwordET = view.findViewById(R.id.etLoginPassword)
        loginBtn = view.findViewById(R.id.btnLogin)
        forgotPasswordTV = view.findViewById(R.id.tvLoginForgotPassword)
        signUpTV = view.findViewById(R.id.tvLoginSignUp)

        loginBtn.setOnClickListener {

        }
        forgotPasswordTV.setOnClickListener {

        }
        signUpTV.setOnClickListener {
            goToSignUp()
        }
    }

    private fun goToSignUp() {

        val fragment = SignUpFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, SignUpFragment.getFragmentTag())
        ft?.commit()
    }
}
