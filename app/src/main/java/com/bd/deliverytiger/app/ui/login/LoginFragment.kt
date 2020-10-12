package com.bd.deliverytiger.app.ui.login


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.endpoint.LoginInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.LoginBody
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.button.MaterialButton
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment: Fragment() {

    private val logTag = "LoginFragmentTag"

    private lateinit var alertLayout: LinearLayout
    private lateinit var alertMsgTV: TextView
    private lateinit var mobileET: EditText
    private lateinit var passwordET: EditText
    private lateinit var loginBtn: MaterialButton
    private lateinit var forgotPasswordTV: TextView
    private lateinit var signUpTV: TextView
    //private lateinit var checkRememberMe: AppCompatCheckBox
    private var sendOTP = false
    private var isSessionOut = false

    companion object {
        fun newInstance(sendOTP: Boolean, isSessionOut: Boolean = false): LoginFragment = LoginFragment().apply {
            this.sendOTP = sendOTP
            this.isSessionOut = isSessionOut
        }
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

        alertLayout = view.findViewById(R.id.alert_msg_layout)
        alertMsgTV = view.findViewById(R.id.alert_msg_tv)
        mobileET = view.findViewById(R.id.etLoginMobileNo)
        passwordET = view.findViewById(R.id.etLoginPassword)
        loginBtn = view.findViewById(R.id.btnLogin)
        forgotPasswordTV = view.findViewById(R.id.tvLoginForgotPassword)
        signUpTV = view.findViewById(R.id.tvLoginSignUp)
        //checkRememberMe = view.findViewById(R.id.login_checkBox_remember_me)

        if (BuildConfig.DEBUG){
            mobileET.setText("01777717798")
            passwordET.setText("Rawnation_2020")
        }

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                val token = it.token
                SessionManager.firebaseToken = token
                Timber.d("applicationLog", "FirebaseToken:\n$token")
        }

        if (isSessionOut) {

            val msg = "সেশন আউট হয়ে গেছে।\nঅ্যাপ ব্যবহার করতে পুনরায় লগইন করুন"
            alertMsgTV.text = msg
            alertMsgTV.setTextColor(Color.parseColor("#B28D13"))
            alertLayout.setBackgroundColor(Color.parseColor("#FFF8E1"))
            alertLayout.visibility = View.VISIBLE
        } else {
            alertLayout.visibility = View.GONE
        }

        if (sendOTP){

            val registrationMsg = "রেজিস্ট্রেশন সফল হয়েছে! এখন আপনার লগইন তথ্য দিয়ে লগইন করতে পারেন"
            alertMsgTV.text = registrationMsg
            alertLayout.visibility = View.VISIBLE
        } else {
            alertLayout.visibility = View.GONE
        }

        loginBtn.setOnClickListener {
            login()
        }
        forgotPasswordTV.setOnClickListener {

            addResetPasswordFragment()
        }
        signUpTV.setOnClickListener {
            goToSignUp()
        }

        if (SessionManager.isRememberMe){
            //checkRememberMe.isChecked = true
            mobileET.setText(SessionManager.loginId)
            passwordET.setText(SessionManager.loginPassword)
        }
    }

    private fun login() {

        hideKeyboard()
        if (!validate()) {
            return
        }
        val mobile = mobileET.text.toString()
        val password = passwordET.text.toString()

        val dialog = ProgressDialog(context)
        dialog.setMessage("অপেক্ষা করুন")
        dialog.setCancelable(false)
        dialog.show()
        val loginInterface = RetrofitSingleton.getInstance(requireContext()).create(LoginInterface::class.java)
        loginInterface.userLogin(LoginBody(mobile, password, SessionManager.firebaseToken)).enqueue(object : Callback<GenericResponse<LoginResponse>> {

            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                Timber.d(logTag, "${t.message}")
                dialog.dismiss()
            }

            override fun onResponse(call: Call<GenericResponse<LoginResponse>>, response: Response<GenericResponse<LoginResponse>>) {
                Timber.d(logTag, "${response.code()} ${response.message()}")
                dialog.dismiss()
                if (response.isSuccessful && response.body() != null && isAdded) {
                    if (response.body()!!.model != null) {

                        SessionManager.createSession(response.body()!!.model)
                        SessionManager.isRememberMe = true
                        SessionManager.loginId = mobile
                        SessionManager.loginPassword = password
                        Timber.d(logTag, "Password saved")

                        Timber.d(logTag, "Token: ${SessionManager.accessToken}")
                        Timber.d(logTag, "RefreshToken: ${SessionManager.refreshToken}")
                        saveAppVersion()
                        goToHomeActivity()
                    } else {
                        context?.toast("মোবাইল নম্বর অথবা পাসওয়ার্ড ভুল হয়েছে")
                    }
                } else {
                    context?.toast("মোবাইল নম্বর অথবা পাসওয়ার্ড ভুল হয়েছে")
                }
            }

        })

    }

    private fun validate(): Boolean{
        var go = true
        if (mobileET.text.toString().isEmpty()) {
            activity?.showToast(getString(R.string.write_phone_number))
            go = false
            mobileET.requestFocus()
        } else if (!Validator.isValidMobileNumber(mobileET.text.toString()) || mobileET.text.toString().length < 11) {
            context?.showToast(getString(R.string.write_proper_phone_number_recharge))
            go = false
            mobileET.requestFocus()
        } else if(passwordET.text.toString().isEmpty()) {
            context?.showToast(getString(R.string.write_password))
            go = false
        }
        hideKeyboard()
        return go
    }

    private fun saveAppVersion() {
        try {
            val pInfo = context?.packageManager?.getPackageInfo(context?.packageName ?: "com.bd.deliverytiger.app", 0)
            val version = pInfo?.versionName ?: "version"
            SessionManager.versionName = version

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun goToHomeActivity() {
        startActivity(Intent(activity, HomeActivity::class.java))
        activity?.finish()
    }

    private fun goToSignUp() {

        val fragment = SignUpFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, SignUpFragment.tag)
        ft?.addToBackStack(SignUpFragment.tag)
        ft?.commit()
    }

    private fun addResetPasswordFragment(){
        val fragment = ResetPasswordFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, ResetPasswordFragment.tag)
        ft?.addToBackStack(ResetPasswordFragment.tag)
        ft?.commit()
    }

}

private fun Context?.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
