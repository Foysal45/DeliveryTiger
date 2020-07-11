package com.bd.deliverytiger.app.ui.payment_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentPaymentHistoryBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.payment_history.details.PaymentHistoryDetailFragment
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject


class PaymentHistoryFragment: Fragment() {

    private val viewModel: PaymentHistoryViewModel by inject()
    private var binding: FragmentPaymentHistoryBinding? = null
    private lateinit var dataAdapter: PaymentHistoryAdapter

    companion object {
        fun newInstance(): PaymentHistoryFragment = PaymentHistoryFragment()
        val tag: String = PaymentHistoryFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentPaymentHistoryBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dataAdapter = PaymentHistoryAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onItemClicked = {
            it.transactionNo?.let { id ->
                goToDetails(id)
            }
        }

        // ToDo: remove
        viewModel.getPaymentHistory(35800/*SessionManager.courierUserId*/).observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                dataAdapter.initLoad(list)
                if (list.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                } else {
                    binding?.emptyView?.visibility = View.GONE
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

    private fun goToDetails(transactionId: String) {

        val fragment = PaymentHistoryDetailFragment.newInstance(transactionId)
        val tag = PaymentHistoryDetailFragment.tag

        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("পেমেন্ট ইতিবৃত্ত")
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}