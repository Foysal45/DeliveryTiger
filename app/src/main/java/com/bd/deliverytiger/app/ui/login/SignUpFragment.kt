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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment: Fragment(),View.OnClickListener {

    companion object{
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
                      showShortToast(context,getString(R.string.success_in_signin))
                      addLoginFragment()
                  } else {
                      if (response.body() != null) {
                          showShortToast(context,response.body()!!.errorMessage)
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
            showShortToast(context,getString(R.string.write_phone_number))
            go = false
            etSignUpMobileNo.requestFocus()
        } else if (!Validator.isValidMobileNumber(etSignUpMobileNo.text.toString()) || etSignUpMobileNo.text.toString().length < 11) {
            showShortToast(context,getString(R.string.write_proper_phone_number_recharge))
            go = false
            etSignUpMobileNo.requestFocus()
        } else if(etSignUpPassword.text.toString().isEmpty()) {
            showShortToast(context,getString(R.string.write_password))
            go = false
        } else if(etSignUpPassword.text.toString() != etSignUpConfirmPassword.text.toString()) {
            showShortToast(context,getString(R.string.match_pass))
            go = false
        }
        hideSoftKeyBoard(activity!!)
        return go
    }

    private fun addLoginFragment(){
        val fragment = LoginFragment.newInstance(false)
        val ft: FragmentTransaction? = (mContext as FragmentActivity?)?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        // ft?.addToBackStack(LoginFragment.getFragmentTag())
        ft?.commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }


}
