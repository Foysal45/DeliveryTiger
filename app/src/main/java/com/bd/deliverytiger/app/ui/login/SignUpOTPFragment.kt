package com.bd.deliverytiger.app.ui.login


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.RetrofitSingletonAPI
import com.bd.deliverytiger.app.api.endpoint.LoginInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.*
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.utils.VariousTask
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SignUpOTPFragment : Fragment() {

    private lateinit var mContext: Context

    private lateinit var OTPET: EditText
    private lateinit var resendBtn: TextView
    private lateinit var backBtn: TextView
    private lateinit var submitBtn: MaterialButton

    private lateinit var OTPInterface: LoginInterface
    private var progressDialog: ProgressDialog? = null

    private var companyName: String = ""
    private var userMobile: String = ""
    private var userPassword: String = ""
    private var referCode: String = ""
    private var bkashNumber: String = ""
    private var preferredPaymentCycle: String = ""
    private var knowingSource: String = ""
    private var accountName = ""
    private var accountNumber = ""
    private var bankName = ""
    private var branchName = ""
    private var routingNumber = ""
    private var gender = ""
    private var fbPage = ""
    private var categoryId: Int = 0
    private var subCategoryId: Int = 0
    private var isBreakableParcel: Boolean = false

    companion object {
        fun newInstance(companyName: String, userMobile: String, userPassword: String, referCode: String,
                        bkashNumber: String, preferredPaymentCycle: String, knowingSource: String,
                        accountName: String, accountNumber: String, bankName: String,
                        branchName: String, routingNumber: String, gender: String, fbPage: String,
                        categoryId: Int, subCategoryId: Int, isBreakableParcel: Boolean
        ): SignUpOTPFragment = SignUpOTPFragment().apply {
            this.companyName = companyName
            this.userMobile = userMobile
            this.userPassword = userPassword
            this.referCode = referCode
            this.bkashNumber = bkashNumber
            this.preferredPaymentCycle = preferredPaymentCycle
            this.knowingSource = knowingSource
            this.accountName = accountName
            this.accountNumber = accountNumber
            this.bankName = bankName
            this.branchName = branchName
            this.routingNumber = routingNumber
            this.gender = gender
            this.fbPage = fbPage
            this.categoryId = categoryId
            this.subCategoryId = subCategoryId
            this.isBreakableParcel = isBreakableParcel
        }

        val tag: String = SignUpOTPFragment::class.java.name
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
        return inflater.inflate(R.layout.fragment_sign_up_ot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        OTPET = view.findViewById(R.id.singUp_OTP_No)
        resendBtn = view.findViewById(R.id.singUp_otp_resend)
        backBtn = view.findViewById(R.id.singUp_back)
        submitBtn = view.findViewById(R.id.singUp_btnReset)

        OTPInterface = RetrofitSingletonAPI.getInstance(mContext).create(LoginInterface::class.java)
        progressDialog = ProgressDialog(mContext)
        progressDialog?.setMessage("অপেক্ষা করুন")
        progressDialog?.setCancelable(false)

        resendBtn.setOnClickListener {
            VariousTask.hideSoftKeyBoard(activity)
            resendOTP()
        }
        backBtn.setOnClickListener {
            VariousTask.hideSoftKeyBoard(activity)
            addLoginFragment(false)
        }
        submitBtn.setOnClickListener {
            VariousTask.hideSoftKeyBoard(activity)
            checkOTP()
            //Test
            //registerUser()
        }
    }

    private fun checkOTP() {

        val OTPCode = OTPET.text.toString()
        if (OTPCode.isEmpty()) {
            VariousTask.showShortToast(context, "OTP কোডটি লিখুন")
            return
        }

        progressDialog?.show()
        OTPInterface.checkOTP(userMobile, OTPCode).enqueue(object : Callback<OTPCheckResponse> {
            override fun onFailure(call: Call<OTPCheckResponse>, t: Throwable) {
                progressDialog?.dismiss()
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
            }

            override fun onResponse(call: Call<OTPCheckResponse>, response: Response<OTPCheckResponse>) {
                if (response.isSuccessful && response.body() != null && isAdded) {
                    if (response.body()!!.model == 1) {
                        registerUser()
                    } else {
                        progressDialog?.dismiss()
                        VariousTask.showShortToast(context, "OTP কোডটি সঠিক নয়")
                    }
                }
            }
        })
    }

    private fun resendOTP() {

        progressDialog?.show()
        val requestBody = OTPRequestModel(userMobile, userMobile)
        OTPInterface.sendOTP(requestBody).enqueue(object : Callback<OTPResponse> {
            override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                //Timber.e("userUserRegister", "failed " + t.message)
                progressDialog?.dismiss()
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
            }

            override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                progressDialog?.dismiss()
                if (response.isSuccessful && response.body() != null && isAdded) {
                    //Timber.e("userUserRegister", response.body().toString())
                    VariousTask.showShortToast(context, response.body()!!.model ?: "Send")
                } else {
                    context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
                }
            }
        })
    }

    private fun registerUser() {

        val loginInterface = RetrofitSingleton.getInstance(mContext).create(LoginInterface::class.java)
        val signUpReqBody = SignUpReqBody(
            userMobile, userPassword, referCode,
            bkashNumber, preferredPaymentCycle, knowingSource, companyName,
            accountName, accountNumber, bankName, branchName, routingNumber, gender, fbPage,
            categoryId, subCategoryId, isBreakableParcel
        )
        Timber.d("signUpReqBody $signUpReqBody")
        loginInterface.userUserRegister(signUpReqBody).enqueue(object : Callback<GenericResponse<SignUpResponse>> {
            override fun onFailure(call: Call<GenericResponse<SignUpResponse>>, t: Throwable) {
                //Timber.e("userUserRegister", "failed " + t.message)
                progressDialog?.dismiss()
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
            }

            override fun onResponse(call: Call<GenericResponse<SignUpResponse>>, response: Response<GenericResponse<SignUpResponse>>) {
                progressDialog?.dismiss()
                if (response.isSuccessful && response.body() != null) {
                    //Timber.e("userUserRegister", response.body().toString())
                    VariousTask.showShortToast(context, getString(R.string.success_in_signin))
                    val model = response.body()?.model
                    if (model != null) {
                        UserLogger.logRegistration(model.courierUserId, "Android")
                    }
                    addLoginFragment(true)
                } else {
                    if (response.body() != null) {
                        VariousTask.showShortToast(context, response.body()!!.errorMessage)
                    }
                    //Timber.e("userUserRegister", "null")
                }
            }

        })

    }

    private fun addLoginFragment(flag: Boolean) {
        val fragment = LoginFragment.newInstance(flag)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        ft?.commit()
    }

}
