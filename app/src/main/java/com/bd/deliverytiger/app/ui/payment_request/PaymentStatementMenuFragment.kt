package com.bd.deliverytiger.app.ui.payment_request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentPaymentStatementMenuBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity

class PaymentStatementMenuFragment : Fragment() {

    private var binding: FragmentPaymentStatementMenuBinding? = null

    companion object {
        fun newInstance(): PaymentStatementMenuFragment = PaymentStatementMenuFragment()
        val tag: String = PaymentStatementMenuFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPaymentStatementMenuBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.paymentRequestDate?.text = "Feb 2021"
        binding?.status?.text = "Pending"
        binding?.lastPaymentRequestDate?.text = "1 Feb 2021"

        if (binding?.paymentRequestDate?.text.isNullOrEmpty()){
            binding?.paymentRequestDate?.text = "dd-mm-yyyy"
            binding?.enablePaymentRequestButton?.visibility = View.VISIBLE
            binding?.lastPaymentRequestLayout?.visibility = View.GONE
        }else{
            binding?.lastPaymentRequestLayout?.visibility = View.VISIBLE
            binding?.enablePaymentRequestButton?.visibility = View.GONE
        }

        binding?.enablePaymentRequestButton?.setOnClickListener{
            binding?.paymentRequestDate?.text = "jan 2020"
            binding?.lastPaymentRequestLayout?.visibility = View.VISIBLE
            binding?.enablePaymentRequestButton?.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.payment_request))
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}