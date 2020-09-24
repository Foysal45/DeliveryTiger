package com.bd.deliverytiger.app.ui.payment_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.databinding.FragmentPaymentDetailsBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject

class PaymentDetailsFragment: Fragment() {

    private val viewModel: PaymentDetailsViewModel by inject()
    private var binding: FragmentPaymentDetailsBinding? = null

    companion object {
        fun newInstance(): PaymentDetailsFragment = PaymentDetailsFragment().apply {
        }
        val tag: String = PaymentDetailsFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPaymentDetailsBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("পেমেন্ট ডিটেলস")
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataAdapter = PaymentDetailsAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onItemClicked = {
            //val dialog = OrderChargeDetailsFragment.newInstance(it)
            //dialog.show(childFragmentManager, OrderChargeDetailsFragment.tag)
        }

        viewModel.getPaymentHistoryDetails(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                binding?.emptyView?.visibility = View.GONE
                dataAdapter.initLoad(list)
                binding?.statementCard?.visibility = View.VISIBLE

                var merchantPayment = 0
                var accountReceived = 0
                var netPayment = 0

                list.forEach { data ->
                    merchantPayment += data.merchantPayable
                    accountReceived += data.accReceiveable
                }
                netPayment = merchantPayment - accountReceived

                binding?.merchantPayment?.text = "${DigitConverter.toBanglaDigit(merchantPayment, true)} ৳"
                binding?.accountReceive?.text = "- ${DigitConverter.toBanglaDigit(accountReceived, true)} ৳"
                binding?.netPayment?.text = "${DigitConverter.toBanglaDigit(netPayment, true)} ৳"
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}