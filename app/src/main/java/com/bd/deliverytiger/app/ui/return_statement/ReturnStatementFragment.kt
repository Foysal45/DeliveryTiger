package com.bd.deliverytiger.app.ui.return_statement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.databinding.FragmentReturnStatementBinding
import com.bd.deliverytiger.app.utils.SessionManager
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.return_statement.ReturnStatementData
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.return_statement.details.ReturnStatementDetailsFragment
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import java.util.*


class ReturnStatementFragment(): Fragment() {

    private var binding:FragmentReturnStatementBinding? = null
    private val viewModel: ReturnStatementViewModel by inject()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // Paging params
    private var isLoading = false
    //private var totalProduct = 0
    private val visibleThreshold = 5

    private lateinit var dataAdapter:ReturnStatementAdapter

    companion object {
        fun newInstance(): ReturnStatementFragment = ReturnStatementFragment().apply {  }
        val tag: String = ReturnStatementFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.return_statement))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentReturnStatementBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dataAdapter = ReturnStatementAdapter()
        with(binding?.recyclerview!!){
            setHasFixedSize(true)
            layoutManager =LinearLayoutManager(requireContext())
            adapter = dataAdapter
        }

        dataAdapter.onItemClick = { model ->
            goToReturnDetails(model)
        }

        fetchReturnStatementData(0)

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

        viewModel.pagingState.observe(viewLifecycleOwner, Observer { state ->
            isLoading = false
            if (state.isInitLoad) {
                dataAdapter.initLoad(state.dataList)

                if (state.dataList.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                } else {
                    binding?.emptyView?.visibility = View.GONE
                }

            } else {
                dataAdapter.pagingLoad(state.dataList)
                if (state.dataList.isEmpty()) {
                    isLoading = true
                }
            }
        })

        binding?.recyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    if (!isLoading && currentItemCount <= lastVisibleItem + visibleThreshold) {
                        isLoading = true
                        fetchReturnStatementData(currentItemCount)
                    }
                }

            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun fetchReturnStatementData(index: Int, count: Int = 20) {
        val courierUserId = SessionManager.courierUserId
        viewModel.fetchReturnCount(courierUserId, index, count )
    }

    private fun goToReturnDetails(model: ReturnStatementData) {

        val fragment = ReturnStatementDetailsFragment.newInstance(model)
        val tag = ReturnStatementDetailsFragment.tag

        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()

        val bundle = bundleOf(
            "returnStatementData" to model
        )
        findNavController().navigate(R.id.nav_returnStatement_returnStatementDetails, bundle)
    }


}