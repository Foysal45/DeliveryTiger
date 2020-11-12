package com.bd.deliverytiger.app.ui.unpaid_cod

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.login.OTPRequestModel
import com.bd.deliverytiger.app.api.model.payment_statement.OrderHistoryData
import com.bd.deliverytiger.app.databinding.FragmentUnpaidCodBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.payment_statement.details.OrderChargeDetailsFragment
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject

@SuppressLint("SetTextI18n")
class UnpaidCODFragment: Fragment() {

    private var binding: FragmentUnpaidCodBinding? = null
    private val viewModel: UnpaidCODViewModel by inject()

    private lateinit var dataAdapter: UnpaidCODAdapter

    private var netAmount: Int = 0

    companion object {
        fun newInstance(): UnpaidCODFragment = UnpaidCODFragment().apply {  }
        val tag: String = UnpaidCODFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("আনপেইড COD কালেকশন")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentUnpaidCodBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAdapter = UnpaidCODAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onItemClicked = { model, isOnlyDelivery ->
            val orderHistoryData = OrderHistoryData(
                orderCode = model.orderCode,
                collectedAmount = model.collectedAmount,
                deliveryCharge = model.deliveryCharge,
                CODCharge = model.CODCharge,
                breakableCharge = model.breakableCharge,
                collectionCharge = model.collectionCharge,
                returnCharge = model.returnCharge,
                packagingCharge = model.packagingCharge,
                amount = model.merchantPayable,
                totalCharge = model.totalCharge
            )
            val dialog = OrderChargeDetailsFragment.newInstance(orderHistoryData, isOnlyDelivery)
            dialog.show(childFragmentManager, OrderChargeDetailsFragment.tag)
        }

        fetchCODData()

        binding?.paymentRequestBtn?.setOnClickListener {
            if (netAmount > 0) {
                if (netAmount > 5000) {
                    sendOTP()
                } else {
                    requestPayment()
                }
            }
        }

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

    private fun sendOTP() {
        viewModel.sendOTP(OTPRequestModel(SessionManager.courierUserId.toString(), SessionManager.mobile)).observe(viewLifecycleOwner, Observer { msg -> {

        } })
    }

    private fun verifyOTP(otpCode: String) {

        viewModel.checkOTP(SessionManager.mobile, otpCode).observe(viewLifecycleOwner, Observer { flag ->
            if (flag) {
                requestPayment()
            }
        })
    }

    private fun requestPayment() {
        viewModel.updateInstantPaymentRequest(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { flag ->
            if (flag) {
                fetchCODData()
            }
        })
    }

    private fun fetchCODData() {
        viewModel.fetchUnpaidCOD(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            //responseModel = model
            if (model.payableOrders.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                binding?.emptyView?.visibility = View.GONE
                dataAdapter.initLoad(model.payableOrders)
            }

            if (model.availability) {
                binding?.paymentRequestBtn?.visibility = View.VISIBLE
            } else {
                binding?.paymentRequestBtn?.visibility = View.GONE
            }


            binding?.statementCard?.visibility = View.VISIBLE
            binding?.codCollection?.text = "${DigitConverter.toBanglaDigit(model.totalCollectedAmount, true)} ৳"
            binding?.codServiceCharge?.text = "- ${DigitConverter.toBanglaDigit(model.totalCodServiceCharge, true)} ৳"
            binding?.deliveryServiceCharge?.text = "- ${DigitConverter.toBanglaDigit(model.totalMerchantReceivable, true)} ৳"
            binding?.netPayment?.text = "${DigitConverter.toBanglaDigit(model.netAdjustedAmount, true)} ৳"
            netAmount = model.netAdjustedAmount

            binding?.filterTab?.visibility = View.VISIBLE
            binding?.filterTab?.getTabAt(0)?.text = "COD (${model.payableOrderCount})"
            binding?.filterTab?.getTabAt(1)?.text = "Only Delivery (${model.receivableOrderCount})"
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
                                dataAdapter.isOnlyDelivery = false
                                dataAdapter.initLoad(model.payableOrders)
                            }
                        }
                        1 -> {
                            if (model.receivableOrders.isEmpty()) {
                                binding?.emptyView?.visibility = View.VISIBLE
                                dataAdapter.clear()
                            } else {
                                binding?.emptyView?.visibility = View.GONE
                                dataAdapter.isOnlyDelivery = true
                                dataAdapter.initLoad(model.receivableOrders)
                            }
                        }
                    }
                }
            })
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}