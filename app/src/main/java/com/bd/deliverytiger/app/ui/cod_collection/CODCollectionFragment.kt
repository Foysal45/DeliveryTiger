package com.bd.deliverytiger.app.ui.cod_collection


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.CODCollectionInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.cod_collection.CODResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.ui.filter.FilterFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
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
class CODCollectionFragment : Fragment() {

    companion object {
        fun newInstance(): CODCollectionFragment {
            val fragment = CODCollectionFragment()
            return fragment
        }

        val tag = CODCollectionFragment::class.java.name
    }

    private lateinit var rvCODCollection: RecyclerView
    private lateinit var tvTotalOrder: TextView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var codCollectionAdapter: CODCollectionAdapter
    private lateinit var codCollectionInterface: CODCollectionInterface
    private lateinit var codProgressBar: ProgressBar
    private lateinit var filterLayout: LinearLayout
    private lateinit var viewID: ConstraintLayout
    private lateinit var ivEmpty: ImageView
    private lateinit var filterGroup: ChipGroup
    private lateinit var filterDateTag: Chip
    private lateinit var filterStatusTag: Chip
    private lateinit var filterSearchKeyTag: Chip

    private var isLoading = false
    private var totalLoadedData = 0
    private var layoutPosition = 0
    private var totalCount = 0
    private var courierOrderViewModelList: ArrayList<CourierOrderViewModel?>? = null
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
        return inflater.inflate(R.layout.fragment_codcollection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCODCollection = view.findViewById(R.id.rvCODCollection)
        codProgressBar = view.findViewById(R.id.codProgressBar)
        filterLayout = view.findViewById(R.id.allOrderFilterLay)
        tvTotalOrder = view.findViewById(R.id.tvTotalOrder)
        viewID = view.findViewById(R.id.viewID)
        ivEmpty = view.findViewById(R.id.ivEmpty)
        filterGroup = view.findViewById(R.id.filter_tag_group)
        filterDateTag = view.findViewById(R.id.filter_tag_date)
        filterStatusTag = view.findViewById(R.id.filter_tag_status)
        filterSearchKeyTag = view.findViewById(R.id.filter_tag_searchKey)

        courierOrderViewModelList = ArrayList()
        // fromDate = getCurrentDateTime().toString()
        // toDate = getPreviousDateTime(-1).toString()

        Timber.e("CurrentDateTime", fromDate + "@" + toDate)

        codCollectionInterface =
            RetrofitSingleton.getInstance(context!!).create(CODCollectionInterface::class.java)

        linearLayoutManager = LinearLayoutManager(context)
        manageAdapter()


        getAllCODCollection(0, 20)

        rvCODCollection.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutPosition = linearLayoutManager.findLastVisibleItemPosition()
                Timber.e(
                    "layoutPosition",
                    layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount
                )
                if (dy > 0) {
                    if (layoutPosition >= (totalLoadedData - 2) && !isLoading && layoutPosition < totalCount && isMoreDataAvailable) {
                        getAllCODCollection(totalLoadedData, 20)
                        Timber.e(
                            "layoutPosition loadMoreCalled ",
                            layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount
                        )
                    }
                }
            }
        })

        filterLayout.setOnClickListener {
            goToFilter()
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("COD কালেকশন")
    }

    private fun manageAdapter() {
        codCollectionAdapter = CODCollectionAdapter(context!!, courierOrderViewModelList)
        rvCODCollection.apply {
            layoutManager = linearLayoutManager
            adapter = codCollectionAdapter
            addItemDecoration(
                DividerItemDecoration(
                    rvCODCollection.getContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        codCollectionAdapter.onItemClick = { position ->

            addOrderTrackFragment(courierOrderViewModelList?.get(position)?.courierOrdersId.toString())
        }
    }

    private fun getAllCODCollection(index: Int, count: Int) {
        isLoading = true
        codProgressBar.visibility = View.VISIBLE
        val reqModel = CODReqBody(
            status, statusList, statusGroupList, fromDate, toDate, SessionManager.courierUserId,
            "", orderId, collectionName, mobileNumber, index, count
        )  // text model

        Timber.e("getAllCODCollectionReq", reqModel.toString())

        codCollectionInterface.getAllCODCollection(reqModel)
            .enqueue(object : Callback<GenericResponse<CODResponse>> {
                override fun onFailure(
                    call: Call<GenericResponse<CODResponse>>,
                    t: Throwable
                ) {
                    isLoading = false
                    Timber.e("getAllCODCollectionResponse", " f " + t.toString())
                    if (codProgressBar.visibility == View.VISIBLE) {
                        codProgressBar.visibility = View.GONE
                    }

                }

                override fun onResponse(
                    call: Call<GenericResponse<CODResponse>>,
                    response: Response<GenericResponse<CODResponse>>
                ) {
                    if (codProgressBar.visibility == View.VISIBLE) {
                        codProgressBar.visibility = View.GONE
                    }
                    isLoading = false
                    if (response.isSuccessful && response.body() != null && response.body()!!.model != null) {
                        courierOrderViewModelList?.addAll(response.body()!!.model.courierOrderViewModel!!)
                        totalLoadedData = courierOrderViewModelList!!.size

                        codCollectionAdapter.notifyDataSetChanged()
                        isMoreDataAvailable =
                            response.body()!!.model.courierOrderViewModel!!.size >= count - 2
                        Timber.e("getAllCODCollectionResponse", " s " + response.body().toString())

                        if (index < 20) {
                            totalCount = response.body()!!.model.totalCount!!.toInt()
                            val msg = "মোট অর্ডারঃ <font color='#CC000000'><b>${DigitConverter.toBanglaDigit(totalCount)}</b></font> টি"
                            tvTotalOrder.text = HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }

                        if(totalLoadedData == 0){
                            viewID.visibility = View.GONE
                            ivEmpty.visibility = View.VISIBLE
                        } else {
                            viewID.visibility = View.VISIBLE
                            ivEmpty.visibility = View.GONE
                        }
                    } else {
                        Timber.e("getAllCODCollectionResponse", " s null")
                    }
                }

            })
    }

    private fun addOrderTrackFragment(orderID: String) {
        val fragment = OrderTrackingFragment.newInstance(orderID)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, OrderTrackingFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    private fun goToFilter() {

        activity?.let {
            (activity as HomeActivity).openRightDrawer()
        }

        val fragment = FilterFragment.newInstance(fromDate, toDate, status, statusGroup, searchKeys)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container_drawer, fragment, FilterFragment.tag)
        //ft?.addToBackStack(FilterFragment.tag)
        ft?.commit()

        fragment.setFilterListener(object : FilterFragment.FilterListener {
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

                    courierOrderViewModelList?.clear()
                    codCollectionAdapter.notifyDataSetChanged()
                    getAllCODCollection(0, 20)
                }

                filterStatusTag.setOnClickListener {
                    filterStatusTag.text = ""
                    filterStatusTag.visibility = View.GONE
                    status = -1
                    statusGroup = "-1"
                    statusGroupList.clear()
                    statusGroupList.add(statusGroup)

                    courierOrderViewModelList?.clear()
                    codCollectionAdapter.notifyDataSetChanged()
                    getAllCODCollection(0, 20)
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

                    courierOrderViewModelList?.clear()
                    codCollectionAdapter.notifyDataSetChanged()
                    getAllCODCollection(0, 20)
                }

                filterSearchKeyTag.setOnCloseIconClickListener {
                    filterSearchKeyTag.performClick()
                }

                courierOrderViewModelList?.clear()
                codCollectionAdapter.notifyDataSetChanged()
                getAllCODCollection(0, 20)

                activity?.onBackPressed()
            }
        })
    }

}
