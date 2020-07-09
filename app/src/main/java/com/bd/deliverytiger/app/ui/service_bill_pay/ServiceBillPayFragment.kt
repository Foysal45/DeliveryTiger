package com.bd.deliverytiger.app.ui.service_bill_pay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.databinding.FragmentServiceBillPayBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject
import java.util.*

class ServiceBillPayFragment: Fragment() {

    private val viewModel: ServiceBillViewModel by inject()
    private var binding: FragmentServiceBillPayBinding? = null

    companion object {
        fun newInstance(): ServiceBillPayFragment = ServiceBillPayFragment()
        val tag: String = ServiceBillPayFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentServiceBillPayBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val calender = Calendar.getInstance()
        val currentYear = calender.get(Calendar.YEAR)
        val currentMonth = calender.get(Calendar.MONTH)

        val monthList: MutableList<String> = mutableListOf()
        val yearList: MutableList<String> = mutableListOf()

        for (year in currentYear downTo 2019){
            yearList.add("${DigitConverter.toBanglaDigit(year)}")
        }
        for (monthIndex in 0..11){
            monthList.add("${DigitConverter.banglaMonth[monthIndex]}")
        }

        val monthAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, monthList)
        binding?.spinnerMonth?.adapter = monthAdapter
        binding?.spinnerMonth?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }

        val yearAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, yearList)
        binding?.spinnerYear?.adapter = yearAdapter
        binding?.spinnerYear?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }

        val dataAdapter = ServiceBillAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        val request = MonthlyReceivableRequest("4376"/*SessionManager.courierUserId.toString()*/,"2020-06-01", "2020-06-30")
        viewModel.getMerchantMonthlyReceivable(request).observe(viewLifecycleOwner, Observer {
            it.orderList?.let { list ->
                dataAdapter.initLoad(list)
                binding?.totalOrder?.text = "মোট অর্ডার: ${DigitConverter.toBanglaDigit(list.size)} টি"
                binding?.totalCharge?.text = "মোট এমাউন্ট: ${DigitConverter.toBanglaDigit(it.totalAmount, true)} ৳"
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
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

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("সার্ভিসের বিল পেমেন্ট")
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }

}