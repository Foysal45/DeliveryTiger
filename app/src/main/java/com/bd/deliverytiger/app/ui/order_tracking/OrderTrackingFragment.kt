package com.bd.deliverytiger.app.ui.order_tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class OrderTrackingFragment : Fragment() {

    private lateinit var toolbarTracking: Toolbar
    private lateinit var backBtn: ImageView
    private lateinit var turnOff: ImageView
    private lateinit var toolbarTitle: TextView
    private lateinit var etOrderTrackId: EditText
    private lateinit var trackBtn: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var dataAdapter: OrderTrackingAdapter
    private lateinit var customerOrderAdapter: CustomerOrderAdapter

    private var orderID = ""

    private val viewModel: OrderTrackingViewModel by inject()

    companion object {
        fun newInstance(orderID: String): OrderTrackingFragment = OrderTrackingFragment().apply {
            this.orderID = orderID
        }
        val tag: String = OrderTrackingFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTracking = view.findViewById(R.id.toolbarTracking)
        toolbarTitle = view.findViewById(R.id.toolbarTitle)
        backBtn = view.findViewById(R.id.backBtn)
        turnOff = view.findViewById(R.id.turnOff)
        etOrderTrackId = view.findViewById(R.id.etOrderTrackId)
        trackBtn = view.findViewById(R.id.orderTrackClickedLay)
        progressBar = view.findViewById(R.id.rvOrderTrackProgress)
        recyclerView = view.findViewById(R.id.rvOrderTrack)

        dataAdapter = OrderTrackingAdapter()
        customerOrderAdapter = CustomerOrderAdapter()
        with(recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = dataAdapter
        }

        dataAdapter.onItemClick = { model, position ->
            val tag = OrderTrackingBottomSheet.tag
            val dialog = OrderTrackingBottomSheet.newInstance(model)
            dialog.show(childFragmentManager, tag)
        }
        customerOrderAdapter.onItemClick = { model, position ->
            orderID = model.courierOrdersId ?: ""
            recyclerView.adapter = dataAdapter
            getOrderTrackingList(orderID)
        }

        trackBtn.setOnClickListener {
            trackOrder()
        }

        if (orderID.isNotEmpty()) {
            getOrderTrackingList(orderID)
            etOrderTrackId.setText(orderID)
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
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        })

        // Test
        //etOrderTrackId.setText("01715269261") //DT-12222
    }

    override fun onResume() {
        super.onResume()
        if (activity is HomeActivity) {
            toolbarTracking.visibility = View.GONE
            (activity as HomeActivity).setToolbarTitle("অর্ডার ট্র্যাকিং")
        } else {
            toolbarTracking.visibility = View.VISIBLE
            toolbarTitle.text = "অর্ডার ট্র্যাকিং"
            backBtn.setOnClickListener {
                activity?.onBackPressed()
            }
            turnOff.setOnClickListener {
                activity?.moveTaskToBack(true)
                activity?.finish()
            }
        }
    }

    private fun trackOrder() {

        val searchKey = etOrderTrackId.text.toString()
        if (searchKey.trim().isEmpty()) {
            etOrderTrackId.requestFocus()
            context?.toast(getString(R.string.give_order_id))
            return
        }

        hideKeyboard()
        if (searchKey != orderID) {
            orderID = searchKey
            if (searchKey.length == 11 && searchKey.startsWith("01")) {
                recyclerView.adapter = customerOrderAdapter
                fetchCustomerOrder(searchKey)
            } else {
                recyclerView.adapter = dataAdapter
                getOrderTrackingList(orderID)
            }
        }
    }

    private fun getOrderTrackingList(orderId: String) {

        dataAdapter.clear()
        viewModel.fetchOrderTrackingList(orderId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                dataAdapter.initLoad(list.reversed())
            } else {
                context?.toast(getString(R.string.give_right_order_id))
            }
        })
    }

    private fun fetchCustomerOrder(mobileNumber: String) {

        customerOrderAdapter.clear()
        viewModel.fetchCustomerOrder(mobileNumber).observe(viewLifecycleOwner, Observer { model ->

            if (!model.courierOrderViewModel.isNullOrEmpty()) {
                customerOrderAdapter.initLoad(model.courierOrderViewModel!!)
            } else {
                context?.toast("সঠিক মোবাইল নম্বর দিয়ে সার্চ করুন")
            }

        })
    }


}
