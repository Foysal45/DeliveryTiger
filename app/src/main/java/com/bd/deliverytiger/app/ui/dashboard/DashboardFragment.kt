package com.bd.deliverytiger.app.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.billing_of_service.BillingofServiceFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    private lateinit var announcementLayout: ConstraintLayout
    private lateinit var serviceChangeLayout: ConstraintLayout
    private lateinit var serviceChargeTV: TextView
    private lateinit var paymentProcessingLayout: ConstraintLayout
    private lateinit var paymentProcessingTV: TextView
    private lateinit var paymentReadyLayout: ConstraintLayout
    private lateinit var paymentReadyTV: TextView
    private lateinit var addOrderBtn: FloatingActionButton

    companion object{
        fun newInstance(): DashboardFragment = DashboardFragment().apply {

        }
        val tag = DashboardFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        announcementLayout = view.findViewById(R.id.dashboard_announce_layout)
        serviceChangeLayout = view.findViewById(R.id.dashboard_service_charge_layout)
        serviceChargeTV = view.findViewById(R.id.dashboard_service_charge_price_tv)
        paymentProcessingLayout = view.findViewById(R.id.dashboard_payment_processing_layout)
        paymentProcessingTV = view.findViewById(R.id.dashboard_payment_processing_price_tv)
        paymentReadyLayout = view.findViewById(R.id.dashboard_payment_ready_layout)
        paymentReadyTV = view.findViewById(R.id.dashboard_payment_ready_price_tv)
        addOrderBtn = view.findViewById(R.id.dashboard_add_order)

        serviceChargeTV.text = "৳ ${DigitConverter.toBanglaDigit(783)}"
        paymentProcessingTV.text = "৳ ${DigitConverter.toBanglaDigit(567)}"
        paymentReadyTV.text = "৳ ${DigitConverter.toBanglaDigit(1890)}"

        announcementLayout.setOnClickListener {
            val bottomSheet = AnnouncementBottomSheet.newInstance()
            bottomSheet.show(childFragmentManager, AnnouncementBottomSheet.tag)
        }
        serviceChangeLayout.setOnClickListener {
            addFragment(BillingofServiceFragment.newInstance(), BillingofServiceFragment.tag)
        }
        paymentProcessingLayout.setOnClickListener {
            addFragment(CODCollectionFragment.newInstance(), CODCollectionFragment.tag)
        }
        paymentReadyLayout.setOnClickListener {
            addFragment(CODCollectionFragment.newInstance(), CODCollectionFragment.tag)
        }
        addOrderBtn.setOnClickListener {
            addOrderFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("ড্যাশবোর্ড")
    }

    private fun addFragment(fragment: Fragment, tag: String){
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    private fun addOrderFragment() {

        val fragment = AddOrderFragmentOne.newInstance()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AddOrderFragmentOne.tag)
        ft?.addToBackStack(AddOrderFragmentOne.tag)
        ft?.commit()

    }

}
