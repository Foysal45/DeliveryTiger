package com.bd.deliverytiger.app.ui.complain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.chat.ChatUserData
import com.bd.deliverytiger.app.api.model.chat.FirebaseCredential
import com.bd.deliverytiger.app.api.model.complain.ComplainRequest
import com.bd.deliverytiger.app.api.model.complain.general_complain.GeneralComplainListRequest
import com.bd.deliverytiger.app.databinding.FragmentComplainBinding
import com.bd.deliverytiger.app.ui.chat.ChatConfigure
import com.bd.deliverytiger.app.ui.complain.complain_history.ComplainHistoryBottomSheet
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ComplainFragment(): Fragment() {

    private var binding: FragmentComplainBinding? = null
    private val viewModel: ComplainViewModel by inject()

    private lateinit var dataAdapter: ComplainAdapter

    private var selectedType = 0

    private var orderId: String? = null

    private var sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var sdf1 = SimpleDateFormat("dd MM", Locale.US)

    private var fromDate = ""
    private var displayFromDate = ""
    private var toDate = ""
    private var displayToDate = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentComplainBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("???????????????????????? ????????????????????????")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle: Bundle? = arguments
        bundle?.let {
            orderId = it.getString("orderId")
        }

        setUpSpinner()
        initComplainList()
        initDate()
        fetchComplain()

        binding?.orderCodeTV?.setText(orderId)

        /*viewModel.fetchHelpLineNumbers().observe(viewLifecycleOwner, Observer { model->
            if (model.helpLine2.isNullOrEmpty()){
                binding?.helpLineContactLayout?.visibility = View.GONE
            }else{
                binding?.helpLineContactLayout?.visibility = View.VISIBLE
                binding?.helpLineNumber?.text = DigitConverter.toBanglaDigit(model.helpLine2)
                binding?.helpLineNumber?.setOnClickListener{
                    callHelplineNumber(model.helpLine2!!)
                }
            }
        })*/

        binding?.submitBtn?.setOnClickListener {
            hideKeyboard()
            if (validate()) {

                val orderCode = binding?.orderCodeTV?.text.toString().trim()
                val complain = binding?.complainTV?.text.toString().trim()
                val code = orderCode.toUpperCase(Locale.US).replace("DT-", "")

                val requestBody = ComplainRequest(orderCode, complain, "app", SessionManager.companyName, SessionManager.mobile)
                viewModel.submitComplain(requestBody).observe(viewLifecycleOwner, Observer { complainStatus->
                    when {
                        complainStatus > 0 -> {
                            binding?.orderCodeTV?.text?.clear()
                            binding?.complainTV?.text?.clear()
                            binding?.spinnerComplainType?.setSelection(0)

                            context?.toast("??????????????? ?????????????????? / ??????????????? ?????????????????? ???????????????")
                            fetchComplain()
                        }
                        complainStatus == -1 -> {
                            context?.toast("?????? ???????????????????????? ???????????????????????? ????????? ???????????????")
                        }
                        else -> {
                            context?.toast("??????????????? ???????????? ?????????????????? ???????????????, ???????????? ?????????????????? ????????????")
                        }
                    }
                })
            }
        }

        binding?.chatLayout?.setOnClickListener {
            goToChatActivity()
        }

        binding?.datePicker?.setOnClickListener {
            dateRangePicker()
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

        binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding?.dateFilterCard?.visibility = View.GONE
                        fetchComplain()
                    }
                    1 -> {
                        fetchGeneralComplain()
                    }
                }
            }
        })
    }

    private fun initDate() {
        val calender = Calendar.getInstance()
        fromDate = sdf.format(calender.timeInMillis)
        displayFromDate = sdf.format(calender.time)
        toDate = sdf.format(calender.timeInMillis)
        displayToDate = sdf.format(calender.time)
        setDateRangePickerTitle()
    }

    private fun initComplainList() {

        dataAdapter = ComplainAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        dataAdapter.onItemClicked = {
            goToComplainHistoryBottomSheet(it.orderId)
        }

    }

    private fun goToComplainHistoryBottomSheet(bookingCode: Int) {
        val tag = ComplainHistoryBottomSheet.tag
        val dialog = ComplainHistoryBottomSheet.newInstance(bookingCode)
        dialog.show(childFragmentManager, tag)
    }

    private fun dateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTheme(R.style.CustomMaterialCalendarTheme)
        builder.setTitleText("????????? ??????????????? ????????????????????? ????????????")
        val picker = builder.build()
        picker.show(childFragmentManager, "Picker")
        picker.addOnPositiveButtonClickListener {

            fromDate = sdf.format(it.first)
            displayFromDate = sdf.format(it.first)
            toDate = sdf.format(it.second)
            displayToDate = sdf.format(it.second)
            setDateRangePickerTitle()
            fetchGeneralComplain()
        }
    }

    private fun setDateRangePickerTitle() {
        val msg = "${DigitConverter.toBanglaDate(displayFromDate, "yyyy-MM-dd")} - ${DigitConverter.toBanglaDate(displayToDate, "yyyy-MM-dd")}"
        binding?.datePicker?.text = msg
    }

    private fun fetchComplain() {
        binding?.recyclerview?.visibility = View.GONE
        binding?.progressBar2?.visibility = View.VISIBLE
        viewModel.fetchComplainList(SessionManager.courierUserId, 0).observe(viewLifecycleOwner, Observer { list ->
            binding?.progressBar2?.visibility = View.GONE
            binding?.recyclerview?.visibility = View.VISIBLE
            dataAdapter.initLoad(list)
            if (list.isNotEmpty()){
                binding?.complaintTitle?.visibility = View.VISIBLE
            }
        })
    }

    private fun fetchGeneralComplain() {

        binding?.recyclerview?.visibility = View.GONE
        binding?.progressBar2?.visibility = View.VISIBLE
        var requestBody = GeneralComplainListRequest(fromDate, toDate)
        viewModel.fetchWithoutOrderCodeComplains(requestBody).observe(viewLifecycleOwner, Observer { list ->
            binding?.progressBar2?.visibility = View.GONE
            binding?.dateFilterCard?.visibility = View.VISIBLE
            binding?.recyclerview?.visibility = View.VISIBLE
            dataAdapter.initGeneralLoad(list)
            if (list.isNotEmpty()){
                binding?.complaintTitle?.visibility = View.VISIBLE
            }
        })
    }


    private fun validate(): Boolean {

        val orderCode = binding?.orderCodeTV?.text.toString()
        val complain = binding?.complainTV?.text.toString()

        if (selectedType == 1 || selectedType == 2) {
            if (orderCode.trim().isEmpty()) {
                context?.toast("?????????????????? ???????????? ???????????????")
                return false
            }
        }

        if (orderCode.trim().isNotEmpty() && !isValidDTCode(orderCode)) {
            context?.toast("???????????? ?????????????????? ???????????? ???????????????")
            return false
        }

        if (selectedType == 0) {
            context?.toast("???????????????????????? ???????????? ????????????????????? ????????????")
            return false
        }

        if (selectedType == 6) {
            if (complain.trim().isEmpty()) {
                context?.toast("??????????????? ?????????????????? / ??????????????? ???????????????")
                return false
            }
        }

        return true
    }

    private fun setUpSpinner() {

        val pickupDistrictList: MutableList<String> = mutableListOf()
        pickupDistrictList.add("???????????????????????? ???????????? ????????????????????? ????????????")
        pickupDistrictList.add("???????????????????????? ???????????? ????????????????????? ???????????????????????? ???????????? ?????????")
        pickupDistrictList.add("????????????????????? ????????????????????? ???????????? ???????????? ????????? ?????????")
        pickupDistrictList.add("COD ????????????????????? ???????????? ????????? ?????????")
        pickupDistrictList.add("????????????????????? ???????????? ????????????????????? ????????? ?????????")
        pickupDistrictList.add("????????????????????? ??????????????????????????? ????????????????????? ????????? ??????")
        pickupDistrictList.add("???????????? ????????????????????????")

        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupDistrictList)
        binding?.spinnerComplainType?.adapter = spinnerAdapter
        binding?.spinnerComplainType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedType = position
                binding?.orderCodeTV?.visibility = View.GONE
                if (position > 0) {
                    binding?.complainTV?.setText(pickupDistrictList[position])
                    binding?.orderCodeTV?.visibility = View.VISIBLE
                    if (position == pickupDistrictList.lastIndex) {
                        binding?.complainTV?.text?.clear()
                        binding?.complainTV?.visibility = View.VISIBLE
                    }else{
                        binding?.complainTV?.visibility = View.GONE
                    }
                } else {
                    binding?.complainTV?.visibility = View.GONE
                }
            }
        }

    }

    private fun goToChatActivity() {
        val firebaseCredential = FirebaseCredential(
            firebaseWebApiKey = BuildConfig.FirebaseWebApiKey
        )
        val senderData = ChatUserData(SessionManager.courierUserId.toString(), SessionManager.companyName, SessionManager.mobile,
            imageUrl = "https://static.ajkerdeal.com/delivery_tiger/profile/${SessionManager.courierUserId}.jpg",
            role = "dt",
            fcmToken = SessionManager.firebaseToken
        )
        val receiverData = ChatUserData("938", "Complain Admin", "01894811222",
            imageUrl = "https://static.ajkerdeal.com/images/admin_users/dt/938.jpg",
            role = "retention"
        )
        ChatConfigure(
            "dt-retention",
            senderData,
            firebaseCredential = firebaseCredential,
            receiver = receiverData
        ).config(requireContext())
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}