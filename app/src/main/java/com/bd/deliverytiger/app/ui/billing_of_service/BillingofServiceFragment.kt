package com.bd.deliverytiger.app.ui.billing_of_service


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
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
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
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
    private lateinit var billingServiceAdapter: BillingServiceAdapter
    private lateinit var billingServiceInterface: BillingServiceInterface

    private lateinit var billingProgressBar: ProgressBar
    private var isLoading = false
    private var totalLoadedData = 0
    private var layoutPosition = 0
    private var totalCount = 0
    private var courierOrderAmountDetailList: ArrayList<CourierOrderAmountDetail?>? = null
    private var fromDate = "01-01-01"
    private var toDate = "01-01-01"
    private var status = -1
    private var isMoreDataAvailable = true

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
        billingFilterLay = view.findViewById(R.id.billingFilterLay)

        billingServiceInterface =
            RetrofitSingleton.getInstance(context!!).create(BillingServiceInterface::class.java)

        courierOrderAmountDetailList = ArrayList()
        linearLayoutManager = LinearLayoutManager(context)

        billingServiceAdapter = BillingServiceAdapter(context!!, courierOrderAmountDetailList)
        rvBillingService.apply {
            layoutManager = linearLayoutManager
            adapter = billingServiceAdapter
            addItemDecoration(DividerItemDecoration(rvBillingService.getContext(), DividerItemDecoration.VERTICAL))
        }

        billingServiceAdapter.onItemClick = { position ->
            addOrderTrackFragment(courierOrderAmountDetailList!![position]?.courierOrdersId.toString())
        }

        getBillingAddress(0, 20)

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
    }

    private fun getBillingAddress(index: Int, count: Int) {

        isLoading = true
        // billingProgressBar.visibility = View.VISIBLE
        val reqModel = BillingServiceReqBody(
            status, ArrayList(), fromDate, toDate, SessionManager.courierUserId,
            "", "", index, count
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
                            tvTotalOrder.text ="মোট অর্ডার: ${DigitConverter.toBanglaDigit(totalCount)} টি"
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
    private fun goToFilter(){

        activity?.let {
            (activity as HomeActivity).openRightDrawer()
        }

        val fragment = FilterFragment.newInstance(fromDate,toDate,status)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container_drawer, fragment, FilterFragment.tag)
        //ft?.addToBackStack(FilterFragment.tag)
        ft?.commit()

        fragment.setFilterListener(object : FilterFragment.FilterListener{
            override fun selectedDate(fromDate1: String, toDate1: String, status1: Int) {
                fromDate = fromDate1
                toDate = toDate1
                status = status1


                courierOrderAmountDetailList?.clear()
                billingServiceAdapter.notifyDataSetChanged()
                getBillingAddress(0,20)

                activity?.onBackPressed()
            }
        })
    }


}
