package com.bd.deliverytiger.app.ui.live.live_schedule_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.databinding.FragmentLiveScheduleProductListBinding
import com.bd.deliverytiger.app.ui.live.home.LiveHomeActivity
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class LiveScheduleProductListFragment(): Fragment() {

    private var binding: FragmentLiveScheduleProductListBinding? = null
    private val viewModel: LiveScheduleProductListViewModel by inject()

    private var liveId: Int = 0

    companion object {
        fun newInstance(): LiveScheduleProductListFragment = LiveScheduleProductListFragment()
        val tag: String = LiveScheduleProductListFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLiveScheduleProductListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as LiveHomeActivity).updateToolbarTitle("আমার লাইভ")
        findNavController().currentDestination?.label = "আমার লাইভ"

        liveId = arguments?.getInt("liveId", 0) ?: 0

        val dataAdapter = LiveScheduleProductListAdapter()
        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onStockOutClick = { model, position ->
            val currentStockOutStatus = model.isSoldOut
            viewModel.updateProductSoldOut(model.id, !currentStockOutStatus).observe(viewLifecycleOwner, Observer { flag ->
                if (flag) {
                    dataAdapter.stockOutUpdate(!currentStockOutStatus, position)
                    context?.toast("আপডেট হয়েছে")
                }
            })
        }

        viewModel.fetchLiveProducts(liveId).observe(viewLifecycleOwner, Observer {
            dataAdapter.initList(it)
            if (it.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                binding?.emptyView?.visibility = View.GONE
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

        super.onDestroyView()
    }

}