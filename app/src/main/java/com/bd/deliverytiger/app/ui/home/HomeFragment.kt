package com.bd.deliverytiger.app.ui.home


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.features.DTFeaturesFragment

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    companion object{
        @JvmStatic
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            return fragment
        }
        val tag = HomeFragment::class.java.name

    }

    private lateinit var mContext: Context
    private lateinit var btnAddOrder: Button
    private lateinit var ivSurpriseBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAddOrder = view.findViewById(R.id.btnAddOrder)
        ivSurpriseBtn = view.findViewById(R.id.ivSurpriseBtn)

        (activity as HomeActivity).setToolbarTitle("Delivery Tiger")
    }

    override fun onStart() {
        super.onStart()
        btnAddOrder.setOnClickListener {
            addOrderFragment()
        }

        ivSurpriseBtn.setOnClickListener {
            addFeaturesFragment()
        }
    }

    private fun addOrderFragment(){
        val fragment = AddOrderFragmentOne.newInstance()
        val ft: FragmentTransaction? = (mContext as FragmentActivity?)?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, AddOrderFragmentOne.tag)
        ft?.addToBackStack(AddOrderFragmentOne.tag)
        ft?.commit()
    }

    private fun addFeaturesFragment(){
        val fragment = DTFeaturesFragment.newInstance()
        val ft: FragmentTransaction? = (mContext as FragmentActivity?)?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, DTFeaturesFragment.tag)
        ft?.addToBackStack(DTFeaturesFragment.tag)
        ft?.commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }


}
