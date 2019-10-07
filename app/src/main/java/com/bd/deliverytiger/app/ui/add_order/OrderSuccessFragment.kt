package com.bd.deliverytiger.app.ui.add_order


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction

import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.api.model.order.OrderRequest
import com.bd.deliverytiger.app.api.model.order.OrderResponse
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.VariousTask

/**
 * A simple [Fragment] subclass.
 */
class OrderSuccessFragment : Fragment() {

    companion object {
        fun newInstance(orderResponse: OrderResponse?): OrderSuccessFragment {
            val fragment = OrderSuccessFragment()
            fragment.orderResponse = orderResponse
            return fragment
        }

        val tag = AddOrderFragmentOne::class.java.name
    }

    private var orderResponse: OrderResponse? = null
    private lateinit var tvSuccessOrderId: TextView
    private lateinit var tvSuccessOrderTitle: TextView
    private lateinit var tvSuccessOrderAddress: TextView
    private lateinit var orderListClickedLay: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSuccessOrderId = view.findViewById(R.id.tvSuccessOrderId)
        tvSuccessOrderTitle = view.findViewById(R.id.tvSuccessOrderTitle)
        tvSuccessOrderAddress = view.findViewById(R.id.tvSuccessOrderAddress)
        orderListClickedLay = view.findViewById(R.id.orderListClickedLay)

        VariousTask.hideSoftKeyBoard(activity)

        if(orderResponse != null){
            tvSuccessOrderId.text ="# ${orderResponse!!.courierOrdersId}"
            tvSuccessOrderTitle.text =orderResponse!!.collectionName
            tvSuccessOrderAddress.text = getAddress(orderResponse)
        }
        orderListClickedLay.setOnClickListener {
            allOrderListFragment()
        }
    }

    private fun allOrderListFragment() {
       // activity?.supportFragmentManager?.popBackStack()
        val fragment = AllOrdersFragment.newInstance()
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
       // ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    private fun getAddress(orderResponse: OrderResponse?): String {
        var mAddress: String = orderResponse?.customerName + "\n" +
                orderResponse?.mobile + "," +
                orderResponse?.otherMobile + "\n" +
                orderResponse?.address /*+ "," +
                orderResponse?.areaId + "," +
                orderResponse?.thanaName + "," +
                orderResponse?.districtName*/

        return mAddress
    }


}
