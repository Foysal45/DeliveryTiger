package com.bd.deliverytiger.app.ui.payment_statement.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.payment_statement.OrderHistoryData
import com.bd.deliverytiger.app.databinding.FragmentPaymentStatementDetailBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject
import timber.log.Timber

class PaymentStatementDetailFragment: Fragment() {

    private val viewModel: PaymentStatementDetailViewModel by inject()
    private var binding: FragmentPaymentStatementDetailBinding? = null

    private lateinit var dataAdapter: PaymentStatementDetailsAdapter
    private var transactionId: String = ""

    companion object {
        fun newInstance(transactionId: String): PaymentStatementDetailFragment = PaymentStatementDetailFragment().apply {
            this.transactionId = transactionId
        }
        val tag: String = PaymentStatementDetailFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPaymentStatementDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dataAdapter = PaymentStatementDetailsAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onItemClicked = {
            val dialog = OrderChargeDetailsFragment.newInstance(it)
            dialog.show(childFragmentManager, OrderChargeDetailsFragment.tag)
        }

        viewModel.getPaymentHistoryDetails(transactionId).observe(viewLifecycleOwner, Observer { model ->
            model?.orderList?.let { list ->
                //dataAdapter.initLoad(list)
                if (list.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                    binding?.statementCard?.visibility = View.GONE
                    binding?.filterTab?.visibility = View.GONE
                    //binding?.header?.info1?.text = "মোট অর্ডার: ০ টি"
                    //binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ০ ৳"
                } else {
                    binding?.emptyView?.visibility = View.GONE
                    binding?.statementCard?.visibility = View.VISIBLE
                    binding?.filterTab?.visibility = View.VISIBLE
                    //binding?.header?.info1?.text = "মোট অর্ডার: ${DigitConverter.toBanglaDigit(list.size)} টি"
                    //binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ${DigitConverter.toBanglaDigit(it.netPaidAmount)} ৳"
                }
            }

            binding?.transactionNo?.text = model?.transactionNo
            binding?.paymentMedium?.text = model?.modeOfPayment
            binding?.orderCount?.text = "${DigitConverter.toBanglaDigit(model?.totalOrderCount.toString())} টি"
            binding?.totalCollectionAmount?.text = "${DigitConverter.toBanglaDigit(model?.netCollectedAmount.toString())} ৳"
            binding?.totalCharge?.text = "- ${DigitConverter.toBanglaDigit(model?.netTotalCharge.toString())} ৳"
            binding?.totalAdjustment?.text = "- ${DigitConverter.toBanglaDigit(model?.netAdjustedAmount.toString())} ৳"
            binding?.totalPayment?.text = "${DigitConverter.toBanglaDigit(model?.netPaidAmount.toString())} ৳"

            binding?.filterTab?.getTabAt(0)?.text = "পেইড (${DigitConverter.toBanglaDigit(model?.totalCrOrderCount.toString())})"
            binding?.filterTab?.getTabAt(1)?.text = "এডজাস্টেড (${DigitConverter.toBanglaDigit(model?.totalAdOrderCount.toString())})"

            filterOrderList(model?.orderList, "CR")
            binding?.filterTab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            Timber.d("Tab selected 0")
                            filterOrderList(model?.orderList, "CR")
                        }
                        1 -> {
                            filterOrderList(model?.orderList, "CC")
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

    private fun filterOrderList(dataList: List<OrderHistoryData>?, filterKey: String) {

        val filteredList =dataList?.filter { it.type == filterKey }
        if (!filteredList.isNullOrEmpty()) {
            dataAdapter.initLoad(filteredList)
            binding?.emptyView?.visibility = View.GONE
        } else {
            dataAdapter.clear()
            binding?.emptyView?.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("স্টেটমেন্ট ডিটেলস")
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}