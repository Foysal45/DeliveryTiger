package com.bd.deliverytiger.app.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bd.deliverytiger.app.R

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment private constructor(): Fragment() {

    companion object{
        @JvmStatic
        fun newInstance(): SignUpFragment{
            val fragment = SignUpFragment()
            return fragment
        }

        fun getFragmentTag(): String? {
            return SignUpFragment::class.java.getName()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }


}
