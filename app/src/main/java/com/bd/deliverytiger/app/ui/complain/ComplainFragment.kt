package com.bd.deliverytiger.app.ui.complain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentComplainBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class ComplainFragment: Fragment() {

    private var binding: FragmentComplainBinding? = null
    private val viewModel: ComplainViewModel by inject()

    private var selectedType = 0

    companion object {
        fun newInstance(): ComplainFragment = ComplainFragment()
        val tag: String = ComplainFragment::class.java.name
    }

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

        setUpSpinner()

        binding?.submitBtn?.setOnClickListener {
            hideKeyboard()
            if (validate()) {

                val orderCode = binding?.orderCodeTV?.text.toString().trim()
                val complain = binding?.complainTV?.text.toString().trim()
                viewModel.submitComplain(orderCode, complain).observe(viewLifecycleOwner, Observer {
                    if (it) {
                        binding?.orderCodeTV?.text?.clear()
                        binding?.complainTV?.text?.clear()
                        binding?.spinnerComplainType?.setSelection(0)

                        context?.toast("আপনার অভিযোগ / মতামত সাবমিট হয়েছে")
                    }
                })
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
        pickupDistrictList.add("কাস্টমার এখনো প্রোডাক্ট ডেলিভারি পায় নাই")
        pickupDistrictList.add("রিটার্ন প্রোডাক্ট এখনো বুঝে পাই নাই")
        pickupDistrictList.add("COD পেমেন্ট এখনো পাই নাই")
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}