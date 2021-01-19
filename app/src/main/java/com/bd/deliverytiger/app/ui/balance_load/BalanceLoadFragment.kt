package com.bd.deliverytiger.app.ui.balance_load

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentBalanceLoadBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.web_view.WebViewFragment
import com.bd.deliverytiger.app.utils.*
import org.koin.android.ext.android.inject
import timber.log.Timber

@SuppressLint("SetTextI18n")
class BalanceLoadFragment: Fragment() {

    private var binding: FragmentBalanceLoadBinding? = null

    private val viewModel: BalanceLoadViewModel by inject()

    private var amountTaka: Int = 0
    private var minimumAmount: Int = 100
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

        fetchCurrentBalance()
        fetchBalanceLimit()

        binding?.payBtn?.setOnClickListener {
            hideKeyboard()
            if (validate()) {
                paymentGateway(amountTaka)
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

    private fun fetchCurrentBalance() {
        viewModel.fetchMerchantCurrentAdvanceBalance(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { accountBalance ->
            val balance = accountBalance.balance

            viewModel.fetchMerchantBalanceInfo(SessionManager.courierUserId, balance).observe(viewLifecycleOwner, Observer { balanceInfo ->

                //val adjustBalance = balanceInfo.serviceCharge + balanceInfo.credit + balanceInfo.staticVal
                Timber.tag("adjustBalance").d( "serviceCharge: ${balanceInfo.serviceCharge} + credit: ${balanceInfo.credit} + staticVal: ${balanceInfo.staticVal}")

                val adjustBalance = "ব্যালান্স: <font color='#f05a2b'>৳${DigitConverter.toBanglaDigit(balanceInfo.adjustBalance, true)}</font>"
                binding?.adjustedBalance?.text = HtmlCompat.fromHtml(adjustBalance, HtmlCompat.FROM_HTML_MODE_LEGACY)

                val creditMsg = "ক্রেডিট: <font color='#f05a2b'>৳${DigitConverter.toBanglaDigit(balanceInfo.credit, true)}</font>"
                binding?.credit?.text = HtmlCompat.fromHtml(creditMsg, HtmlCompat.FROM_HTML_MODE_LEGACY)
            })
        })
    }

    private fun fetchBalanceLimit() {
        viewModel.fetchBalanceLimit(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { model ->
            minimumAmount = model.minAmount
            maximumAmount = model.maxAmount
        })
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
        super.onDestroyView()
        binding = null
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }

}