package com.bd.deliverytiger.app.ui.payment_request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.instant_payment_update.UpdatePaymentCycleRequest
import com.bd.deliverytiger.app.databinding.FragmentInstantPaymentUpdateBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.AppConstant
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject
import timber.log.Timber

class InstantPaymentUpdateFragment : Fragment() {

    private var binding: FragmentInstantPaymentUpdateBinding? = null
    private val viewModel: InstantPaymentUpdateViewModel by inject()

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

        viewModel.getCourierUsersInformation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer {  model->
            if (model.preferredPaymentCycleDate == ""){
                binding?.paymentRequestDate?.text = "Not Registered"
                binding?.requestFormLayout?.visibility = View.VISIBLE
            }else{
                binding?.requestFormLayout?.visibility = View.GONE
                binding?.paymentRequestDate?.text = model.preferredPaymentCycleDate
            }
        })


        binding?.lastPaymentRequestDate?.text = SessionManager.instantPaymentLastRequestDate
        binding?.status?.text = SessionManager.instantPaymentStatus

        binding?.enablePaymentRequestButton?.setOnClickListener{
            val bkashNumber = binding?.bkashNumber?.text?.toString() ?: ""
            if (bkashNumber.isNotEmpty()) {
                val requestBody = UpdatePaymentCycleRequest(SessionManager.courierUserId, bkashNumber, "instant")
                viewModel.updatePaymentCycle(requestBody).observe(viewLifecycleOwner, Observer { model->
                    binding?.paymentRequestDate?.text = model.preferredPaymentCycleDate
                })

            } else {
                context?.toast("সঠিক বিকাশ মোবাইল নম্বর লিখুন")
            }
        }

        binding?.faqBtn?.setOnClickListener {
            goToWebView(AppConstant.FAQ_URL)
        }

    }

    private fun goToWebView(url: String) {
        val fragment = WebViewFragment.newInstance(url, "FAQ")
        val tag = WebViewFragment.tag

        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    private fun dateFormat(inputDate: String): String {
        var date = inputDate.split("T").first()
        date = DigitConverter.formatDate(date, "yyyy-MM-dd", "dd-MM-yyyy")
        date = DigitConverter.toBanglaDigit(date)
        return date
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}