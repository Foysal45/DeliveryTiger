package com.bd.deliverytiger.app.ui.bill_pay_history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.databinding.FragmentServiceBillPayHistoryBinding
import com.bd.deliverytiger.app.ui.bill_pay_history.details.BillHistoryDetailsBottomSheet
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

@SuppressLint("SetTextI18n")
class ServiceBillPayHistoryFragment: Fragment() {

    private val viewModel: ServiceBillHistoryViewModel by inject()
    private var binding: FragmentServiceBillPayHistoryBinding? = null

    companion object {
        fun newInstance(): ServiceBillPayHistoryFragment = ServiceBillPayHistoryFragment()
        val tag: String = ServiceBillPayHistoryFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("বিল পে হিস্টোরি")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentServiceBillPayHistoryBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataAdapter = ServiceBillPayHistoryAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onItemClicked = { model ->
            val tag = BillHistoryDetailsBottomSheet.tag
            val dialog = BillHistoryDetailsBottomSheet.newInstance(model)
            dialog.show(childFragmentManager, tag)
        }

        viewModel.fetchBillPayHistory(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNullOrEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                binding?.emptyView?.visibility = View.GONE
                dataAdapter.initLoad(list)
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