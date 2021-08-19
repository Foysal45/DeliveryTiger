package com.bd.deliverytiger.app.ui.unpaid_cod

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.payment_statement.OrderHistoryData
import com.bd.deliverytiger.app.api.model.unpaid_cod.CODDetailsData
import com.bd.deliverytiger.app.databinding.FragmentUnpaidCodBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.payment_statement.details.OrderChargeDetailsBottomSheet
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
        dataAdapter.onItemClicked = { model, tabFlag ->
            showDetails(model, tabFlag)
        }

        fetchCODData()



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

    private fun showDetails(model: CODDetailsData, tabFlag: Int) {
        if (tabFlag == 2) {
            return
        }
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
        val dialog = OrderChargeDetailsBottomSheet.newInstance(orderHistoryData, tabFlag)
        dialog.show(childFragmentManager, OrderChargeDetailsBottomSheet.tag)
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

            /*if (model.availability) {
                binding?.paymentRequestBtn?.visibility = View.VISIBLE
            } else {
                binding?.paymentRequestBtn?.visibility = View.GONE
            }*/


            binding?.statementCard?.visibility = View.VISIBLE
            binding?.codCollection?.text = "${DigitConverter.toBanglaDigit(model.totalCollectedAmount, true)} ৳"
            binding?.codServiceCharge?.text = "- ${DigitConverter.toBanglaDigit(model.totalCodServiceCharge, true)} ৳"
            binding?.deliveryServiceCharge?.text = "- ${DigitConverter.toBanglaDigit(model.totalMerchantReceivable, true)} ৳"
            if ((model?.totalAdvAccReceiveable ?: 0) > 0) {
                binding?.key9?.isVisible = true
                binding?.adjustment?.isVisible = true
                binding?.adjustment?.text = "- ${DigitConverter.toBanglaDigit(model?.totalAdvAccReceiveable.toString())} ৳"
                binding?.key9?.text = "(-) রিটার্ন প্রোডাক্টের পেমেন্ট অ্যাডজাস্টমেন্ট (${DigitConverter.toBanglaDigit(model.advAccReceiveableOrderCount)})"
            }
            binding?.netPayment?.text = "${DigitConverter.toBanglaDigit(model.netAdjustedAmount, true)} ৳"
            netAmount = model.netAdjustedAmount

            binding?.key3?.text = "(-) সার্ভিস চার্জ - COD (${DigitConverter.toBanglaDigit(model.payableOrderCount)})"
            binding?.key4?.text = "(-) সার্ভিস চার্জ - প্রি-পেইড (${DigitConverter.toBanglaDigit(model.receivableOrderCount)})"

            binding?.filterTab?.visibility = View.VISIBLE
            binding?.filterTab?.getTabAt(0)?.text = "COD (${model.payableOrderCount})"
            binding?.filterTab?.getTabAt(1)?.text = "প্রি-পেইড (${model.receivableOrderCount})"
            if (model.advAccReceiveableOrderCount > 0) {
                binding?.filterTab?.let { tabLayout ->
                    tabLayout.addTab(tabLayout.newTab())
                    tabLayout.getTabAt(2)?.text = "রিটার্ন প্রোডাক্টের পেমেন্ট অ্যাডজাস্টমেন্ট (${model.advAccReceiveableOrderCount})"
                }
            }

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
                                dataAdapter.tabFlag = 0
                                dataAdapter.initLoad(model.payableOrders)
                            }
                        }
                        1 -> {
                            if (model.receivableOrders.isEmpty()) {
                                binding?.emptyView?.visibility = View.VISIBLE
                                dataAdapter.clear()
                            } else {
                                binding?.emptyView?.visibility = View.GONE
                                dataAdapter.tabFlag = 1
                                dataAdapter.initLoad(model.receivableOrders)
                            }
                        }
                        2 -> {
                            if (model.advAccReceiveableOrders.isEmpty()) {
                                binding?.emptyView?.visibility = View.VISIBLE
                                dataAdapter.clear()
                            } else {
                                binding?.emptyView?.visibility = View.GONE
                                dataAdapter.tabFlag = 2
                                dataAdapter.initLoad(model.advAccReceiveableOrders)
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