package com.bd.deliverytiger.app.ui.payment_history.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.databinding.FragmentPaymentHistoryDetailBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class PaymentHistoryDetailFragment: Fragment() {

    private val viewModel: PaymentHistoryDetailViewModel by inject()
    private var binding: FragmentPaymentHistoryDetailBinding? = null

    private lateinit var dataAdapter: PaymentHistoryDetailsAdapter
    private var transactionId: String = ""

    companion object {
        fun newInstance(transactionId: String): PaymentHistoryDetailFragment = PaymentHistoryDetailFragment().apply {
            this.transactionId = transactionId
        }
        val tag: String = PaymentHistoryDetailFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPaymentHistoryDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dataAdapter = PaymentHistoryDetailsAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        viewModel.getPaymentHistoryDetails(transactionId).observe(viewLifecycleOwner, Observer {
            it?.orderList?.let { list ->
                dataAdapter.initLoad(list)
                if (list.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                    binding?.header?.info1?.text = "মোট অর্ডার: ০ টি"
                    binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ০ ৳"
                } else {
                    binding?.emptyView?.visibility = View.GONE
                    binding?.header?.info1?.text = "মোট অর্ডার: ${DigitConverter.toBanglaDigit(list.size)} টি"
                    binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ${DigitConverter.toBanglaDigit(it.netPaidAmount)} ৳"
                }
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

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("পেমেন্ট বৃত্তান্ত")
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}