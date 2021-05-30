package com.bd.deliverytiger.app.ui.live.live_order_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.databinding.FragmentLiveOrderListBinding
import org.koin.android.ext.android.inject

class LiveOrderListFragment : Fragment() {

    private var binding: FragmentLiveOrderListBinding? = null
    private val viewModel: LiveOrderListViewModel by inject()

    private var liveId: Int = 0
    private lateinit var dataAdapter: LiveOrderListAdapter

    companion object {
        fun newInstance(bundle: Bundle): LiveOrderListFragment = LiveOrderListFragment().apply {
        }
        val tag: String = LiveOrderListFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentLiveOrderListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liveId = arguments?.getInt("liveId", 0) ?: 0

        dataAdapter = LiveOrderListAdapter()
        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        }

        setAdapterValue()
    }

    private fun setAdapterValue() {
        binding?.recyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = dataAdapter
                fetchLiveOrderList()
            }
        }
    }

    private fun fetchLiveOrderList() {
        viewModel.fetchLiveOrderList(liveId).observe(viewLifecycleOwner, Observer {
            dataAdapter.initLoad(it)
        })
    }

}