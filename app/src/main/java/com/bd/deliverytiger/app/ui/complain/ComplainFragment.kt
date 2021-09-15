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
import com.bd.deliverytiger.app.databinding.FragmentComplainBinding
import com.bd.deliverytiger.app.ui.chat.ChatConfigure
import com.bd.deliverytiger.app.ui.complain.complain_history.ComplainHistoryBottomSheet
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject
import java.util.*

class ComplainFragment(): Fragment() {

    private var binding: FragmentComplainBinding? = null
    private val viewModel: ComplainViewModel by inject()

    private lateinit var dataAdapter: ComplainAdapter

    private var selectedType = 0

    private var orderId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentComplainBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("ডেলিভারি কমপ্লেইন")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle: Bundle? = arguments
        bundle?.let {
            orderId = it.getString("orderId")
        }

        setUpSpinner()
        initComplainList()
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

                viewModel.submitComplain(orderCode, complain).observe(viewLifecycleOwner, Observer { complainStatus->
                    when {
                        complainStatus > 0 -> {
                            binding?.orderCodeTV?.text?.clear()
                            binding?.complainTV?.text?.clear()
                            binding?.spinnerComplainType?.setSelection(0)

                            context?.toast("আপনার অভিযোগ / মতামত সাবমিট হয়েছে")
                            fetchComplain()
                        }
                        complainStatus == -1 -> {
                            context?.toast("এই কমপ্লেইন ইতিমধ্যে করা হয়েছে")
                        }
                        else -> {
                            context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
                        }
                    }
                })
            }
        }

        binding?.chatLayout?.setOnClickListener {
            goToChatActivity()
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

    private fun fetchComplain() {

        viewModel.fetchComplainList(SessionManager.courierUserId, 0).observe(viewLifecycleOwner, Observer { list ->
            dataAdapter.initLoad(list)
            if (list.isNotEmpty()){
                binding?.complaintTitle?.visibility = View.VISIBLE
            }
        })
    }

    private fun validate(): Boolean {

        val orderCode = binding?.orderCodeTV?.text.toString()
        val complain = binding?.complainTV?.text.toString()

        if (orderCode.trim().isEmpty()) {
            context?.toast("অর্ডার আইডি লিখুন")
            return false
        }

        if (selectedType == 0) {
            context?.toast("কমপ্লেইন টাইপ সিলেক্ট করুন")
            return false
        }

        if (complain.trim().isEmpty()) {
            context?.toast("আপনার অভিযোগ / মতামত লিখুন")
            return false
        }

        return true
    }

    private fun setUpSpinner() {

        val pickupDistrictList: MutableList<String> = mutableListOf()
        pickupDistrictList.add("কমপ্লেইন টাইপ সিলেক্ট করুন")
        pickupDistrictList.add("কাস্টমার এখনো পার্সেল ডেলিভারি পায় নাই")
        pickupDistrictList.add("রিটার্ন পার্সেল এখনো বুঝে পাই নাই")
        pickupDistrictList.add("COD পেমেন্ট এখনো পাই নাই")
        pickupDistrictList.add("পার্সেল এখনো কালেকশন হয় নাই")
        pickupDistrictList.add("অন্য কমপ্লেইন")

        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupDistrictList)
        binding?.spinnerComplainType?.adapter = spinnerAdapter
        binding?.spinnerComplainType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedType = position
                if (position > 0) {
                    binding?.complainTV?.setText(pickupDistrictList[position])
                    if (position == pickupDistrictList.lastIndex) {
                        binding?.complainTV?.text?.clear()
                        binding?.complainTV?.visibility = View.VISIBLE
                    } else {
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