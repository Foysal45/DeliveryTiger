package com.bd.deliverytiger.app.ui.balance_load

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentBalanceLoadBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.*

class BalanceLoadFragment: Fragment() {

    private var binding: FragmentBalanceLoadBinding? = null

    private var amountTaka: Int = 0
    private var minimumAmount: Int = 1000
    private var maximumAmount: Int = 50000

    companion object {
        fun newInstance(): BalanceLoadFragment = BalanceLoadFragment().apply {

        }
        val tag: String = BalanceLoadFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("ব্যালেন্স লোড")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentBalanceLoadBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.payBtn?.setOnClickListener {
            hideKeyboard()
            if (validate()) {
                paymentGateway(amountTaka)
            }
        }
    }

    private fun validate(): Boolean {

        val amountText = binding?.amountET?.text.toString().trim()
        if (amountText.isEmpty()) {
            context?.toast("টাকার অ্যামাউন্ট লিখুন")
            return false
        }
        amountTaka = amountText.toIntOrNull() ?: 0
        if (amountTaka < minimumAmount) {
            context?.toast("টাকার অ্যামাউন্ট ৳ ${DigitConverter.toBanglaDigit(minimumAmount, true)} টাকার সমান বা বেশি হতে হবে")
            return false
        }
        if (amountTaka > maximumAmount) {
            context?.toast("টাকার অ্যামাউন্ট ৳ ${DigitConverter.toBanglaDigit(maximumAmount, true)} টাকার থেকে কম হতে হবে")
            return false
        }


        return true
    }

    private fun paymentGateway(amount: Int) {

        val url = "${AppConstant.BALANCE_LOAD_GATEWAY}?CourierID=${SessionManager.courierUserId}&Amount=$amount"
        val fragment = WebViewFragment.newInstance(url, "ব্যালেন্স লোড")
        val tag = WebViewFragment.tag

        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}