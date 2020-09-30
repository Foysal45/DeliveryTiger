package com.bd.deliverytiger.app.ui.login


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.RetrofitSingletonAPI
import com.bd.deliverytiger.app.api.endpoint.LoginInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.api.model.login.OTPResponse
import com.bd.deliverytiger.app.api.model.login.UserInfoRequest
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.Validator
import com.bd.deliverytiger.app.utils.VariousTask.hideSoftKeyBoard
import com.bd.deliverytiger.app.utils.VariousTask.showShortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(): SignUpFragment {
            val fragment = SignUpFragment()
            return fragment
        }

        val tag = LoginFragment::class.java.name
    }

    private lateinit var mContext: Context
    private lateinit var etSignUpMobileNo: EditText
    private lateinit var etSignUpPassword: EditText
    private lateinit var etSignUpConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView

    private lateinit var OTPInterface: LoginInterface
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSignUpMobileNo = view.findViewById(R.id.etSignUpMobileNo)
        etSignUpPassword = view.findViewById(R.id.etSignUpPassword)
        etSignUpConfirmPassword = view.findViewById(R.id.etSignUpConfirmPassword)
        btnSignUp = view.findViewById(R.id.btnSignUp)
        tvLogin = view.findViewById(R.id.tvLogin)

        progressDialog = ProgressDialog(mContext)
        progressDialog?.setMessage("অপেক্ষা করুন")
        progressDialog?.setCancelable(false)

        initClickListener()
    }

    private fun initClickListener() {
        btnSignUp.setOnClickListener(this)
        tvLogin.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            btnSignUp -> {
                hideSoftKeyBoard(activity)
                if (validate()) {
                    checkIfUserExist()
                }
            }
            tvLogin -> {
                activity?.onBackPressed()
                //goToLoginFragment()
            }
        }
    }

    private fun sendOTP() {

        progressDialog?.show()

        val mobileNo = etSignUpMobileNo.text.toString()
        val retrofit = RetrofitSingletonAPI.getInstance(mContext)
        val loginInterface = retrofit.create(LoginInterface::class.java)
        val requestBody = OTPRequestModel(mobileNo, mobileNo)
        loginInterface.sendOTP(requestBody).enqueue(object : Callback<OTPResponse> {
            override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                Timber.e("userUserRegister", "failed " + t.message)
                progressDialog?.dismiss()
            }

            override fun onResponse(
                call: Call<OTPResponse>,
                response: Response<OTPResponse>
            ) {
                progressDialog?.dismiss()
                if (response.isSuccessful && response.body() != null && isAdded) {
                    Timber.e("userUserRegister", response.body().toString())
                    showShortToast(context, response.body()!!.model ?: "Send")
                    goToSignUpOTP()
                }
            }
        })

    }


    private fun checkIfUserExist() {

        progressDialog?.show()
        val mobile = etSignUpMobileNo.text.toString()
        val loginInterface = RetrofitSingleton.getInstance(mContext).create(LoginInterface::class.java)
        loginInterface.getUserInfo(UserInfoRequest(mobile)).enqueue(object : Callback<GenericResponse<LoginResponse>> {
            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                progressDialog?.dismiss()
            }

            override fun onResponse(call: Call<GenericResponse<LoginResponse>>, response: Response<GenericResponse<LoginResponse>>) {

                if (response.code() == 404) {
                    sendOTP()
                } else {
                    progressDialog?.dismiss()
                    showShortToast(context, "এই মোবাইল নম্বর দিয়ে ইতিমধ্যে রেজিস্ট্রেশন করা হয়েছে")
                }
            }

        })
    }

    private fun validate(): Boolean {
        var go = true
        if (etSignUpMobileNo.text.toString().isEmpty()) {
            showShortToast(context, getString(R.string.write_phone_number))
            go = false
            etSignUpMobileNo.requestFocus()
        } else if (!Validator.isValidMobileNumber(etSignUpMobileNo.text.toString()) || etSignUpMobileNo.text.toString().length < 11) {
            showShortToast(context, getString(R.string.write_proper_phone_number_recharge))
            go = false
            etSignUpMobileNo.requestFocus()
        } else if (etSignUpPassword.text.toString().isEmpty()) {
            showShortToast(context, getString(R.string.write_password))
            go = false
        } else if (etSignUpPassword.text.toString() != etSignUpConfirmPassword.text.toString()) {
            showShortToast(context, getString(R.string.match_pass))
            go = false
        }
        hideSoftKeyBoard(activity!!)
        return go
    }

    private fun goToSignUpOTP() {

        val mobile = etSignUpMobileNo.text.toString()
        val password = etSignUpPassword.text.toString()

        val fragment = SignUpOTPFragment.newInstance(mobile, password)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, SignUpOTPFragment.tag)
        ft?.commit()
    }

    private fun goToLoginFragment(sendOTP: Boolean = false) {

        val fragment = LoginFragment.newInstance(sendOTP)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        //ft?.addToBackStack(LoginFragment.tag)
        ft?.commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }


}
