package com.bd.deliverytiger.app.ui.payment_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.accounts.AccountDetailsResponse
import com.bd.deliverytiger.app.databinding.FragmentPaymentDetailsBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject

class PaymentDetailsFragment: Fragment() {

    private val viewModel: PaymentDetailsViewModel by inject()
    private var binding: FragmentPaymentDetailsBinding? = null

    private var accountDetailsResponse: AccountDetailsResponse? = null

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

        viewModel.getPaymentHistoryDetails(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            accountDetailsResponse = model

            if (model.payableOrders.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                binding?.emptyView?.visibility = View.GONE
                dataAdapter.initLoad(model.payableOrders)
            }

            binding?.statementCard?.visibility = View.VISIBLE
            binding?.orderCount?.text = DigitConverter.toBanglaDigit(model.totalOrderCount)
            binding?.merchantPayment?.text = "${DigitConverter.toBanglaDigit(model.totalMerchantPayable, true)} ৳"
            binding?.accountReceive?.text = "- ${DigitConverter.toBanglaDigit(model.totalMerchantReceivable, true)} ৳"
            binding?.netPayment?.text = "${DigitConverter.toBanglaDigit(model.netAdjustedAmount, true)} ৳"

            binding?.filterTab?.visibility = View.VISIBLE
            binding?.filterTab?.getTabAt(0)?.text = "মার্চেন্ট পেমেন্ট (${DigitConverter.toBanglaDigit(model.payableOrderCount.toString())})"
            binding?.filterTab?.getTabAt(1)?.text = "একাউন্ট রিসিভ (${DigitConverter.toBanglaDigit(model.receivableOrderCount.toString())})"
            binding?.filterTab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            if (model.payableOrders.isEmpty()) {
                                binding?.emptyView?.visibility = View.VISIBLE
                                dataAdapter.clear()
                            } else {
                                binding?.emptyView?.visibility = View.GONE
                                dataAdapter.initLoad(model.payableOrders)
                            }
                        }
                        1 -> {
                            if (model.receivableOrders.isEmpty()) {
                                binding?.emptyView?.visibility = View.VISIBLE
                                dataAdapter.clear()
                            } else {
                                binding?.emptyView?.visibility = View.GONE
                                dataAdapter.initLoad(model.receivableOrders)
                            }
                        }
                    }
                }
            })

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