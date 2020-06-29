package com.bd.deliverytiger.app.ui.billing_of_service


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.BillingServiceInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceMainResponse
import com.bd.deliverytiger.app.api.model.billing_service.BillingServiceReqBody
import com.bd.deliverytiger.app.api.model.billing_service.CourierOrderAmountDetail
import com.bd.deliverytiger.app.ui.filter.FilterFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.AppConstant
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class BillingofServiceFragment : Fragment() {

    companion object {
        fun newInstance(): BillingofServiceFragment {
            val fragment = BillingofServiceFragment()
            return fragment
        }

        val tag = BillingofServiceFragment::class.java.name
    }

    private lateinit var rvBillingService: RecyclerView
    private lateinit var tvTotalOrder: TextView
    private lateinit var billingFilterLay: LinearLayout
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var ivEmpty: ImageView
    //private lateinit var topLay: LinearLayout
    private lateinit var billingProgressBar: ProgressBar
    private lateinit var filterGroup: ChipGroup
    private lateinit var filterDateTag: Chip
    private lateinit var filterStatusTag: Chip
    private lateinit var filterSearchKeyTag: Chip

    private lateinit var billingServiceAdapter: BillingServiceAdapter
    private lateinit var billingServiceInterface: BillingServiceInterface

    private var isLoading = false
    private var totalLoadedData = 0
    private var layoutPosition = 0
    private var totalCount = 0
    private var courierOrderAmountDetailList: ArrayList<CourierOrderAmountDetail?>? = null
    private var defaultDate = "2001-01-01"
    private var fromDate = "2001-01-01"
    private var toDate = "2001-01-01"
    private var status = -1
    private var statusGroup = "-1"
    private var orderId = ""
    private var mobileNumber = ""
    private var collectionName = ""
    private var searchKeys = ""
    private var searchTypes = 0
    private var isMoreDataAvailable = true
    private val statusList: MutableList<Int> = mutableListOf(-1)
    private val statusGroupList: MutableList<String> = mutableListOf("-1")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_billingof, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvBillingService = view.findViewById(R.id.rvBillingService)
        billingProgressBar = view.findViewById(R.id.billingProgressBar)
        tvTotalOrder = view.findViewById(R.id.tvTotalOrder)
        billingFilterLay = view.findViewById(R.id.allOrderFilterLay)
        ivEmpty = view.findViewById(R.id.ivEmpty)
        //topLay = view.findViewById(R.id.topLay)
        filterGroup = view.findViewById(R.id.filter_tag_group)
        filterDateTag = view.findViewById(R.id.filter_tag_date)
        filterStatusTag = view.findViewById(R.id.filter_tag_status)
        filterSearchKeyTag = view.findViewById(R.id.filter_tag_searchKey)

        billingServiceInterface =
            RetrofitSingleton.getInstance(context!!).create(BillingServiceInterface::class.java)

        courierOrderAmountDetailList = ArrayList()
        linearLayoutManager = LinearLayoutManager(context)

        billingServiceAdapter = BillingServiceAdapter(context!!, courierOrderAmountDetailList)
        rvBillingService.apply {
            layoutManager = linearLayoutManager
            adapter = billingServiceAdapter
            //addItemDecoration(DividerItemDecoration(rvBillingService.getContext(), DividerItemDecoration.VERTICAL))
        }

        billingServiceAdapter.onItemClick = { position ->
            addOrderTrackFragment(courierOrderAmountDetailList!![position]?.courierOrdersId.toString())
        }

        billingServiceAdapter.onPaymentClick = { position ->
            val model = courierOrderAmountDetailList!![position]
            model?.courierOrdersId?.let {
                paymentGateway(it)
            }
        }



        rvBillingService.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutPosition = linearLayoutManager.findLastVisibleItemPosition()
                Timber.e(
                    "layoutPosition",
                    layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount + " " + isMoreDataAvailable
                )
                if (dy > 0) {
                    if (layoutPosition >= (totalLoadedData - 2) && !isLoading && layoutPosition < totalCount && isMoreDataAvailable) {
                        getBillingAddress(totalLoadedData, 20)
                        Timber.e(
                            "layoutPosition loadMoreCalled ",
                            layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount
                        )
                    }
                }
            }
        })

        billingFilterLay.setOnClickListener {
            goToFilter()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("সার্ভিসের বিল")
        courierOrderAmountDetailList?.clear()
        billingServiceAdapter.notifyDataSetChanged()
        getBillingAddress(0, 20)
    }

    private fun getBillingAddress(index: Int, count: Int) {

        isLoading = true
        // billingProgressBar.visibility = View.VISIBLE
        val reqModel = BillingServiceReqBody(
            status, statusList, statusGroupList, fromDate, toDate, SessionManager.courierUserId,
            "", orderId,collectionName,mobileNumber, index, count
        )  // text model

        Timber.e("getAllBillingServiceReq", reqModel.toString())

        billingServiceInterface.getAllBillingService(reqModel)
            .enqueue(object : Callback<GenericResponse<BillingServiceMainResponse>> {
                override fun onFailure(
                    call: Call<GenericResponse<BillingServiceMainResponse>>,
                    t: Throwable
                ) {
                    Timber.e("getAllBillingServiceResponse", " f " + t.toString())
                    if (billingProgressBar.visibility == View.VISIBLE) {
                        billingProgressBar.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<GenericResponse<BillingServiceMainResponse>>,
                    response: Response<GenericResponse<BillingServiceMainResponse>>
                ) {
                    if (billingProgressBar.visibility == View.VISIBLE) {
                        billingProgressBar.visibility = View.GONE
                    }
                    isLoading = false
                    if (response.isSuccessful && response.body() != null && response.body()!!.model != null) {
                        Timber.e(
                            "getAllBillingServiceResponse",
                            " s " + response.body()!!.model.courierOrderAmountDetails
                        )
                        courierOrderAmountDetailList?.addAll(response.body()!!.model.courierOrderAmountDetails)
                        totalLoadedData = courierOrderAmountDetailList!!.size

                        billingServiceAdapter.notifyDataSetChanged()
                        isMoreDataAvailable =
                            response.body()!!.model.courierOrderAmountDetails!!.size >= count - 2
                        Timber.e("getAllBillingServiceResponse", " s " + response.body().toString())

                        if (index < 20) {
                            totalCount = response.body()!!.model.totalDataCount!!.toInt()
                            val msg = "মোট অর্ডারঃ <font color='#CC000000'><b>${DigitConverter.toBanglaDigit(totalCount)}</b></font> টি"
                            tvTotalOrder.text = HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }

                        if(totalLoadedData == 0){
                            //topLay.visibility = View.GONE
                            ivEmpty.visibility = View.VISIBLE
                        } else {
                            //topLay.visibility = View.VISIBLE
                            ivEmpty.visibility = View.GONE
                        }

                    } else {
                        Timber.e("getAllBillingServiceResponse", " s null")
                    }
                }
            })

    }

    private fun addOrderTrackFragment(orderId: String) {
        val fragment = OrderTrackingFragment.newInstance(orderId)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, OrderTrackingFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    private fun paymentGateway(orderId: String) {

        val url = "${AppConstant.GATEWAY}?CID=$orderId"
        val fragment = WebViewFragment.newInstance(url, "পেমেন্ট")
        val tag = WebViewFragment.tag

        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    private fun goToFilter(){

        activity?.let {
            (activity as HomeActivity).openRightDrawer()
        }

        val fragment = FilterFragment.newInstance(fromDate,toDate,status,statusGroup, searchKeys)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container_drawer, fragment, FilterFragment.tag)
        //ft?.addToBackStack(FilterFragment.tag)
        ft?.commit()

        fragment.setFilterListener(object : FilterFragment.FilterListener{
            override fun selectedDate(fromDate1: String, toDate1: String, status1: Int, statusGroup1: String, searchKey: String, searchType: Int) {
                fromDate = fromDate1
                toDate = toDate1
                status = status1
                statusGroup = statusGroup1
                statusGroupList.clear()
                statusGroupList.add(statusGroup1)

                searchKeys = searchKey
                searchTypes = searchType

                if (searchType == 0){
                    mobileNumber = ""
                    collectionName = ""
                    orderId = ""
                }

                when(searchType){
                    1 -> {
                        mobileNumber = searchKey
                    }
                    2 -> {
                        orderId = searchKey
                    }
                    3 -> {
                        collectionName = searchKey
                    }
                }

                if (fromDate1 != defaultDate){
                    val msg = "${DigitConverter.toBanglaDate(fromDate1, "yyyy-MM-dd")} - ${DigitConverter.toBanglaDate(toDate1, "yyyy-MM-dd")}"
                    filterDateTag.text = msg
                    filterDateTag.visibility = View.VISIBLE
                } else {
                    filterDateTag.text = ""
                    filterDateTag.visibility = View.GONE
                    fromDate = defaultDate
                    toDate = defaultDate
                }

                if (statusGroup != "-1"){
                    filterStatusTag.text = statusGroup1
                    filterStatusTag.visibility = View.VISIBLE
                } else {
                    filterStatusTag.text = ""
                    filterStatusTag.visibility = View.GONE
                    status = -1
                    statusGroup = "-1"
                    statusGroupList.clear()
                    statusGroupList.add(statusGroup)
                }

                if (searchTypes != 0) {
                    filterSearchKeyTag.text = searchKeys
                    filterSearchKeyTag.visibility = View.VISIBLE
                } else {
                    filterSearchKeyTag.text = ""
                    filterSearchKeyTag.visibility = View.GONE

                }

                filterDateTag.setOnClickListener {
                    filterDateTag.text = ""
                    filterDateTag.visibility = View.GONE
                    fromDate = defaultDate
                    toDate = defaultDate

                    courierOrderAmountDetailList?.clear()
                    billingServiceAdapter.notifyDataSetChanged()
                    getBillingAddress(0, 20)
                }

                filterStatusTag.setOnClickListener {
                    filterStatusTag.text = ""
                    filterStatusTag.visibility = View.GONE
                    status = -1
                    statusGroup = "-1"
                    statusGroupList.clear()
                    statusGroupList.add(statusGroup)

                    courierOrderAmountDetailList?.clear()
                    billingServiceAdapter.notifyDataSetChanged()
                    getBillingAddress(0, 20)
                }

                filterDateTag.setOnCloseIconClickListener {
                    filterDateTag.performClick()
                }

                filterStatusTag.setOnCloseIconClickListener {
                    filterStatusTag.performClick()
                }

                filterSearchKeyTag.setOnClickListener {
                    filterSearchKeyTag.text = ""
                    filterSearchKeyTag.visibility = View.GONE
                    fromDate = defaultDate
                    toDate = defaultDate
                    status = -1
                    statusGroup = "-1"
                    statusGroupList.clear()
                    statusGroupList.add(statusGroup)
                    mobileNumber = ""
                    collectionName = ""
                    orderId = ""
                    searchKeys = ""
                    searchTypes = 0

                    courierOrderAmountDetailList?.clear()
                    billingServiceAdapter.notifyDataSetChanged()
                    getBillingAddress(0,20)
                }

                filterSearchKeyTag.setOnCloseIconClickListener {
                    filterSearchKeyTag.performClick()
                }

                courierOrderAmountDetailList?.clear()
                billingServiceAdapter.notifyDataSetChanged()
                getBillingAddress(0,20)

                activity?.onBackPressed()
            }
        })
    }


}
