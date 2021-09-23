package com.bd.deliverytiger.app.ui.lead_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class LeadManagementFragment : Fragment() {

    private var binding: FragmentLeadManagementBinding? = null
    private val viewModel: LeadManagementViewModel by inject()

    private  var dataAdapter: LeadManagementAdapter = LeadManagementAdapter()
    private var isLoading = false
    private var isEmpty = false
    //private var totalProduct = 0
    private val visibleThreshold = 5
    private var selectedTab = 1

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
        binding?.allCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
        binding?.deliveredCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
        binding?.phonebookCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
        binding?.addToPhonebookLayout?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)

        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        fetchCustomerInformation(0)

        fetchBanner()

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
                //dataAdapter.initLoad(state.dataList)

                when (selectedTab) {
                    1 -> {
                        dataAdapter.clearList()
                        dataAdapter.initLoad(state.dataList)
                    }
                    2 -> {
                        dataAdapter.clearList()
                        val tempDataList = state.dataList.filter { it.totalOrder > 0 }
                        dataAdapter.initLoad(tempDataList)
                    }
                    3 -> {
                        dataAdapter.clearList()
                        val tempDataList = state.dataList.filter { it.totalOrder == 0 }
                        dataAdapter.initLoad(tempDataList)
                    }
                }


                if (state.dataList.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                } else {
                    binding?.emptyView?.visibility = View.GONE
                }

            } else {
                //dataAdapter.pagingLoad(state.dataList)
                if (state.dataList.isEmpty()) {
                    isLoading = true
                } else {
                    dataAdapter.lazyLoadWithFilter(state.dataList, selectedTab)
                }
                Timber.d("dataAdapter.lazyLoad called")
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

        binding?.allCustomer?.setOnClickListener{
            selectedTab = 1
            isEmptyListCheck(dataAdapter.filter(selectedTab), selectedTab)
        }
        binding?.deliveredCustomer?.setOnClickListener{
            selectedTab = 2
            isEmptyListCheck(dataAdapter.filter(selectedTab), selectedTab)

        }
        binding?.phonebookCustomer?.setOnClickListener{
            selectedTab = 3
            isEmptyListCheck(dataAdapter.filter(selectedTab), selectedTab)

        }
        binding?.addToPhonebookLayout?.setOnClickListener{
            //selectedTab = 4
            displayDialogue()
        }

        dataAdapter.onItemClicked = { model, position ->
            dataAdapter.multipleSelection(model, position)
            binding?.addContactBtn?.isVisible = true
            binding?.clearBtn?.isVisible = true

            if (dataAdapter.getSelectedItemCount() == 0) {
                binding?.clearBtn?.performClick()
            }
        }

        dataAdapter.onOrderDetailsClicked = {model, position ->
            if (model.totalOrder > 0){
                goToCustomerDetailsBottomSheet(model.mobile ?: "")
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

    private fun isEmptyListCheck(isEmpty: Boolean, selectedTab: Int) {
        binding?.emptyView?.isVisible = isEmpty

        binding?.recyclerview?.smoothScrollToPosition(0)
        when(selectedTab) {
            1 -> {
                binding?.allCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
                binding?.deliveredCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
                binding?.phonebookCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
                binding?.addToPhonebookLayout?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            }
            2 -> {
                binding?.allCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
                binding?.deliveredCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
                binding?.phonebookCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
                binding?.addToPhonebookLayout?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            }
            3 -> {
                binding?.allCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
                binding?.deliveredCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
                binding?.phonebookCustomer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_selected)
                binding?.addToPhonebookLayout?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_time_slot_unselected)
            }
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

    private fun fetchBanner() {
        val options = RequestOptions()
            .placeholder(R.drawable.ic_banner_place)
            .signature(ObjectKey(Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toString()))
        binding?.bannerImage?.let { image ->
            Glide.with(image)
                .load("https://static.ajkerdeal.com/images/merchant/chumbok_banner.jpg")
                .apply(options)
                .into(image)
        }
    }

    private fun displayDialogue() {
        /*val tag = CustomerDetailsBottomSheet.tag
        val dialog = CustomerDetailsBottomSheet.newInstance()
        dialog.show(childFragmentManager, tag)*/
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}