package com.bd.deliverytiger.app.ui.balance_load_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.balance_load.BalanceLimitResponse
import com.bd.deliverytiger.app.databinding.FragmentBalanceLoadHistoryBinding


class BalanceLoadHistoryFragment : Fragment() {

    private var binding: FragmentBalanceLoadHistoryBinding? = null
    private var dataAdapter : BalanceLoadHistoryAdapter = BalanceLoadHistoryAdapter()

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

    private fun initData(){
        dataAdapter.initLoad(listOf(BalanceLimitResponse(500,0)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}