package com.bd.deliverytiger.app.ui.login


import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.LoginInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.SignUpReqBody
import com.bd.deliverytiger.app.api.model.login.SignUpResponse
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.Validator
import com.bd.deliverytiger.app.utils.VariousTask.hideSoftKeyBoard
import com.bd.deliverytiger.app.utils.VariousTask.showShortToast
import kotlinx.android.synthetic.main.fragment_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(): ResetPasswordFragment = ResetPasswordFragment().apply {}
        val tag = ResetPasswordFragment::class.java.name
    }

    private lateinit var etResetMobileNo: EditText
    private lateinit var btnReset: Button
    private lateinit var tvResetLogin: TextView
    private lateinit var tvRegister: TextView


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

                }
            }
            tvRegister -> {
                goToSignUp()
            }
            tvResetLogin -> {
                // addLoginFragment()
                activity?.onBackPressed()
            }
        }
    }


    private fun signUp() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("অপেক্ষা করুন")
        progressDialog.show()

        val retrofit = RetrofitSingleton.getInstance(context!!)
        val loginInterface = retrofit.create(LoginInterface::class.java)
        val signUpReqBody =
            SignUpReqBody(etSignUpMobileNo.text.toString(), etSignUpPassword.text.toString())
        loginInterface.userResetPassword(signUpReqBody).enqueue(object :
            Callback<GenericResponse<SignUpResponse>> {
            override fun onFailure(call: Call<GenericResponse<SignUpResponse>>, t: Throwable) {
                Timber.e("userUserRegister", "failed " + t.message)
                progressDialog.hide()
            }

            override fun onResponse(
                call: Call<GenericResponse<SignUpResponse>>,
                response: Response<GenericResponse<SignUpResponse>>
            ) {
                progressDialog.hide()
                if (response.isSuccessful && response.body() != null) {
                    Timber.e("userUserRegister", response.body().toString())
                    showShortToast(context, getString(R.string.success_in_signin))
                    addLoginFragment(true)
                } else {
                    if (response.body() != null) {
                        showShortToast(context, response.body()!!.errorMessage)
                    }
                    Timber.e("userUserRegister", "null")
                }
            }

        })
    }

    private fun goToSignUp() {

        val fragment = SignUpFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, SignUpFragment.tag)
        ft?.addToBackStack(SignUpFragment.tag)
        ft?.commit()
    }

    private fun addLoginFragment(sendOTP: Boolean) {
        val fragment = LoginFragment.newInstance(sendOTP)
        val ft: FragmentTransaction? =
            (context as FragmentActivity?)?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        ft?.addToBackStack(LoginFragment.tag)
        ft?.commit()
    }


}
