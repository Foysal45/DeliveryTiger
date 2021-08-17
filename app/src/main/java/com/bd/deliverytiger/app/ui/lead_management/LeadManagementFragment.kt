package com.bd.deliverytiger.app.ui.lead_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInfoRequest
import com.bd.deliverytiger.app.api.model.lead_management.CustomerInformation
import com.bd.deliverytiger.app.databinding.FragmentLeadManagementBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.lead_management.customer_details_bottomsheet.CustomerDetailsBottomSheet
import com.bd.deliverytiger.app.ui.share.SmsShareDialogue
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class LeadManagementFragment : Fragment() {

    private var binding: FragmentLeadManagementBinding? = null
    private val viewModel: LeadManagementViewModel by inject()

    private  var dataAdapter: LeadManagementAdapter = LeadManagementAdapter()
    private var isLoading = false
    //private var totalProduct = 0
    private val visibleThreshold = 5

    companion object {
        fun newInstance(): LeadManagementFragment = LeadManagementFragment()
        val tag: String = LeadManagementFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.lead_management))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLeadManagementBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickLister()
    }

    private fun initView(){
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        fetchCustomerInformation(0)

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
                        fetchCustomerInformation(currentItemCount)
                    }
                }

            }
        })
    }

    private fun initClickLister(){
        dataAdapter.onItemClicked = { model, position, selection ->
            if (selection){
                dataAdapter.multipleSelection(model, position)
                binding?.addContactBtn?.isVisible = true
                binding?.clearBtn?.isVisible = true
            }else{
                goToCustomerDetailsBottomSheet(model.mobile ?: "")
                binding?.addContactBtn?.isVisible = false
            }
        }

        binding?.addContactBtn?.setOnClickListener{
            if (dataAdapter.getSelectedItemCount() > 0){
                goToSmsSendBottomSheet(dataAdapter.getSelectedItemModelList())
            }

        }

        binding?.clearBtn?.setOnClickListener {
            dataAdapter.clearSelections()
            binding?.clearBtn?.isVisible = false
            binding?.addContactBtn?.isVisible = false
        }
    }

    private fun fetchCustomerInformation(index: Int){
        viewModel.fetchCustomerList(CustomerInfoRequest(SessionManager.courierUserId,index,20), index)
    }

    private fun goToCustomerDetailsBottomSheet(mobile: String) {
        val tag = CustomerDetailsBottomSheet.tag
        val dialog = CustomerDetailsBottomSheet.newInstance(mobile)
        dialog.show(childFragmentManager, tag)
    }

    private fun goToSmsSendBottomSheet(model: List<CustomerInformation>) {
        val tag = SmsShareDialogue.tag
        val dialog = SmsShareDialogue.newInstance(model)
        dialog.show(childFragmentManager, tag)
        dialog.onSend = { isSend ->
            if (isSend) {
                dataAdapter.clearSelections()
            }
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}