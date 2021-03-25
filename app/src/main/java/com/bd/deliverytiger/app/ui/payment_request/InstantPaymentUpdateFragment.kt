package com.bd.deliverytiger.app.ui.payment_request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentInstantPaymentUpdateBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.toast

class InstantPaymentUpdateFragment : Fragment() {

    private var binding: FragmentInstantPaymentUpdateBinding? = null

    companion object {
        fun newInstance(): InstantPaymentUpdateFragment = InstantPaymentUpdateFragment()
        val tag: String = InstantPaymentUpdateFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.instant_payment))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentInstantPaymentUpdateBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.paymentRequestDate?.text = "Not Registered"

        binding?.requestFormLayout?.isVisible = true

        binding?.lastPaymentRequestDate?.text = SessionManager.instantPaymentLastRequestDate
        binding?.status?.text = SessionManager.instantPaymentStatus

        binding?.enablePaymentRequestButton?.setOnClickListener{
            val bkashNumber = binding?.bkashNumber?.text?.toString() ?: ""
            if (bkashNumber.isNotEmpty()) {

            } else {
                context?.toast("সঠিক বিকাশ মোবাইল নম্বর লিখুন")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}