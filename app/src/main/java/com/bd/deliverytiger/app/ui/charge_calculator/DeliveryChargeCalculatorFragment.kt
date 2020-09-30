package com.bd.deliverytiger.app.ui.charge_calculator

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.calculator.DeliveryInfo
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

    private lateinit var dataAdapter: ChargeDeliveryTypeAdapter

    private val weightPriceList: MutableList<WeightPrice> = mutableListOf()
    private var showTitle: Boolean = false

    private var codPercent: String = ""
    private var codPercentBangla: String = ""
    private var insideDhakaData: DeliveryInfo? = null
    private var outsideDhakaData: DeliveryInfo? = null
    private var districtId: Int = 0

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

        dataAdapter = ChargeDeliveryTypeAdapter()
        binding?.recyclerView?.let { view1 ->
            with(view1) {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                adapter = dataAdapter
                layoutAnimation = null
                itemAnimator = null
            }
        }
        dataAdapter.onItemClick = { position, model ->
            fetchWeightData(districtId, model.daliveryRangeId)
        }

        viewModel.fetchDeliveryChargeCalculationInfo().observe(viewLifecycleOwner, Observer { model ->
            codPercent = model.codCharge ?: ""
            codPercentBangla = DigitConverter.toBanglaDigit(codPercent)
            insideDhakaData = model.inSideDhaka
            outsideDhakaData = model.outSideDhaka
            initDeliveryType(insideDhakaData)
        })

        changeSelectionColor(1)

        binding?.insideDhakaBtn?.setOnClickListener {
            changeSelectionColor(1)
            initDeliveryType(insideDhakaData)
        }

        binding?.outsideDhakaBtn?.setOnClickListener {
            changeSelectionColor(2)
            initDeliveryType(outsideDhakaData)
        }

        binding?.indicatorSeekBar?.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}
            override fun onSeeking(seekParams: SeekParams?) {
                val position = seekParams?.thumbPosition ?: 0
                val model = weightPriceList[position]
                calculate(model.courierDeliveryCharge)
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

    private fun initDeliveryType(deliveryInfo: DeliveryInfo?) {
        deliveryInfo ?: return
        districtId = deliveryInfo.districtId
        dataAdapter.initLoad(deliveryInfo.deliveryRange)
        val model = deliveryInfo.deliveryRange.first()
        fetchWeightData(districtId, model.daliveryRangeId)
    }

    private fun fetchWeightData(districtId: Int, deliveryRangeId: Int) {

        viewModel.fetchWeightData(districtId, deliveryRangeId).observe(viewLifecycleOwner, Observer { list ->
            weightPriceList.clear()
            weightPriceList.addAll(list)

            val firstModel= list.first()
            val lastModel = list.last()

            val minValue = firstModel.weightNumber.toFloat()
            val maxValue = lastModel.weightNumber.toFloat()
            val weightList = list.map { DigitConverter.toBanglaDigit(it.weightNumber)}

            binding?.indicatorSeekBar?.let { view ->
                with(view) {
                    min = minValue
                    max = maxValue
                    tickCount = list.size
                    customTickTexts(weightList.toTypedArray())
                    setProgress(minValue)
                }
            }

            calculate(firstModel.courierDeliveryCharge)
        })
    }

    private fun changeSelectionColor(flag: Int) {

        when (flag) {
            1 -> {
                binding?.insideDhakaBtn?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding?.outsideDhakaBtn?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.charge_unselect_color))
            }
            2 -> {
                binding?.insideDhakaBtn?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.charge_unselect_color))
                binding?.outsideDhakaBtn?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }
        }
    }

    private fun calculate(charge: Int) {
        val msg = "ডেলিভারি চার্জ <font color='#e11f27'>৳ ${DigitConverter.toBanglaDigit(charge, true)}</font> + <font color='#e11f27'>$codPercentBangla%</font> COD চার্জ মাত্র"
        binding?.changeTV?.text = HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}