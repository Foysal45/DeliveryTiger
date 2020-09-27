package com.bd.deliverytiger.app.ui.complain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.databinding.FragmentComplainBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class ComplainFragment: Fragment() {

    private var binding: FragmentComplainBinding? = null
    private val viewModel: ComplainViewModel by inject()

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
        (activity as HomeActivity).setToolbarTitle("কমপ্লেইন")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.submitBtn?.setOnClickListener {
            hideKeyboard()
            if (validate()) {

                val orderCode = binding?.orderCodeTV?.text.toString().trim()
                val complain = binding?.complainTV?.text.toString().trim()
                viewModel.submitComplain(orderCode, complain).observe(viewLifecycleOwner, Observer {
                    if (it) {
                        binding?.orderCodeTV?.text?.clear()
                        binding?.complainTV?.text?.clear()
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

        if (complain.trim().isEmpty()) {
            context?.toast("আপনার অভিযোগ / মতামত লিখুন")
            return false
        }

        return true
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}