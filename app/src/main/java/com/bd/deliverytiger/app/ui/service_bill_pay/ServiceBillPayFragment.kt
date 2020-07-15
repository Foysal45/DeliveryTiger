package com.bd.deliverytiger.app.ui.service_bill_pay

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.MonthlyReceivableUpdateRequest
import com.bd.deliverytiger.app.api.model.service_bill_pay.OrderCode
import com.bd.deliverytiger.app.api.model.service_bill_pay.OrderData
import com.bd.deliverytiger.app.databinding.FragmentServiceBillPayBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
@SuppressLint("SetTextI18n")
class ServiceBillPayFragment: Fragment() {

    private val viewModel: ServiceBillViewModel by inject()
    private var binding: FragmentServiceBillPayBinding? = null

    private lateinit var dataAdapter: ServiceBillAdapter
    private var selectedMonthIndex: Int = 0
    private var selectedYear: Int = 0
    private var fromDate: String = ""
    private var toDate: String = ""
    private val orderList: MutableList<OrderData> = mutableListOf()

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

        val monthList: MutableList<String> = mutableListOf()
        val yearList: MutableList<String> = mutableListOf()
        for (year in currentYear downTo 2019){
            yearList.add(DigitConverter.toBanglaDigit(year))
        }
        for (monthIndex in 0..11){
            monthList.add(DigitConverter.banglaMonth[monthIndex])
        }

        calender.add(Calendar.MONTH, -1)
        val previousMonth = calender.get(Calendar.MONTH)
        selectedYear = calender.get(Calendar.YEAR)
        selectedMonthIndex = previousMonth
        Timber.d("selectedYear $selectedYear, selectedMonthIndex $selectedMonthIndex")

        val monthAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, monthList)
        binding?.spinnerMonth?.adapter = monthAdapter
        binding?.spinnerMonth?.setSelection(selectedMonthIndex)

        val yearAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, yearList)
        binding?.spinnerYear?.adapter = yearAdapter
        binding?.spinnerYear?.setSelection(yearList.indexOf(selectedYear.toString()))

        Handler().postDelayed({
            binding?.spinnerMonth?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                    if (view != null) {
                        selectedMonthIndex = position
                        fetchMerchantMonthlyReceivable(selectedYear, selectedMonthIndex)
                    }
                }
            }
            binding?.spinnerYear?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                    if (view != null) {
                        selectedYear = yearList[position].toInt()
                        fetchMerchantMonthlyReceivable(selectedYear, selectedMonthIndex)
                    }
                }
            }
        },300L)

        dataAdapter = ServiceBillAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        //fetchMerchantMonthlyReceivable(selectedYear, selectedMonthIndex) on Resume

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

        binding?.payBtn?.setOnClickListener {

            paymentGateway(fromDate, toDate)
        }
    }


    private fun fetchMerchantMonthlyReceivable(year: Int, monthIndex: Int) {

        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, monthIndex)
        val lastDay = calender.getActualMaximum(Calendar.DAY_OF_MONTH)

        fromDate = "$year-${monthIndex+1}-01"
        toDate = "$year-${monthIndex+1}-$lastDay"

        val request = MonthlyReceivableRequest(SessionManager.courierUserId.toString(),fromDate, toDate)
        viewModel.getMerchantMonthlyReceivable(request).observe(viewLifecycleOwner, Observer {
            it.orderList?.let { list ->
                dataAdapter.initLoad(list)
                orderList.clear()
                orderList.addAll(list)
                if (list.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                    binding?.payBtn?.visibility = View.GONE
                    binding?.header?.parent?.visibility = View.GONE
                    binding?.header?.info1?.text = "মোট অর্ডার: ০ টি"
                    binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ০ ৳"
                } else {
                    binding?.emptyView?.visibility = View.GONE
                    binding?.payBtn?.visibility = View.VISIBLE
                    binding?.header?.parent?.visibility = View.VISIBLE
                    binding?.header?.info1?.text = "মোট অর্ডার: ${DigitConverter.toBanglaDigit(list.size)} টি"
                    binding?.header?.info2?.text = "মোট অ্যামাউন্ট: ${DigitConverter.toBanglaDigit(it.totalAmount, true)} ৳"
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("সার্ভিসের বিল পেমেন্ট")
        fetchMerchantMonthlyReceivable(selectedYear, selectedMonthIndex)
    }

    private fun paymentGateway(from: String, to: String) {

        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val sdfTrans = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = sdf.format(System.currentTimeMillis())
        val dateTrans = sdfTrans.format(System.currentTimeMillis())
        val orderCodeList: MutableList<OrderCode> = mutableListOf()
        orderList.forEach {
            orderCodeList.add(OrderCode(it.orderCode?: "DT-",it.totalAmount,"Bkash-$dateTrans"))
        }
        val model = MonthlyReceivableUpdateRequest(date,"",orderCodeList)
        val bundle = bundleOf(
            "requestBody" to model
        )

        // ToDo: remove
        val courierId = SessionManager.courierUserId.toString() //6188

        val url = "${AppConstant.GATEWAY}?CourierID=$courierId&FromDate=$from&ToDate=$to"
        val fragment = WebViewFragment.newInstance(url, "পেমেন্ট", bundle)
        val tag = WebViewFragment.tag

        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }

}