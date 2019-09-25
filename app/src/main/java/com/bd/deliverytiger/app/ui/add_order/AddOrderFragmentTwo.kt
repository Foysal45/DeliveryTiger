package com.bd.deliverytiger.app.ui.add_order


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
class AddOrderFragmentTwo : Fragment() {

    companion object {
        fun newInstance(): AddOrderFragmentTwo = AddOrderFragmentTwo().apply {
        }
        val tag = AddOrderFragmentTwo::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_order_fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).setToolbarTitle("প্যাকেজ ও কালেকশনের তথ্য")

    }

}
