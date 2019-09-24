package com.bd.deliverytiger.app.ui.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bd.deliverytiger.app.R

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : Fragment() {

    companion object {
        fun newInstance(): ResetPasswordFragment = ResetPasswordFragment().apply {}
        val tag = ResetPasswordFragment::class.java.name
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }


}
