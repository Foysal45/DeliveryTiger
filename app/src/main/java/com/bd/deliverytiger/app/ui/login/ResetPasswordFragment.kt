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
import com.bd.deliverytiger.app.api.RetrofitSingletonAD
import com.bd.deliverytiger.app.api.`interface`.LoginInterface
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
class ResetPasswordFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var etResetMobileNo: EditText
    private lateinit var btnReset: Button
    private lateinit var tvResetLogin: TextView
    private lateinit var tvRegister: TextView

    private var progressDialog: ProgressDialog? = null

    companion object {
        fun newInstance(): ResetPasswordFragment = ResetPasswordFragment().apply {}
        val tag = ResetPasswordFragment::class.java.name
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etResetMobileNo = view.findViewById(R.id.etResetMobileNo)
        btnReset = view.findViewById(R.id.btnReset)
        tvResetLogin = view.findViewById(R.id.tvResetLogin)
        tvRegister = view.findViewById(R.id.tvRegister)

        progressDialog = ProgressDialog(mContext)
        progressDialog?.setMessage("অপেক্ষা করুন")
        progressDialog?.setCancelable(false)

        intClickEvent()
    }

    private fun intClickEvent() {
        btnReset.setOnClickListener(this)
        tvRegister.setOnClickListener(this)
        tvResetLogin.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        when (p0) {
            btnReset -> {
                if (!Validator.isValidMobileNumber(etResetMobileNo.text.toString()) || etResetMobileNo.text.toString().length < 11) {
                    showShortToast(context, getString(R.string.write_proper_phone_number_recharge))
                    hideSoftKeyBoard(activity!!)
                    etResetMobileNo.requestFocus()
                } else {

                    checkIfUserExist()
                }
            }
            tvRegister -> {
                goToSignUp()
            }
            tvResetLogin -> {
                // addLoginFragment()
                addLoginFragment(false)
            }
        }
    }


    private fun checkIfUserExist() {

        progressDialog?.show()
        val mobile = etResetMobileNo.text.toString()
        val loginInterface = RetrofitSingleton.getInstance(mContext).create(LoginInterface::class.java)
        loginInterface.getUserInfo(UserInfoRequest(mobile)).enqueue(object : Callback<GenericResponse<LoginResponse>> {
            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                progressDialog?.dismiss()
            }

            override fun onResponse(call: Call<GenericResponse<LoginResponse>>, response: Response<GenericResponse<LoginResponse>>) {

                if (response.code() == 404) {
                    progressDialog?.dismiss()
                    showShortToast(context, "এই মোবাইল নম্বর দিয়ে রেজিস্টেশন করা হয়নি")
                } else {
                    sendOTP()
                }
            }

        })
    }

    private fun sendOTP() {

        val mobileNo = etResetMobileNo.text.toString()
        val retrofit = RetrofitSingletonAD.getInstance(mContext)
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
                    goToResetPasswordTwo()
                }
            }
        })

    }

    private fun goToResetPasswordTwo(){

        val mobileNo = etResetMobileNo.text.toString()

        val fragment = ResetPasswordFinalFragment.newInstance(mobileNo)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, ResetPasswordFinalFragment.tag)
        //ft?.addToBackStack(SignUpFragment.tag)
        ft?.commit()

    }

    private fun goToSignUp() {

        val fragment = SignUpFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, SignUpFragment.tag)
        //ft?.addToBackStack(SignUpFragment.tag)
        ft?.commit()
    }

    private fun addLoginFragment(sendOTP: Boolean) {

        val fragment = LoginFragment.newInstance(sendOTP)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        //ft?.addToBackStack(LoginFragment.tag)
        ft?.commit()
    }


}
