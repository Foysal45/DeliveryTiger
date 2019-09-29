package com.bd.deliverytiger.app.ui.cod_collection


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.CODCollectionInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.cod_collection.CODResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
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
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var codCollectionAdapter: CODCollectionAdapter
    private lateinit var codCollectionInterface: CODCollectionInterface
    private lateinit var codProgressBar: ProgressBar
    private var isLoading = false
    private var totalLoadedData = 0
    private var layoutPosition = 0
    private var totalCount = 0
    private var courierOrderViewModelList: ArrayList<CourierOrderViewModel?>? = null
    private var fromDate = "01-01-01"
    private var toDate = "01-01-01"
    private var status = -1
    private var isMoreDataAvailable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_codcollection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).setToolbarTitle("COD Collection")
        rvCODCollection = view.findViewById(R.id.rvCODCollection)
        codProgressBar = view.findViewById(R.id.codProgressBar)

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
                    "layoutPosition", layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount
                )
                if (dy > 0) {
                    if (layoutPosition >= (totalLoadedData - 2) && !isLoading && layoutPosition < totalCount && isMoreDataAvailable) {
                        getAllCODCollection(totalLoadedData, 20)
                        Timber.e("layoutPosition loadMoreCalled ",
                            layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount
                        )
                    }
                }
            }
        })


    }

    private fun manageAdapter() {
        codCollectionAdapter = CODCollectionAdapter(context!!, courierOrderViewModelList)
        rvCODCollection.apply {
            layoutManager = linearLayoutManager
            adapter = codCollectionAdapter
        }

        codCollectionAdapter.onItemClick = { position ->

            addOrderTrackFragment()
        }
    }


    private fun getAllCODCollection(index: Int, count: Int) {
        isLoading = true
        codProgressBar.visibility = View.VISIBLE
        val reqModel = CODReqBody(
            status, ArrayList(), fromDate, toDate, SessionManager.courierUserId,
            "", "", index, count
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
                        totalCount = response.body()!!.model.totalCount!!.toInt()
                        codCollectionAdapter.notifyDataSetChanged()
                        isMoreDataAvailable = response.body()!!.model.courierOrderViewModel!!.size >= count-2
                        Timber.e("getAllCODCollectionResponse", " s " + response.body().toString())
                    } else {
                        Timber.e("getAllCODCollectionResponse", " s null")
                    }
                }

            })
    }

    private fun addOrderTrackFragment() {
        val fragment = OrderTrackingFragment.newInstance(0)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, OrderTrackingFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

}
