package com.bd.deliverytiger.app.ui.all_orders


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.AllOrderInterface
import com.bd.deliverytiger.app.api.model.billing_service.CourierOrderAmountDetail
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.Timber

/**
 * A simple [Fragment] subclass.
 */
class AllOrdersFragment : Fragment() {

    companion object {
        fun newInstance(): AllOrdersFragment {
            val fragment = AllOrdersFragment()
            return fragment
        }

        val tag = AllOrdersFragment::class.java.name
    }

    private lateinit var rvAllOrder: RecyclerView
    private lateinit var tvTotalOrder: TextView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var allOrdersAdapter: AllOrdersAdapter
    private lateinit var allOrderInterface: AllOrderInterface

    private lateinit var allOrderProgressBar: ProgressBar
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
        return inflater.inflate(R.layout.fragment_all_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).setToolbarTitle("সব অর্ডার")
        rvAllOrder = view.findViewById(R.id.rvAllOrder)
        allOrderProgressBar = view.findViewById(R.id.allOrderProgressBar)
        tvTotalOrder = view.findViewById(R.id.tvTotalOrder)

        allOrderInterface =
            RetrofitSingleton.getInstance(context!!).create(AllOrderInterface::class.java)

        courierOrderAmountDetailList = ArrayList()
        linearLayoutManager = LinearLayoutManager(context)

        allOrdersAdapter = AllOrdersAdapter(context!!, courierOrderAmountDetailList)
        rvAllOrder.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(rvAllOrder.getContext(), DividerItemDecoration.VERTICAL))
            adapter = allOrdersAdapter
        }

        allOrdersAdapter.onItemClick = { position ->
            //addOrderTrackFragment(courierOrderAmountDetailList!![position]?.courierOrdersId.toString())
        }

        //getBillingAddress(0, 20)

        rvAllOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutPosition = linearLayoutManager.findLastVisibleItemPosition()
                Timber.e(
                    "layoutPosition",
                    layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount + " " + isMoreDataAvailable
                )
                if (dy > 0) {
                    if (layoutPosition >= (totalLoadedData - 2) && !isLoading && layoutPosition < totalCount && isMoreDataAvailable) {
                        //getBillingAddress(totalLoadedData, 20)
                        Timber.e(
                            "layoutPosition loadMoreCalled ",
                            layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount
                        )
                    }
                }
            }
        })
    }

    /*  private fun getAllOrders(index: Int, count: Int) {

          isLoading = true
          // billingProgressBar.visibility = View.VISIBLE
          val reqModel = BillingServiceReqBody(
              status, ArrayList(), fromDate, toDate, SessionManager.courierUserId,
              "", "", index, count
          )  // text model

          Timber.e("getAllBillingServiceReq", reqModel.toString())

          allOrderInterface.getAllOrder(reqModel)
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

                          allOrdersAdapter.notifyDataSetChanged()
                          isMoreDataAvailable =
                              response.body()!!.model.courierOrderAmountDetails!!.size >= count - 2
                          Timber.e("getAllBillingServiceResponse", " s " + response.body().toString())

                          if (index < 20) {
                              totalCount = response.body()!!.model.totalDataCount!!.toInt()
                              tvTotalOrder.text ="মোট অর্ডার : " + DigitConverter.toBanglaDigit(totalCount)
                          }

                      } else {
                          Timber.e("getAllBillingServiceResponse", " s null")
                      }
                  }
              })

      }*/


}
