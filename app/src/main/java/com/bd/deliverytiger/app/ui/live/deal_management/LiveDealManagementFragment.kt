package com.bd.deliverytiger.app.ui.live.deal_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.deal_management.DealManagementData
import com.bd.deliverytiger.app.api.model.deal_management.DealManagementRequest
import com.bd.deliverytiger.app.api.model.live.live_deal_management.InsertDealManagementRequestBody
import com.bd.deliverytiger.app.databinding.FragmentLiveDealManagementBinding
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject

class LiveDealManagementFragment : Fragment() {
    private var binding: FragmentLiveDealManagementBinding? = null
    private val viewModel: LiveDealManagementViewModel by inject()
    private  var dataAdapter: LiveDealManagementAdapter = LiveDealManagementAdapter()


    // Paging params
    private var isLoading = false
    private var totalCount = 0
    private val visibleThreshold = 5

    private var liveId: Int = 0

    var requestBody = DealManagementRequest()

    companion object {
        @JvmStatic
        fun newInstance(): LiveDealManagementFragment = LiveDealManagementFragment()

        @JvmField
        val tag: String = LiveDealManagementFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentLiveDealManagementBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestBody.apply {
            profileId = SessionManager.courierUserId
            liveSoldOut = "1"
            orderBy = "DESC"
            orderByType = "deal"
        }

        liveId = arguments?.getInt("liveId", 0) ?: 0

        dataAdapter.onAddClicked = { model ->
            goToProductAdd(model)
        }

        viewModel.fetchDealManagementData(requestBody)

        binding?.recyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                //layoutManager = GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
            }
        }

        viewModel.pagingState.observe(viewLifecycleOwner, Observer { state ->
            isLoading = false
            if (binding?.swipeRefreshLayout?.isRefreshing == true) {
                binding?.swipeRefreshLayout?.isRefreshing = false
            }
            if (state.isInitLoad) {
                dataAdapter.initLoad(state.dataList)
                totalCount = state.totalCount
                binding?.count?.text = DigitConverter.toBanglaDigit(totalCount)
                if (state.dataList.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                } else {
                    binding?.emptyView?.visibility = View.GONE
                }
            } else {
                dataAdapter.pagingLoad(state.dataList)
            }

        })

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (!isLoading && currentItemCount <= lastVisibleItem + visibleThreshold && currentItemCount < totalCount) {
                        isLoading = true
                        requestBody.apply {
                            pagingLowerVal = currentItemCount
                        }
                        viewModel.fetchDealManagementData(requestBody)
                    }
                }
            }
        })

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel.fetchDealManagementData(requestBody)
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
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

    private fun goToProductAdd(model: DealManagementData) {
        val request = InsertDealManagementRequestBody(model.dealId, liveId,  SessionManager.courierUserId)

        viewModel.insertFromOrderManagement(request).observe(viewLifecycleOwner, Observer { data ->
            if (data == 0) {
                context?.toast("প্রোডাক্টটি লাইভে অ্যাড হয়েছে")
            } else if (data == -1) {
                context?.toast("প্রোডাক্টটি লাইভে অ্যাড করা আছে")
            } else {
                context?.toast("প্রোডাক্টটি লাইভে অ্যাড করতে কোথাও কোনো সমস্যা হচ্ছে")
            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}