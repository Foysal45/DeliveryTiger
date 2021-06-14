package com.bd.deliverytiger.app.ui.balance_load_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.balance_load.BalanceLimitResponse
import com.bd.deliverytiger.app.databinding.FragmentBalanceLoadHistoryBinding
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject


class BalanceLoadHistoryFragment : Fragment() {

    private var binding: FragmentBalanceLoadHistoryBinding? = null
    private var dataAdapter : BalanceLoadHistoryAdapter = BalanceLoadHistoryAdapter()
    private val viewModel: BalanceLoadHistoryViewModel by inject()

    companion object {
        fun newInstance(): BalanceLoadHistoryFragment = BalanceLoadHistoryFragment().apply {}
        val tag: String = BalanceLoadHistoryFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentBalanceLoadHistoryBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initView()
        initClickLister()
        initData()
    }

    private fun initView() {
        binding?.recyclerview?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
            }
        }
    }

    private fun initClickLister(){
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

    private fun initData(){
        viewModel.merchantBalanceLoadHistory(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list->
           if (!list.isNullOrEmpty()){
               binding?.emptyView?.visibility = View.GONE
               dataAdapter.initLoad(list)
           }else{
               binding?.emptyView?.visibility = View.VISIBLE
           }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}