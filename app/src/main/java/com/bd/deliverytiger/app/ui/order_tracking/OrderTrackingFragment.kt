package com.bd.deliverytiger.app.ui.order_tracking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.HubInfo
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackData
import com.bd.deliverytiger.app.databinding.FragmentOrderTrackingBinding
import com.bd.deliverytiger.app.ui.collector_tracking.MapFragment
import com.bd.deliverytiger.app.ui.complain.ComplainFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class OrderTrackingFragment : Fragment() {

    private var binding: FragmentOrderTrackingBinding? = null
    private val viewModel: OrderTrackingViewModel by inject()

    private lateinit var dataAdapter: OrderTrackingNewAdapter
    private lateinit var customerOrderAdapter: CustomerOrderAdapter

    private var orderID = ""
    private var containerType: String = ""

    companion object {
        fun newInstance(orderID: String, containerType: String = ""): OrderTrackingFragment = OrderTrackingFragment().apply {
            this.orderID = orderID
            this.containerType = containerType
        }
        val tag: String = OrderTrackingFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return inflater.inflate(R.layout.fragment_order_tracking, container, false)
        return FragmentOrderTrackingBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.merchantInfoLayout?.visibility = View.VISIBLE
        if (containerType == "login") {
            binding?.complainBtn?.visibility = View.GONE
            binding?.merchantInfoLayout?.visibility = View.GONE
        }

        dataAdapter = OrderTrackingNewAdapter()
        customerOrderAdapter = CustomerOrderAdapter()
        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = dataAdapter
        }
        /*dataAdapter.onItemClick = { model, position ->
            val tag = OrderTrackingBottomSheet.tag
            val dialog = OrderTrackingBottomSheet.newInstance(model)
            dialog.show(childFragmentManager, tag)
        }*/
        dataAdapter.onLocationClick = { model, position ->
            goToHubLocation(model)
        }
        dataAdapter.onCallPress = { model, position ->
            if (model.courierDeliveryMan?.courierDeliveryManMobile?.isNotEmpty() == true) {
                callNumber(model.courierDeliveryMan?.courierDeliveryManMobile!!)
            }
        }
        customerOrderAdapter.onItemClick = { model, position ->
            orderID = model.courierOrdersId ?: ""
            binding?.recyclerView?.adapter = dataAdapter
            getOrderTrackingList(orderID)
        }
        viewModel.fetchHelpLineNumbers().observe(viewLifecycleOwner, Observer { model->
            if (model.helpLine2.isNullOrEmpty()){
                binding?.helpLineContactLayout?.visibility = View.GONE
            }else{
                binding?.helpLineContactLayout?.visibility = View.VISIBLE
                binding?.helpLineNumber?.text = DigitConverter.toBanglaDigit(model.helpLine2)
                binding?.helpLineNumber?.setOnClickListener{
                    callHelplineNumber(model.helpLine2!!)
                }
            }
        })

        binding?.trackBtn?.setOnClickListener {
            Timber.d("trackBtn called")
            trackOrder()
        }
        binding?.merchantName?.text = "${SessionManager.companyName} (${SessionManager.courierUserId})"
        binding?.merchantMobile?.text = SessionManager.mobile

        if (orderID.isNotEmpty()) {
            getOrderTrackingList(orderID)
            binding?.orderIdET?.setText(orderID)
        }

        binding?.complainBtn?.setOnClickListener {
            val tag = ComplainFragment.tag
            val fragment = ComplainFragment.newInstance(orderID)
            addFragment(fragment, tag)
        }

        binding?.callBtn?.setOnClickListener {
            callHelpLine()
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

        // Test
        if (BuildConfig.DEBUG) {
            binding?.orderIdET?.setText("DT-357538") //DT-12222 01715269261 DT-314560
        }
    }

    private fun callNumber(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().toast("Could not find an activity to place the call")
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is HomeActivity) {
            binding?.toolbarTracking?.visibility = View.GONE
            (activity as HomeActivity).setToolbarTitle("অর্ডার ট্র্যাকিং")
        } else {
            binding?.toolbarTracking?.visibility = View.VISIBLE
            binding?.toolbarTitle?.text = "অর্ডার ট্র্যাকিং"
            binding?.backBtn?.setOnClickListener {
                activity?.onBackPressed()
            }
            binding?.turnOff?.setOnClickListener {
                activity?.moveTaskToBack(true)
                activity?.finish()
            }
        }
    }

    private fun trackOrder() {

        val searchKey = binding?.orderIdET?.text.toString()
        if (searchKey.trim().isEmpty()) {
            binding?.orderIdET?.requestFocus()
            context?.toast(getString(R.string.give_order_id))
            return
        }

        hideKeyboard()
        if (searchKey != orderID) {
            orderID = searchKey
            if (searchKey.length == 11 && searchKey.startsWith("01")) {
                binding?.recyclerView?.adapter = customerOrderAdapter
                fetchCustomerOrder(searchKey)
            } else {
                binding?.recyclerView?.adapter = dataAdapter
                getOrderTrackingList(orderID)
            }
        }
    }

    private fun getOrderTrackingList(orderId: String) {

        dataAdapter.clear()
        viewModel.fetchOrderTrackingList(orderId, SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->

            binding?.orderCode?.text = model.courierOrdersViewModel?.courierOrdersId
            binding?.reference?.text = model.courierOrdersViewModel?.collectionName
            if (model.orderTrackingGroupViewModel.isNotEmpty()) {

                val filteredShipmentList = model.orderTrackingGroupViewModel.filter { it.trackingFlag && it.trackingColor == "green" }
                val filteredReturnList = model.orderTrackingGroupViewModel.filter { it.trackingFlag && it.trackingColor == "red" }

                val shipmentStep = filteredShipmentList.size
                val returnStep = filteredReturnList.size
                var shipmentIndex = 0
                var returnIndex = 0

                model.orderTrackingGroupViewModel.forEach {
                    if (shipmentStep >= 2) {
                        if (it.trackingFlag && it.trackingColor == "green") {
                            if (shipmentStep == 2) {
                                if (shipmentIndex == 0) {
                                    it.trackState = 1 //top
                                } else if (shipmentIndex == 1) {
                                    it.trackState = 3 //bottom
                                }
                                it.trackStateCount = shipmentStep
                            } else {
                                if (shipmentIndex == 0) {
                                    it.trackState = 1 //top
                                } else if (shipmentIndex == (shipmentStep-1)) {
                                    it.trackState = 3 //bottom
                                } else {
                                    it.trackState = 2 //middle
                                }
                                it.trackStateCount = shipmentStep
                            }
                            shipmentIndex++
                        }
                    }
                    if (returnStep >= 2) {
                        if (it.trackingFlag && it.trackingColor == "red") {
                            if (returnStep == 2) {
                                if (returnIndex == 0) {
                                    it.trackState = 1 //top
                                } else if (returnIndex == 1) {
                                    it.trackState = 3 //bottom
                                }
                                it.trackStateCount = returnStep
                            } else {
                                if (returnIndex == 0) {
                                    it.trackState = 1 //top
                                } else if (returnIndex == (returnStep-1)) {
                                    it.trackState = 3 //bottom
                                } else {
                                    it.trackState = 2 //middle
                                }
                                it.trackStateCount = returnStep
                            }
                            returnIndex++
                        }
                    }
                }


                dataAdapter.initLoad(model.orderTrackingGroupViewModel)
                binding?.trackInfoLayout?.visibility = View.VISIBLE
            } else {
                context?.toast(getString(R.string.give_right_order_id))
            }
        })
    }

    private fun fetchCustomerOrder(mobileNumber: String) {

        binding?.orderCode?.text = orderID
        binding?.reference?.text = "-"

        customerOrderAdapter.clear()
        viewModel.fetchCustomerOrder(mobileNumber).observe(viewLifecycleOwner, Observer { model ->

            if (!model.courierOrderViewModel.isNullOrEmpty()) {
                customerOrderAdapter.initLoad(model.courierOrderViewModel!!)
                binding?.trackInfoLayout?.visibility = View.VISIBLE
            } else {
                context?.toast("সঠিক মোবাইল নম্বর দিয়ে সার্চ করুন")
            }

        })
    }

    private fun callHelpLine() {
        try {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${AppConstant.hotLineNumber}")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }.also {
                startActivity(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToHubLocation(model: OrderTrackData) {

        val hubModel = HubInfo()
        if (model.trackingFlag && model.trackingColor == "green") {
            val trackHub = model.subTrackingShipmentName
            hubModel.apply {
                id = 0
                name = trackHub.name
                value = trackHub.value
                hubAddress = trackHub.hubAddress
                latitude = trackHub.latitude
                longitude = trackHub.longitude
                hubMobile = trackHub.hubMobile
            }
        } else if (model.trackingFlag && model.trackingColor == "red") {
            val trackHub = model.subTrackingReturnName
            hubModel.apply {
                id = 0
                name = trackHub.name
                value = trackHub.value
                hubAddress = trackHub.hubAddress
                latitude = trackHub.latitude
                longitude = trackHub.longitude
                hubMobile = trackHub.hubMobile
            }
        }

        val bundle = bundleOf(
            "hubView" to true,
            "hubModel" to hubModel
        )
        val fragment = MapFragment.newInstance(bundle)
        val tag = MapFragment.tag
        addFragment(fragment, tag)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }


}
