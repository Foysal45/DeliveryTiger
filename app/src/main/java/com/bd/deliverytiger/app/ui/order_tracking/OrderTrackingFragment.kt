package com.bd.deliverytiger.app.ui.order_tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.OrderTrackInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackReqBody
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.VariousTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class OrderTrackingFragment : Fragment() {

    companion object {
        fun newInstance(orderID: String): OrderTrackingFragment {
            val fragment = OrderTrackingFragment()
            fragment.orderID = orderID
            return fragment
        }

        val tag = OrderTrackingFragment::class.java.name
    }

    private var orderID = ""
    private lateinit var rvOrderTrack: RecyclerView
    private lateinit var rvOrderTrackProgress: ProgressBar
    private lateinit var orderTrackClickedLay: LinearLayout
    private lateinit var etOrderTrackId: EditText

    private lateinit var mLinerLayoutManager: LinearLayoutManager
    private lateinit var orderTrackingAdapter: OrderTrackingAdapter
    private lateinit var orderTrackInterface: OrderTrackInterface
    private lateinit var orderTrackStatusList: ArrayList<OrderTrackMainResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).setToolbarTitle("অর্ডার ট্র্যাকিং")
        rvOrderTrack = view.findViewById(R.id.rvOrderTrack)
        rvOrderTrackProgress = view.findViewById(R.id.rvOrderTrackProgress)
        orderTrackClickedLay = view.findViewById(R.id.orderTrackClickedLay)
        etOrderTrackId = view.findViewById(R.id.etOrderTrackId)

        orderTrackInterface =
            RetrofitSingleton.getInstance(context!!).create(OrderTrackInterface::class.java)

        orderTrackStatusList = ArrayList()
        mLinerLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        orderTrackingAdapter = OrderTrackingAdapter(context!!, orderTrackStatusList)

        orderTrackingAdapter.onItemClick = { position ->
            OrderTrackingBottomSheet.newInstance(orderTrackStatusList[position]).show(childFragmentManager,OrderTrackingBottomSheet.tag)
        }

        rvOrderTrack.apply {
            layoutManager = mLinerLayoutManager
            adapter = orderTrackingAdapter
        }

        orderTrackClickedLay.setOnClickListener {
            if (etOrderTrackId.text.toString().isNotEmpty()) {
                if (etOrderTrackId.text.toString() != orderID) {
                    orderID = etOrderTrackId.text.toString().trim()
                    getOrderTrackingList(orderID, "private")
                }
            } else {
                etOrderTrackId.requestFocus()
                VariousTask.showShortToast(context, getString(R.string.give_order_id))
            }
        }

        if (orderID.isNotEmpty()) {
            getOrderTrackingList(orderID, "private")
            etOrderTrackId.setText(orderID)
        }

    }

    private fun getOrderTrackingList(orderId: String, flag: String) {
        VariousTask.hideSoftKeyBoard(activity!!)
        rvOrderTrackProgress.visibility = View.VISIBLE
        val orderTrackReqBody = OrderTrackReqBody(orderId)
        orderTrackInterface.getOrderTrackingList(flag, orderTrackReqBody)
            .enqueue(object : Callback<GenericResponse<List<OrderTrackMainResponse>>> {
                override fun onFailure(
                    call: Call<GenericResponse<List<OrderTrackMainResponse>>>,
                    t: Throwable
                ) {
                    Timber.e("getOrderTrackingList f ", t.toString())
                    rvOrderTrackProgress.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<GenericResponse<List<OrderTrackMainResponse>>>,
                    response: Response<GenericResponse<List<OrderTrackMainResponse>>>
                ) {
                    rvOrderTrackProgress.visibility = View.GONE
                    rvOrderTrack.visibility = View.VISIBLE
                    if (response.isSuccessful && response.body() != null && response.body()!!.model.isNotEmpty()) {
                        orderTrackStatusList.clear()
                        orderTrackStatusList.addAll(response.body()!!.model)
                        orderTrackingAdapter.notifyDataSetChanged()
                        // rvOrderTrack.scrollToPosition(5)
                        mLinerLayoutManager.scrollToPositionWithOffset(
                            orderTrackStatusList.size - 1,
                            0
                        );
                        Timber.e("getOrderTrackingList s ", response.body()!!.toString())
                    } else {
                        VariousTask.showShortToast(context, getString(R.string.give_right_order_id))
                        Timber.e("getOrderTrackingList s ", "null")
                    }
                }

            })
    }


}
