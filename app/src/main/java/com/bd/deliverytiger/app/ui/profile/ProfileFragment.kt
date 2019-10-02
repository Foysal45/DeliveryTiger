package com.bd.deliverytiger.app.ui.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    companion object{
        fun newInstance(): ProfileFragment = ProfileFragment().apply {  }
        val tag = ProfileFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("এডিট প্রোফাইল")
    }
}
