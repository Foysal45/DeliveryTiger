package com.bd.deliverytiger.app.login


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.LoginInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.LoginBody
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment private constructor() : Fragment() {

    private val logTag = "LoginFragmentTag"

    private lateinit var mobileET: EditText
    private lateinit var passwordET: EditText
    private lateinit var loginBtn: MaterialButton
    private lateinit var forgotPasswordTV: TextView
    private lateinit var signUpTV: TextView

    companion object {
        fun newInstance(): LoginFragment = LoginFragment().apply {}
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

        mobileET.setText("01844172323")
        passwordET.setText("01844172323")

        loginBtn.setOnClickListener {
            login()
        }
        forgotPasswordTV.setOnClickListener {

            context?.showToast("Under development")
        }
        signUpTV.setOnClickListener {
            goToSignUp()
        }
    }

    private fun login() {

        val mobile = mobileET.text.toString()
        val password = passwordET.text.toString()

        val dialog = ProgressDialog(context)
        dialog.setMessage("Please wait")
        dialog.show()
        val loginInterface = RetrofitSingleton.getInstance(context!!).create(LoginInterface::class.java)
        loginInterface.userLogin(LoginBody(mobile, password)).enqueue(object :
            Callback<GenericResponse<LoginResponse>> {
            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                Timber.d(logTag, "${t.message}")
                dialog.hide()
            }

            override fun onResponse(
                call: Call<GenericResponse<LoginResponse>>,
                response: Response<GenericResponse<LoginResponse>>
            ) {
                Timber.d(logTag, "${response.code()} ${response.message()}")
                dialog.hide()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.model != null) {

                        SessionManager.accessToken = response.body()!!.model.token

                    }
                }
            }

        })

    }

    private fun goToSignUp() {

        val fragment = SignUpFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, SignUpFragment.getFragmentTag())
        ft?.addToBackStack(SignUpFragment.getFragmentTag())
        ft?.commit()
    }
}

private fun Context?.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
