package com.bd.deliverytiger.app.ui.quick_order.quick_order_history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.quick_order.quick_order_history.QuickOrderListRequest
import com.bd.deliverytiger.app.databinding.FragmentQuickOrderListBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.quick_order.QuickOrderRequestViewModel
import com.bd.deliverytiger.app.utils.SessionManager
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class QuickOrderListFragment : Fragment() {
    private var binding: FragmentQuickOrderListBinding? = null
    private val viewModel: QuickOrderRequestViewModel by inject()
    private  var dataAdapter: QuickOrderListAdapter = QuickOrderListAdapter()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val sdf1 = SimpleDateFormat("dd MMM, yyyy", Locale.US)

    private var selectedDate = "2001-03-23"
    private var selectedDateFormatted = "2001-03-23"

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
        initClickLister()
        fetchQuickOrderLists(selectedDate)
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

    private fun initClickLister() {
        binding?.dateRangePicker?.setOnClickListener {
            dateRangePicker()
        }
    }

        private fun fetchQuickOrderLists(date: String) {
        val requestBody  = QuickOrderListRequest(date, date, SessionManager.courierUserId)
        viewModel.getMerchantQuickOrders(requestBody).observe(viewLifecycleOwner, Observer { list->
            if (list.isNullOrEmpty()){
                binding?.ivEmpty?.visibility = View.VISIBLE
            }else{
                dataAdapter.initLoad(list)
                binding?.ivEmpty?.visibility = View.GONE
            }
        })
    }

    private fun dateRangePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTheme(R.style.CustomMaterialCalendarTheme)
        builder.setTitleText("Select date")
        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {
            selectedDate = sdf.format(it)
            selectedDateFormatted = sdf1.format(it)
            setDateRangePickerTitle()
            fetchQuickOrderLists(selectedDate)
        }
    }

    private fun setDateRangePickerTitle(){
        binding?.dateRangePicker?.text = selectedDateFormatted
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
