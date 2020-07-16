package com.bd.deliverytiger.app.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.databinding.FragmentDashboardBinding
import com.bd.deliverytiger.app.ui.add_order.AddOrderFragmentOne
import com.bd.deliverytiger.app.ui.all_orders.AllOrdersFragment
import com.bd.deliverytiger.app.ui.billing_of_service.BillingofServiceFragment
import com.bd.deliverytiger.app.ui.cod_collection.CODCollectionFragment
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.ui.payment_statement.PaymentStatementFragment
import com.bd.deliverytiger.app.ui.shipment_charges.ShipmentChargeFragment
import com.bd.deliverytiger.app.utils.*
import com.bd.deliverytiger.app.utils.DigitConverter.banglaMonth
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null

    private lateinit var dashboardAdapter: DashboardAdapter
    private val dataList: MutableList<DashboardData> = mutableListOf()
    private val viewModel: DashboardViewModel by inject()
    private lateinit var monthSpinnerAdapter: CustomSpinnerAdapter

    private val viewList: MutableList<String> = mutableListOf()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var currentYear = 0
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedStartDate = ""
    private var selectedEndDate = ""
    private var currentDate = ""
    private var isLoading = false


    companion object {
        fun newInstance(): DashboardFragment = DashboardFragment().apply {}
        val tag: String = DashboardFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDashboardBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setDashBoardAdapter()
        setSpinner()



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
                        isLoading = true
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        isLoading = false
                    }
                }
            }
        })
    }

    /**
     * Called from Home Activity
     */
    fun showBanner(showBanner: Boolean, bannerImgUri: String?) {

        if (showBanner) {
            binding?.bannerImage?.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(bannerImgUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_banner_place))
                .into(binding?.bannerImage!!)
        } else {
            binding?.bannerImage?.visibility = View.GONE
        }
    }

    private fun setDashBoardAdapter() {

        dashboardAdapter = DashboardAdapter(requireContext(), dataList)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (dataList[position].dashboardSpanCount!! == 0 || dataList[position].dashboardSpanCount!! > 2) {
                    2
                } else {
                    dataList[position].dashboardSpanCount!!
                }
            }
        }
        with(binding?.recyclerview!!) {
            setHasFixedSize(false)
            layoutManager = gridLayoutManager
            adapter = dashboardAdapter
        }

        dashboardAdapter.onItemClick = { _, model ->
            //dashBoardClickEvent(model?.dashboardRouteUrl!!)
            if (model?.count != 0) {
                when (model?.dashboardRouteUrl) {
                    "add-order" -> {
                        addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
                    }
                    "billing-service" -> {
                        addFragment(BillingofServiceFragment.newInstance(), BillingofServiceFragment.tag)
                    }
                    "order-tracking" -> {
                        addFragment(OrderTrackingFragment.newInstance(""), OrderTrackingFragment.tag)
                    }
                    "shipment-charge" -> {
                        addFragment(ShipmentChargeFragment.newInstance(), ShipmentChargeFragment.tag)
                    }
                    "all-order" -> {
                        goToAllOrder(model.name ?: "", model.dashboardStatusFilter)
                    }
                    "cod-collection" -> {
                        addFragment(CODCollectionFragment.newInstance(), CODCollectionFragment.tag)
                    }
                    else -> {
                        addFragment(AllOrdersFragment.newInstance(), AllOrdersFragment.tag)
                    }
                }
            } else {
                VariousTask.showShortToast(context, "পর্যাপ্ত তথ্য নেই")
            }
        }

        binding?.paymentDoneLayout?.setOnClickListener {
            addFragment(PaymentStatementFragment.newInstance(), PaymentStatementFragment.tag)
        }

        binding?.orderBtn?.setOnClickListener {
            addFragment(AddOrderFragmentOne.newInstance(), AddOrderFragmentOne.tag)
        }

        /*binding?.collectionLayout?.setOnClickListener {
            goToAllOrder("কালেকশন করা হয়েছে", "কালেকশন করা হয়েছে")
        }*/

        binding?.dateRangePicker?.setOnClickListener {

            val builder = MaterialDatePicker.Builder.dateRangePicker()
            builder.setTheme(R.style.CustomMaterialCalendarTheme)
            builder.setTitleText("ডেট রেঞ্জ সিলেক্ট করুন")
            val picker = builder.build()
            picker.show(childFragmentManager, "Picker")
            picker.addOnPositiveButtonClickListener {

                selectedStartDate = sdf.format(it.first)
                selectedEndDate = sdf.format(it.second)
                selectedMonth = 0
                selectedYear = 0

                val sdf1 = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                //context?.toast("$selectedStartDate $selectedEndDate")
                viewList[0] = DigitConverter.toBanglaDigit("${sdf1.format(it.first)} - ${sdf1.format(it.second)}")
                monthSpinnerAdapter.notifyDataSetChanged()
                binding?.monthSpinner?.setSelection(0)

                getDashBoardData(selectedMonth, selectedYear)
            }
        }

    }

    private fun setSpinner() {
        val calender = Calendar.getInstance()
        currentYear = calender.get(Calendar.YEAR)
        val currentMonth = calender.get(Calendar.MONTH)
        selectedYear = currentYear
        selectedMonth = currentMonth + 1
        currentDate = sdf.format(calender.timeInMillis)
        selectedStartDate = currentDate
        selectedEndDate = currentDate
        //getDashBoardData(selectedMonth, selectedYear)

        val list: MutableList<MonthDataModel> = mutableListOf()
        viewList.clear()
        viewList.add(getString(R.string.dashboard_spinner_temp))
        for (year in currentYear downTo 2019) {
            //Timber.d("DashboardTag", "year: $year")
            var lastMonth = 11
            if (year == currentYear) {
                lastMonth = currentMonth
            }
            for (monthIndex in lastMonth downTo 0) {
                if (year == 2019 && monthIndex == 6) {
                    break
                }
                list.add(MonthDataModel(monthIndex + 1, year))
                viewList.add("${banglaMonth[monthIndex]}, ${DigitConverter.toBanglaDigit(year)}")
            }
        }

        monthSpinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, viewList)
        binding?.monthSpinner?.adapter = monthSpinnerAdapter
        binding?.monthSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (position > 0) {
                    if (!isLoading) {
                        val model = list[position - 1]
                        selectedYear = model.year
                        selectedMonth = model.monthId

                        val calendar = Calendar.getInstance()
                        calendar.set(selectedYear, selectedMonth - 1, 1)
                        val lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        selectedStartDate = "$selectedYear-$selectedMonth-01"
                        selectedEndDate = "$selectedYear-$selectedMonth-$lastDate"

                        getDashBoardData(model.monthId, model.year)
                        Timber.d("DashboardTag", "${model.monthId} $currentYear")
                    }
                }
            }
        }
        binding?.monthSpinner?.setSelection(1)
    }


    private fun getDashBoardData(selectedMonth: Int, selectedYear: Int) {

        val dashBoardReqBody = DashBoardReqBody(selectedMonth, selectedYear, selectedStartDate, selectedEndDate, SessionManager.courierUserId)
        Timber.d("DashboardTag r ", dashBoardReqBody.toString())
        viewModel.getDashboardStatusGroup(dashBoardReqBody).observe(viewLifecycleOwner, Observer { model ->

            model.orderDashboardViewModel?.let {
                dataList.clear()
                dataList.addAll(it)
                dashboardAdapter.notifyDataSetChanged()
            }

            if (model.paymentDashboardViewModel?.isNullOrEmpty() == false) {

                val paymentModel1 = model.paymentDashboardViewModel!!.first()
                binding?.amount1?.text = "৳ ${DigitConverter.toBanglaDigit(paymentModel1.totalAmount.toInt().toString())}"
                binding?.msg1?.text = "${DigitConverter.toBanglaDigit(paymentModel1.count)}টি ${paymentModel1.name}"

                if (model.paymentDashboardViewModel!!.size >= 2) {
                    val paymentModel2 = model.paymentDashboardViewModel!![1]
                    binding?.amount2?.text = paymentModel2.name
                }
            }

            if (model.pickDashboardViewModel?.isNullOrEmpty() == false) {

                val sdf = SimpleDateFormat("dd MMM", Locale("bn","BD"))
                val currentDate = sdf.format(Date())
                val pickModel = model.pickDashboardViewModel!!.first()
                binding?.msg3?.text = "আজকে ($currentDate) পার্সেল দিয়েছি"
                binding?.amount3?.text = "${DigitConverter.toBanglaDigit(pickModel.count.toString())}টি"

                if (pickModel.count == 0) {
                    binding?.collectionCountLayout?.visibility = View.GONE
                    binding?.switchCollector?.visibility = View.VISIBLE
                    binding?.switchCollector?.isChecked = SessionManager.isCollectorAttendance

                    binding?.switchCollector?.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            val calender = Calendar.getInstance()
                            val dayOfYear = calender.get(Calendar.DAY_OF_YEAR)
                            if (SessionManager.collectorAttendanceDateOfYear != dayOfYear) {
                                viewModel.updateStatusLocation(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer {
                                    if (it) {
                                        context?.toast("সফলভাবে আপডেট হয়েছে")
                                        SessionManager.isCollectorAttendance = true
                                        SessionManager.collectorAttendanceDateOfYear = dayOfYear
                                    }
                                })
                            } else {
                                context?.toast("আপনি ইতিমধ্যে জানিয়েছেন")
                            }
                        }
                    }
                }
            } else {
                binding?.collectionLayout?.visibility = View.GONE
            }

        })
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    private fun goToAllOrder(statusGroupName: String, statusFilter: String) {

        val bundle = Bundle()
        bundle.putString("statusGroup", statusGroupName)
        bundle.putString("fromDate", selectedStartDate)
        bundle.putString("toDate", selectedEndDate)
        bundle.putString("dashboardStatusFilter", statusFilter)

        val fragment = AllOrdersFragment.newInstance(bundle)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AllOrdersFragment.tag)
        ft?.addToBackStack(AllOrdersFragment.tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        binding?.unbind()
        binding = null
        super.onDestroyView()
    }
}
