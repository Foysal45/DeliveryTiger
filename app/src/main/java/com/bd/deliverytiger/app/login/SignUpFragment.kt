package com.bd.deliverytiger.app.login


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.LoginInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.SignUpReqBody
import com.bd.deliverytiger.app.api.model.login.SignUpResponse
import com.bd.deliverytiger.app.utils.Helper
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.Validator
import kotlinx.android.synthetic.main.fragment_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment: Fragment(),View.OnClickListener {

    companion object{
        @JvmStatic
        fun newInstance(): SignUpFragment{
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

        initClickListener()
    }

    private fun initClickListener() {
        btnSignUp.setOnClickListener(this)
        tvLogin.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0){
            btnSignUp -> {
                signUp()
            }
            tvLogin -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun signUp(){
        if(validate()){
            val progressDialog = ProgressDialog(mContext)
            progressDialog.setMessage("অপেক্ষা করুন")
            progressDialog.show()

            val retrofit = RetrofitSingleton.getInstance(mContext)
            val loginInterface = retrofit.create(LoginInterface::class.java)
            val signUpReqBody = SignUpReqBody(etSignUpMobileNo.text.toString(),etSignUpPassword.text.toString())
            loginInterface.userUserRegister(signUpReqBody).enqueue(object : Callback<GenericResponse<SignUpResponse>>{
                override fun onFailure(call: Call<GenericResponse<SignUpResponse>>, t: Throwable) {
                    Timber.e("userUserRegister","failed "+t.message)
                    progressDialog.hide()
                }

                override fun onResponse(
                    call: Call<GenericResponse<SignUpResponse>>,
                    response: Response<GenericResponse<SignUpResponse>>
                ) {
                    progressDialog.hide()
                  if(response.isSuccessful && response.body() != null){
                      Timber.e("userUserRegister",response.body().toString())
                      showToast(getString(R.string.success_in_signin))
                      addLoginFragment()
                  } else {
                      if (response.body() != null) {
                          showToast(response.body()!!.errorMessage)
                      }
                      Timber.e("userUserRegister","null")
                  }
                }

            })
        }
    }

    private fun validate(): Boolean{
        var go = true
        if (etSignUpMobileNo.text.toString().isEmpty()) {
            showToast(getString(R.string.write_phone_number))
            go = false
            hideSoftKeyBoard()
            etSignUpMobileNo.requestFocus()
            editTextEnableOrDisable(etSignUpMobileNo)
        } else if (!Validator.isValidMobileNumber(etSignUpMobileNo.text.toString()) || etSignUpMobileNo.text.toString().length < 11) {
            showToast(getString(R.string.write_proper_phone_number_recharge))
            go = false
            hideSoftKeyBoard()
            etSignUpMobileNo.requestFocus()
            editTextEnableOrDisable(etSignUpMobileNo)
        } else if(etSignUpPassword.text.toString().isEmpty()) {
            showToast(getString(R.string.write_password))
            go = false
        } else if(etSignUpPassword.text.toString() != etSignUpConfirmPassword.text.toString()) {
            showToast(getString(R.string.match_pass))
            go = false
        }
        hideSoftKeyBoard()
        return go
    }

    private fun addLoginFragment(){
        val fragment = LoginFragment.newInstance()
        val ft: FragmentTransaction? = (mContext as FragmentActivity?)?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        // ft?.addToBackStack(LoginFragment.getFragmentTag())
        ft?.commit()
    }

    // show toast method
    private fun showToast(message: String) {
        val toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.show()
    }

    // clear focus if payment lay blinking
    private fun editTextEnableOrDisable(et: EditText) {
        et.isSelected = false
        et.isFocusable = false
        et.isFocusableInTouchMode = true
    }

    private fun hideSoftKeyBoard() {
        try {  // hide keyboard if its open
            val inputMethodManager = activity!!.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                activity!!.currentFocus!!.windowToken, 0)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }


}
