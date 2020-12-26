package com.bd.deliverytiger.app.ui.order_tracking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentOrderTrackingBinding
import com.bd.deliverytiger.app.ui.complain.ComplainFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.AppConstant
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject
import timber.log.Timber

class OrderTrackingFragment : Fragment() {

    private var binding: FragmentOrderTrackingBinding? = null
    private val viewModel: OrderTrackingViewModel by inject()

    private lateinit var dataAdapter: OrderTrackingNewAdapter
    private lateinit var customerOrderAdapter: CustomerOrderAdapter

    private var orderID = ""

    companion object {
        fun newInstance(orderID: String): OrderTrackingFragment = OrderTrackingFragment().apply {
            this.orderID = orderID
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
        customerOrderAdapter.onItemClick = { model, position ->
            orderID = model.courierOrdersId ?: ""
            binding?.recyclerView?.adapter = dataAdapter
            getOrderTrackingList(orderID)
        }

        binding?.trackBtn?.setOnClickListener {
            Timber.d("trackBtn called")
            trackOrder()
        }

        if (orderID.isNotEmpty()) {
            getOrderTrackingList(orderID)
            binding?.orderIdET?.setText(orderID)
        }

        binding?.complainBtn?.setOnClickListener {
            val tag = ComplainFragment.tag
            val fragment = ComplainFragment.newInstance()
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
            binding?.orderIdET?.setText("DT-246194") //DT-12222 01715269261
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

        binding?.orderCode?.text = orderId
        binding?.reference?.text = "Reference"

        dataAdapter.clear()
        viewModel.fetchOrderTrackingList(orderId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                dataAdapter.initLoad(list)
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
