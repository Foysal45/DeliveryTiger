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
class LoginFragment : Fragment() {

    companion object{
        @JvmStatic
        fun newInstance(): LoginFragment{
            val fragment = LoginFragment()
            return fragment
        }

        fun getFragmentTag(): String? {
            return LoginFragment::class.java.getName()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


}
