package com.bd.deliverytiger.app.ui.charge_calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.api.model.calculator.WeightPrice
import com.bd.deliverytiger.app.databinding.FragmentDeliveryChargeCalculatorBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import org.koin.android.ext.android.inject

@SuppressLint("SetTextI18n")
class DeliveryChargeCalculatorFragment: Fragment() {

    private var binding: FragmentDeliveryChargeCalculatorBinding? = null
    private val viewModel: DeliveryChargeViewModel by inject()

    private val weightPriceList: MutableList<WeightPrice> = mutableListOf()
    private var showTitle: Boolean = false

    companion object {
        fun newInstance(showTitle: Boolean = false): DeliveryChargeCalculatorFragment = DeliveryChargeCalculatorFragment().apply {
            this.showTitle = showTitle
        }
        val tag: String = DeliveryChargeCalculatorFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        if (showTitle) {
            (activity as HomeActivity).setToolbarTitle("ডেলিভারি চার্জ ক্যালকুলেটর")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDeliveryChargeCalculatorBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getDashboardStatusGroup(14, 2).observe(viewLifecycleOwner, Observer { list ->
            weightPriceList.clear()
            weightPriceList.addAll(list)

            calculate(list.first().courierDeliveryCharge)
            binding?.indicatorSeekBar?.let { view ->
                with(view) {
                    min = list.first().weightNumber.toFloat()
                    max = list.last().weightNumber.toFloat()
                    tickCount = list.size
                }
            }
        })
        binding?.indicatorSeekBar?.onSeekChangeListener = object : OnSeekChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onSeeking(seekParams: SeekParams?) {

                val position = seekParams?.thumbPosition ?: 0
                val model = weightPriceList[position]
                calculate(model.courierDeliveryCharge)
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}

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


    private fun calculate(charge: Int) {
        binding?.changeTV?.text = "আপনার ডেলিভারি চার্জ ৳ ${DigitConverter.toBanglaDigit(charge, true)} + ১.৫% COD চার্জ মাত্র"
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}