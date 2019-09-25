package com.bd.deliverytiger.app.ui.add_order


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.login.LoginFragment
import com.bd.deliverytiger.app.ui.login.SignUpFragment

/**
 * A simple [Fragment] subclass.
 */
class AddOrderFragmentOne : Fragment() {

    companion object{
        @JvmStatic
        fun newInstance(): AddOrderFragmentOne {
            val fragment = AddOrderFragmentOne()
            return fragment
        }
        val tag = AddOrderFragmentOne::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_order_fragment_one, container, false)
    }




}
