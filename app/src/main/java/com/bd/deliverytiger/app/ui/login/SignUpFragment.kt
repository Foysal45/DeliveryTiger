package com.bd.deliverytiger.app.ui.login


import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.RetrofitSingletonAPI
import com.bd.deliverytiger.app.api.endpoint.LoginInterface
import com.bd.deliverytiger.app.api.endpoint.OtherApiInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.*
import com.bd.deliverytiger.app.api.model.terms.TermsModel
import com.bd.deliverytiger.app.databinding.FragmentSignUpBinding
import com.bd.deliverytiger.app.utils.*
import com.bd.deliverytiger.app.utils.VariousTask.hideSoftKeyBoard
import com.bd.deliverytiger.app.utils.VariousTask.showShortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpFragment() : Fragment(), View.OnClickListener {

    private lateinit var etCompanyName: EditText
    private lateinit var etSignUpMobileNo: EditText
    private lateinit var etSignUpPassword: EditText
    private lateinit var etSignUpConfirmPassword: EditText
    private lateinit var referCodeET: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView
    private lateinit var paymentGroup: RadioGroup
    private lateinit var instantPaymentLayout: LinearLayout
    private lateinit var bkashNumberET: EditText
    private lateinit var conditionTV: TextView
    private lateinit var referTitle: TextView
    private lateinit var referImage: ImageView
    private lateinit var knownSourceSpinner: AppCompatSpinner
    private lateinit var genderGroup: RadioGroup

    private lateinit var loginInterface: LoginInterface
    private lateinit var checkReferrer: LoginInterface
    private lateinit var otherApiInterface: OtherApiInterface
    private var progressDialog: ProgressDialog? = null

    private var preferredPaymentCycle: String = ""
    private var preferredPaymentMedium: String = ""
    private var gender: String = ""
    private var fbPage: String = ""

    private var binding: FragmentSignUpBinding? = null

    companion object {
        fun newInstance(): SignUpFragment = SignUpFragment()
        val tag: String = SignUpFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentSignUpBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSignUpMobileNo = view.findViewById(R.id.etSignUpMobileNo)
        etCompanyName = view.findViewById(R.id.etCompanyName)
        etSignUpPassword = view.findViewById(R.id.etSignUpPassword)
        etSignUpConfirmPassword = view.findViewById(R.id.etSignUpConfirmPassword)
        referCodeET = view.findViewById(R.id.referCodeET)
        btnSignUp = view.findViewById(R.id.btnSignUp)
        tvLogin = view.findViewById(R.id.tvLogin)
        paymentGroup = view.findViewById(R.id.paymentGroup)
        instantPaymentLayout = view.findViewById(R.id.instantPaymentLayout)
        bkashNumberET = view.findViewById(R.id.bkashNumber)
        conditionTV = view.findViewById(R.id.conditionTV)
        referTitle = view.findViewById(R.id.referTitle)
        referImage = view.findViewById(R.id.referImage)
        knownSourceSpinner = view.findViewById(R.id.spinnerAboutDeliveryTiger)
        genderGroup = view.findViewById(R.id.ganderType)


        binding?.accountNumber?.transformationMethod = null
        binding?.routingNumber?.transformationMethod = null


        loginInterface = RetrofitSingletonAPI.getInstance(requireContext()).create(LoginInterface::class.java)
        checkReferrer = RetrofitSingleton.getInstance(requireContext()).create(LoginInterface::class.java)
        otherApiInterface = RetrofitSingleton.getInstance(requireContext()).create(OtherApiInterface::class.java)

        progressDialog = ProgressDialog(requireContext())
        progressDialog?.setMessage("অপেক্ষা করুন")
        progressDialog?.setCancelable(false)

        initClickListener()
        loadTerms()
    }

    private fun initClickListener() {
        btnSignUp.setOnClickListener(this)
        tvLogin.setOnClickListener(this)
        paymentGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.paymentWeekly -> {
                    preferredPaymentCycle = "week"
                    binding?.paymentMedium?.isVisible = true
                    bkashNumberET.visibility = View.GONE
                    instantPaymentLayout.visibility = View.GONE
                }
                R.id.paymentInstant -> {
                    preferredPaymentCycle = "instant"
                    binding?.paymentMedium?.isVisible = false
                    bkashNumberET.visibility = View.VISIBLE
                    instantPaymentLayout.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        bkashNumberET.requestFocus()
                    }, 200L)
                    preferredPaymentMedium = ""
                }
            }
        }
        binding?.paymentMedium?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.eft -> {
                    preferredPaymentMedium = "EFT"
                    binding?.EFTInfoLayout?.isVisible = true
                    bkashNumberET.isVisible = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding?.accountName?.requestFocus()
                    }, 200L)
                }
                R.id.bkash -> {
                    preferredPaymentMedium = "bkash"
                    binding?.EFTInfoLayout?.isVisible = false
                    bkashNumberET.isVisible = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        bkashNumberET.requestFocus()
                    }, 200L)
                }
            }
        }
        genderGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.ganderMale -> {
                    gender = "male"
                }
                R.id.ganderFemale -> {
                    gender = "female"
                }
                R.id.ganderOther -> {
                    gender = "others"
                }
            }
        }
        referTitle.setOnClickListener {
            if (referCodeET.visibility == View.GONE) {
                referCodeET.visibility = View.VISIBLE
            } else {
                referCodeET.visibility = View.GONE
                referCodeET.text.clear()
            }
        }
        referImage.setOnClickListener {
            referTitle.performClick()
        }
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


    private fun checkIfUserExist() {

        progressDialog?.show()
        val mobile = etSignUpMobileNo.text.toString()
        checkReferrer.getUserInfo(UserInfoRequest(mobile)).enqueue(object : Callback<GenericResponse<LoginResponse>> {
            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                progressDialog?.dismiss()
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
            }

            override fun onResponse(call: Call<GenericResponse<LoginResponse>>, response: Response<GenericResponse<LoginResponse>>) {

                if (response.code() == 404) {
                    val referCode = referCodeET.text.toString().trim()
                    if (referCode.isNotEmpty()) {
                        checkReferrerMobile(referCode)
                    } else {
                        sendOTP()
                    }
                } else {
                    progressDialog?.dismiss()
                    showShortToast(context, "এই মোবাইল নম্বর দিয়ে ইতিমধ্যে রেজিস্ট্রেশন করা হয়েছে")
                }
            }

        })
    }

    private fun checkReferrerMobile(referrerMobile: String) {

        progressDialog?.show()
        checkReferrer.checkReferrerMobile(referrerMobile).enqueue(object: Callback<GenericResponse<SignUpResponse>> {
            override fun onResponse(call: Call<GenericResponse<SignUpResponse>>, response: Response<GenericResponse<SignUpResponse>>) {
                progressDialog?.dismiss()
                if (response.code() == 200) {
                    sendOTP()
                } else {
                    context?.toast("রেফার কোড সঠিক নয়")
                }
            }

            override fun onFailure(call: Call<GenericResponse<SignUpResponse>>, t: Throwable) {
                progressDialog?.dismiss()
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
            }

        })
    }

    private fun sendOTP() {

        progressDialog?.show()

        val mobileNo = etSignUpMobileNo.text.toString()
        val requestBody = OTPRequestModel(mobileNo, mobileNo)
        loginInterface.sendOTP(requestBody).enqueue(object : Callback<OTPResponse> {
            override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                //Timber.e("userUserRegister", "failed " + t.message)
                progressDialog?.dismiss()
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
            }

            override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                progressDialog?.dismiss()
                if (response.isSuccessful && response.body() != null && isAdded) {
                    //Timber.e("userUserRegister", response.body().toString())
                    showShortToast(context, response.body()!!.model ?: "Send")
                    goToSignUpOTP()
                } else {
                    context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
                }
            }
        })

    }

    private fun loadTerms() {

        otherApiInterface.loadTerms().enqueue(object : Callback<GenericResponse<TermsModel>> {
            override fun onFailure(call: Call<GenericResponse<TermsModel>>, t: Throwable) {
            }

            override fun onResponse(call: Call<GenericResponse<TermsModel>>, response: Response<GenericResponse<TermsModel>>) {

                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null){
                        val termsConditions = response.body()!!.model.registerTermsConditions ?: "Terms and Conditions"
                        //val termsConditions = "Terms and Conditions"
                        conditionTV.text = HtmlCompat.fromHtml(termsConditions, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                }
            }

        })
    }

    private fun validate(): Boolean {

        hideKeyboard()
        val password = etSignUpPassword.text.toString().trim()
        val confirmPassword = etSignUpConfirmPassword.text.toString().trim()

        if (etCompanyName.text.toString().isEmpty()) {
            context?.toast(getString(R.string.write_name))
            etCompanyName.requestFocus()
            return false
        }
        if (!isExistSpecialCharacter(etCompanyName.text.toString())){
            context?.toast(getString(R.string.write_correct_name))
            etCompanyName.requestFocus()
            return false
        }
        if (etSignUpMobileNo.text.toString().isEmpty()) {
            showShortToast(context, getString(R.string.write_phone_number))
            etSignUpMobileNo.requestFocus()
            return false
        }
        if (!Validator.isValidMobileNumber(etSignUpMobileNo.text.toString()) || etSignUpMobileNo.text.toString().length < 11) {
            showShortToast(context, getString(R.string.write_proper_phone_number_recharge))
            etSignUpMobileNo.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            showShortToast(context, getString(R.string.write_password))
            return false
        }
        if (!isAlphaNumericPassword(password)) {
            showShortToast(context, getString(R.string.password_pattern))
            return false
        }
        if (password != confirmPassword) {
            showShortToast(context, getString(R.string.match_pass))
            return false
        }
        if (gender.isEmpty()) {
            context?.toast("জেন্ডার সিলেক্ট করুন")
            return false
        }
        if (referCodeET.text.toString().isNotEmpty()) {
            val referCode = referCodeET.text.toString().trim()
            if (!Validator.isValidMobileNumber(referCode) || referCode.length < 11) {
                showShortToast(context, "সঠিক রেফার কোড লিখুন")
                referCodeET.requestFocus()
                return false
            }
        }
        if (preferredPaymentCycle.isEmpty()) {
            showShortToast(context, "পেমেন্ট টাইপ সিলেক্ট করুন")
            return false
        }
        if (preferredPaymentCycle == "instant") {
            val bkashNumber = bkashNumberET.text.toString().trim()
            if (!Validator.isValidMobileNumber(bkashNumber) || bkashNumber.length < 11){
                showShortToast(context, "সঠিক বিকাশ মোবাইল নম্বর লিখুন")
                bkashNumberET.requestFocus()
                return false
            }
        }
        if (preferredPaymentCycle == "week") {

            if (preferredPaymentMedium.isEmpty()) {
                showShortToast(context, "পেমেন্ট টাইপ সিলেক্ট করুন")
                return false
            }

            val accountName = binding?.accountName?.text?.toString()?.trim() ?: ""
            val accountNumber = binding?.accountNumber?.text?.toString()?.trim() ?: ""
            val bankName = binding?.bankName?.text?.toString()?.trim() ?: ""
            val branchName = binding?.branchName?.text?.toString()?.trim() ?: ""
            val routingNumber = binding?.routingNumber?.text?.toString()?.trim() ?: ""
            val bkashNumber = bkashNumberET.text.toString().trim()

            if (preferredPaymentMedium == "EFT") {
                if (accountName.isEmpty()) {
                    context?.toast("একাউন্ট নাম লিখুন")
                    return false
                }
                if (accountNumber.isEmpty()) {
                    context?.toast("একাউন্ট নম্বর লিখুন")
                    return false
                }
                if (bankName.isEmpty()) {
                    context?.toast("ব্যাক নাম লিখুন")
                    return false
                }
                if (branchName.isEmpty()) {
                    context?.toast("ব্রাঞ্চ নাম লিখুন")
                    return false
                }
                if (routingNumber.isEmpty()) {
                    context?.toast("রাউটিং নম্বর লিখুন")
                    return false
                }
            } else if (preferredPaymentMedium == "bkash") {
                if (!Validator.isValidMobileNumber(bkashNumber) || bkashNumber.length < 11){
                    showShortToast(context, "সঠিক বিকাশ মোবাইল নম্বর লিখুন")
                    bkashNumberET.requestFocus()
                    return false
                }
            }
        }

        fbPage = binding?.facebookPage?.text?.toString() ?: ""
        if (fbPage.isNotEmpty()) {
            if (!fbPage.contains("https://www.facebook.com/")) {
                context?.toast("Invalid format (https://www.facebook.com/yourPage)")
                return false
            }
        }

        return true
    }

    private fun goToSignUpOTP() {

        val companyName = etCompanyName.text.toString()
        val mobile = etSignUpMobileNo.text.toString()
        val password = etSignUpPassword.text.toString().trim()
        val referCode = referCodeET.text.toString()
        val bkashNumber = bkashNumberET.text.toString().trim()
        val accountName = binding?.accountName?.text?.toString()?.trim() ?: ""
        val accountNumber = binding?.accountNumber?.text?.toString()?.trim() ?: ""
        val bankName = binding?.bankName?.text?.toString()?.trim() ?: ""
        val branchName = binding?.branchName?.text?.toString()?.trim() ?: ""
        val routingNumber = binding?.routingNumber?.text?.toString()?.trim() ?: ""
        val knowingSource: String = if (knownSourceSpinner.selectedItemPosition == 0) "" else knownSourceSpinner.selectedItem.toString()

        val fragment = SignUpOTPFragment.newInstance(companyName, mobile, password, referCode,
            bkashNumber, preferredPaymentCycle, knowingSource,
            accountName, accountNumber, bankName, branchName, routingNumber, gender, fbPage
        )
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

}
