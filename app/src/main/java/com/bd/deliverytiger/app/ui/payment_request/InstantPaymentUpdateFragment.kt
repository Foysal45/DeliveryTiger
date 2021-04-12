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
import java.text.SimpleDateFormat
import java.util.*

class InstantPaymentUpdateFragment : Fragment() {

    private var binding: FragmentInstantPaymentUpdateBinding? = null
    private val viewModel: InstantPaymentUpdateViewModel by inject()

    private var preferredPaymentCycle = ""
    private var formattedDate: String = ""

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

        fetchInstantPaymentActivation()
        fetchInstantPaymentStatus()

        binding?.enablePaymentRequestButton?.setOnClickListener{
            val bkashNumber = binding?.bkashNumber?.text?.toString() ?: ""
            if (bkashNumber.isNotEmpty() && bkashNumber.length == 11) {
                val requestBody = UpdatePaymentCycleRequest(SessionManager.courierUserId, bkashNumber, "instant")
                viewModel.updatePaymentCycle(requestBody).observe(viewLifecycleOwner, Observer { model->
                    if (model.preferredPaymentCycle == "instant") {
                        val formattedDate = DigitConverter.toBanglaDate(model.preferredPaymentCycleDate, "yyyy-MM-dd")
                        binding?.paymentRequestDate?.text = formattedDate
                        context?.toast("ইন্সট্যান্ট পেমেন্ট এক্টিভেট রিকোয়েস্ট সফল হয়েছে")
                    }
                })
            } else {
                context?.toast("সঠিক বিকাশ মোবাইল নম্বর লিখুন")
            }
        }

        binding?.faqBtn?.setOnClickListener {
            goToWebView(AppConstant.FAQ_URL)
        }

    }

    private fun fetchInstantPaymentStatus() {
        //58649
        viewModel.fetchDTMerchantInstantPaymentStatus(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            if (model.lastRequestDate.isNullOrEmpty()) {
                binding?.lastPaymentRequestDate?.text = "-"
                binding?.status?.text = "-"
            } else {
                binding?.lastPaymentRequestDate?.text = DigitConverter.formatDate(model.lastRequestDate, "dd-MM-yyyy HH:mm:ss", "dd MMM',' yyyy hh:mm a")
                binding?.status?.text = if (model.lastPaymentStatus == 0) "${model.lastPaymentAmount}৳ (Processing)" else "৳ ${DigitConverter.toBanglaDigit(model.lastPaymentAmount, true)} (Paid)"
            }
        })
    }

    private fun fetchInstantPaymentActivation() {
        viewModel.getInstantPaymentActivationStatus(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            if (model.hasInstantPayment == 1) {
                binding?.paymentRequestDate?.text = "এক্টিভ করা হয়েছে"
                binding?.requestFormLayout?.visibility = View.GONE
            } else {
                fetchCourierUsersInformation()
            }
        })
    }

    private fun fetchCourierUsersInformation() {
        viewModel.getCourierUsersInformation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer {  model->
            preferredPaymentCycle = model.preferredPaymentCycle ?: ""

            if (preferredPaymentCycle != "instant") {
                binding?.paymentRequestDate?.text = "এক্টিভ করা হয়নি"
                binding?.requestFormLayout?.visibility = View.VISIBLE
            } else {
                if (!model.preferredPaymentCycleDate.isNullOrEmpty()) {
                    formattedDate = DigitConverter.toBanglaDate(model.preferredPaymentCycleDate, "yyyy-MM-dd")
                    binding?.paymentRequestDate?.text = formattedDate
                    binding?.requestFormLayout?.visibility = View.GONE
                }
            }
        })
    }

    private fun goToWebView(url: String) {
        val fragment = WebViewFragment.newInstance(url, "FAQ")
        val tag = WebViewFragment.tag

        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}