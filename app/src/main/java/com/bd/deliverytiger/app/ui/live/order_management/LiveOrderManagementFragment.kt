package com.bd.deliverytiger.app.ui.live.order_management

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.order_management.OrderManagementPendingRequestBody
import com.bd.deliverytiger.app.databinding.FragmentLiveOrderManagementBinding
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LiveOrderManagementFragment : Fragment() {

    private var binding: FragmentLiveOrderManagementBinding? = null
    private val viewModel: LiveOrderManagementViewModel by inject()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private lateinit var dataAdapter: OrderManagementAdapter
    private lateinit var dataAdapterPending: OrderManagementPendingAdapter

    // RequestBody TokenData
    private var mMerchantId = 0
    private var mDateFieldType = 2
    private var mFromDate = ""
    private var mToDate = ""
    private var mOrderStatus = "-1"
    private var mDealId = "-1"
    private var mBookingCode = "-1"
    private var mMobile = "-1"
    private var mDealTitle = "-1"
    private var mDeliveryDistrict = "-1"
    private var mBusinessModel = "-1"

    private var isExcelDownload: Boolean = false

    private var  orderId = 0
    private var  productId = 0
    private var  phoneNumber = ""
    private var  productName = ""


    private lateinit var bundle: Bundle
    private lateinit var mFilterAdapter: OrderManagementFilterAdapter

    //variables
    private var totalProduct = 0
    private var isLoading = false
    private val visibleThreshold = 5
    private var currentTotalCount = 0
    private val mFilterTagList: MutableList<FilterDataModel> = mutableListOf()

    companion object {
        @JvmStatic
        fun newInstance(): LiveOrderManagementFragment = LiveOrderManagementFragment().apply {
        }

        @JvmField
        val tag: String = LiveOrderManagementFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLiveOrderManagementBinding.inflate(inflater, container, false).also{
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataAdapter = OrderManagementAdapter()
        dataAdapterPending = OrderManagementPendingAdapter()
        mMerchantId = SessionManager.courierUserId

        setAdapterValue()
        pendingPagingState()
        setDataToRecyclerView()


        manageSearch()
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            clearFilter()
            clearSearch()
            fetchOrderManagementPendingList(0)
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

        mFilterAdapter = OrderManagementFilterAdapter(requireContext(), mFilterTagList)
        binding?.filterRecyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = mFilterAdapter
            }
        }

        // clear filter
        binding?.searchLayout?.filterClear?.setOnClickListener(View.OnClickListener {
            clearFilter()
            fetchOrderManagementPendingList(0)
            binding?.searchLayout?.filterClear?.visibility = View.GONE
        })

    }

    private fun setAdapterValue() {
        binding?.orderRecyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                Timber.d("FlowOfTheProcess 11")
                adapter = dataAdapterPending
                fetchOrderManagementPendingList(0)
            }
        }
    }

    private fun setDataToRecyclerView() {

        Timber.d("FlowOfTheProcess 30")
        currentTotalCount = 0
        binding?.orderRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    /*val lastVisibleItemPositions = (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                    val lastVisibleItem = when {
                        lastVisibleItemPositions.last() != RecyclerView.NO_POSITION -> {
                            lastVisibleItemPositions.last()
                        }
                        lastVisibleItemPositions.first() != RecyclerView.NO_POSITION -> {
                            lastVisibleItemPositions.first()
                        }
                        else -> {
                            0
                        }
                    }*/

                    Timber.d("onScrolled: CurrentTotalCount: $currentTotalCount, ItemCount: $currentItemCount <= lastVisible: $lastVisibleItem + $visibleThreshold ${!isLoading}")
                    if (!isLoading && currentItemCount <= (lastVisibleItem+visibleThreshold)) {
                        Timber.d("onScrolled: loading CurrentTotalCount: $currentTotalCount, ItemCount: $currentItemCount <= lastVisible: $lastVisibleItem + $visibleThreshold ${!isLoading}")
                        isLoading = true
                        //currentTotalCount += currentItemCount

                        Timber.d("FlowOfTheProcess 31")
                        fetchOrderManagementPendingList(currentItemCount)
                    }
                }
            }
        })
    }

    private fun pendingPagingState() {

        Timber.d("FlowOfTheProcess 41")

        viewModel.pendingPagingState.observe(viewLifecycleOwner, Observer { state ->
            isLoading = false
            binding?.swipeRefreshLayout?.isRefreshing = false

            if (state.dataList.size < 20) {
                isLoading = true
            }

            if (state.isInitLoad) {
                dataAdapterPending.initLoad(state.dataList)
                //totalProduct = state.totalCount
                totalProduct = state.dataList.size

                binding?.searchLayout?.countTV?.text = "মোট " + DigitConverter.toBanglaDigit(totalProduct, false) + " টি অর্ডার পাওয়া গিয়েছে"
                if (state.dataList.isEmpty()) {
                    binding?.searchLayout?.countTV?.visibility = View.GONE
                    binding?.searchLayout?.filterClear?.visibility = View.GONE
                    binding?.emptyView?.visibility = View.VISIBLE
                } else {
                    binding?.searchLayout?.countTV?.visibility = View.VISIBLE
                    //binding?.searchLayout?.filterClear?.visibility = View.VISIBLE
                    binding?.emptyView?.visibility = View.GONE
                }
                if (isExcelDownload) {
                    isExcelDownload = false
                    //generateExcel()
                }
            } else {
                if (state.dataList.isEmpty()) {
                    isLoading = true
                } else {
                    Timber.d("recyclerViewDebug ${state.dataList.size}")
                    dataAdapterPending.pagingLoad(state.dataList)
                }
            }
        })
    }

    private fun fetchOrderManagementPendingList(currentItemCount: Int) {

        Timber.d("FlowOfTheProcess 21")
        binding?.swipeRefreshLayout?.isRefreshing = true

        var channelId = SessionManager.courierUserId
        val requestBody = OrderManagementPendingRequestBody(channelId, orderId, productId, phoneNumber, productName,
            currentItemCount, 20)

        viewModel.fetchOrderManagementPendingList(requestBody)
    }

    private fun manageSearch() {

        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, resources.getStringArray(R.array.order_management_filter).toMutableList())
        binding?.searchLayout?.filterTypeSpinner?.adapter = spinnerAdapter
        binding?.searchLayout?.filterTypeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                when (i) {
                    0 -> {
                        // Deal code
                        binding?.searchLayout?.searchET?.inputType = InputType.TYPE_CLASS_NUMBER
                        binding?.searchLayout?.searchET?.text?.clear()
                    }
                    1 -> {
                        // Order iD
                        binding?.searchLayout?.searchET?.inputType = InputType.TYPE_CLASS_NUMBER
                        binding?.searchLayout?.searchET?.text?.clear()
                    }
                    2 -> {
                        // Deal Name
                        binding?.searchLayout?.searchET?.inputType = InputType.TYPE_CLASS_TEXT
                        binding?.searchLayout?.searchET?.text?.clear()
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding?.searchLayout?.searchET?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding?.searchLayout?.searchBtn?.performClick()
                return@OnEditorActionListener true
            }
            false
        })

        binding?.searchLayout?.searchET?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }

        binding?.searchLayout?.searchBtn?.setOnClickListener {
            searchData()
        }

        binding?.searchLayout?.searchKey?.setOnClickListener {
            clearFilter()
            fetchOrderManagementPendingList(0)
            if (binding?.searchLayout?.dateRangeKey?.visibility == View.GONE) {
                binding?.searchLayout?.chipsGroup?.visibility = View.GONE
            }
            binding?.searchLayout?.searchKey?.text = ""
            binding?.searchLayout?.searchKey?.visibility = View.GONE
        }

        binding?.searchLayout?.searchKey?.setOnCloseIconClickListener {
            binding?.searchLayout?.searchKey?.performClick()
        }
    }

    private fun searchData() {

        val searchText: String = binding?.searchLayout?.searchET?.text.toString() ?: ""
        if (searchText.isEmpty()) {
            context?.toast("কিছু লিখে সার্চ করুন")
            return
        }
        clearFilter()
        val filterType: Int = binding?.searchLayout?.filterTypeSpinner?.selectedItemPosition ?: 0
        when (filterType) {
            // Booking code
            0 -> {
                try {
                    orderId = searchText.toInt()
                    fetchOrderManagementPendingList(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    context?.toast("বুকিং কোড লিখে সার্চ করুন")
                }
            }
            // Deal Name
            1 -> {
                try {
                    productName = searchText
                    fetchOrderManagementPendingList(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    context?.toast("অর্ডার আইডি লিখে সার্চ করুন")
                }
            }
            // Deal Code
            2 -> {
                try {
                    productId = searchText.toInt()
                    fetchOrderManagementPendingList(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    context?.toast("ডিল কোড লিখে সার্চ করুন")
                }
            }
            // Phone Number
            3 -> {
                try {
                    phoneNumber = searchText
                    fetchOrderManagementPendingList(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    context?.toast("ফোন নম্বর লিখে সার্চ করুন")
                }
            }
        }
        binding?.searchLayout?.chipsGroup?.visibility = View.VISIBLE
        binding?.searchLayout?.searchKey?.text = searchText
        binding?.searchLayout?.searchKey?.visibility = View.VISIBLE

    }

    private fun clearFilter() {
        /*dealId = -1
        couponId = 0
        dealTitle = "-1"*/
        currentTotalCount = 0
        binding?.searchLayout?.searchET?.text?.clear()

        binding?.searchLayout?.countTV?.text = ""
        binding?.searchLayout?.countTV?.visibility = View.GONE
        mFilterTagList.clear()
        mFilterAdapter.notifyDataSetChanged()
        binding?.filterRecyclerView?.visibility = View.GONE
        binding?.searchLayout?.filterClear?.visibility = View.GONE
        binding?.searchLayout?.searchET?.text?.clear()


        // pending Order Management
        orderId = 0
        productId = 0
        phoneNumber = ""
        productName = ""

        // processing Order Management
        mDateFieldType = 2
        mFromDate = ""
        mToDate = ""
        mOrderStatus = "-1"
        mDealId = "-1"
        mBookingCode = "-1"
        mDealTitle = "-1"
        mMobile = "-1"
        mDeliveryDistrict = "-1"
        mBusinessModel = "-1"
        //mFilterType = 1;

    }

    private fun clearSearch() {
        fetchOrderManagementPendingList(0)

        if (binding?.searchLayout?.dateRangeKey?.visibility == View.GONE) {
            binding?.searchLayout?.chipsGroup?.visibility = View.GONE
        }
        binding?.searchLayout?.searchKey?.text = ""
        binding?.searchLayout?.searchKey?.visibility = View.GONE
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}