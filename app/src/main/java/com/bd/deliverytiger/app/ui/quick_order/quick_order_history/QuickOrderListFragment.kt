package com.bd.deliverytiger.app.ui.quick_order.quick_order_history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveredReturnedCountRequest
import com.bd.deliverytiger.app.api.model.quick_order.quick_order_history.QuickOrderListRequest
import com.bd.deliverytiger.app.databinding.FragmentQuickOrderListBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.quick_order.QuickBookingTimeSlotBottomSheet
import com.bd.deliverytiger.app.ui.quick_order.QuickOrderRequestViewModel
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class QuickOrderListFragment : Fragment() {
    private var binding: FragmentQuickOrderListBinding? = null
    private val viewModel: QuickOrderRequestViewModel by inject()
    private var dataAdapter: QuickOrderListAdapter = QuickOrderListAdapter()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val sdf1 = SimpleDateFormat("dd MMM, yyyy", Locale.US)

    private var fromDate = "2001-01-01"
    private var toDate = "2001-01-01"

    companion object {
        fun newInstance(): QuickOrderListFragment = QuickOrderListFragment().apply {}
        val tag: String = QuickOrderListFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentQuickOrderListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initView()
        initData()
        initClickLister()
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.quick_booking_list))
    }

    private fun initView() {
        binding?.recyclerView?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
            }
        }
    }

    private fun initData() {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        toDate = simpleDateFormat.format(calendar.time)
        fromDate = simpleDateFormat.format(calendar.time)
        //setDateRangePickerTitle()
        val requestBody = QuickOrderListRequest(fromDate, toDate, SessionManager.courierUserId)
        fetchQuickOrderLists(requestBody)
    }

    private fun initClickLister() {

        binding?.dateRangePicker?.setOnClickListener {
            dateRangePicker()
        }

        dataAdapter.onDelete = { model, _ ->
            alert(getString(R.string.instruction), "কুইক বুকিং রিকোয়েস্টটি ডিলিট করতে চান?", false) {
                if (it == AlertDialog.BUTTON_POSITIVE) {
                    context?.toast("Under dev")
                }
            }.show()
        }

        dataAdapter.onEdit = { model, _ ->

            val dialog = QuickBookingTimeSlotBottomSheet.newInstance(
                model.orderRequestId,
                model.collectionTimeSlot?.collectionTimeSlotId ?: 0,
                model.collectionDate ?: ""
            )
            val tag = QuickBookingTimeSlotBottomSheet.tag
            dialog.show(childFragmentManager, tag)
            dialog.onUpdate = {
                val requestBody = QuickOrderListRequest(fromDate, toDate, SessionManager.courierUserId)
                fetchQuickOrderLists(requestBody)
            }
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
    }

    private fun fetchQuickOrderLists(requestBody: QuickOrderListRequest) {

        viewModel.getMerchantQuickOrders(requestBody).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNullOrEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                dataAdapter.initLoad(list)
                binding?.emptyView?.visibility = View.GONE
            }
        })
    }

    private fun dateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTheme(R.style.CustomMaterialCalendarTheme)
        builder.setTitleText("ডেট রেঞ্জ সিলেক্ট করুন")
        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {

            fromDate = sdf.format(it.first)
            toDate = sdf.format(it.second)
            setDateRangePickerTitle()
            val requestBody = QuickOrderListRequest(fromDate, toDate, SessionManager.courierUserId)
            fetchQuickOrderLists(requestBody)
        }
    }

    private fun setDateRangePickerTitle() {
        val msg = "${DigitConverter.toBanglaDate(fromDate, "yyyy-MM-dd")} - ${DigitConverter.toBanglaDate(toDate, "yyyy-MM-dd")}"
        binding?.dateRangePicker?.text = msg
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
