package com.bd.deliverytiger.app.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment private constructor(): Fragment() {

    companion object{
        fun newInstance():LoginFragment = LoginFragment().apply {}
        val tag = LoginFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
