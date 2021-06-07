package com.bd.deliverytiger.app.ui.complain.complain_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentComplainHistoryBottomSheetBinding
import com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet.ServicesSelectionBottomSheet
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

class ComplainHistoryBottomSheet : BottomSheetDialogFragment() {
    private var binding: FragmentComplainHistoryBottomSheetBinding? = null
    private var dataAdapter: ComplainHistoryAdapter = ComplainHistoryAdapter()
    private val viewModel: ComplainHistoryViewModel by inject()

    private var selectedBookingCode = 0

    companion object {

        fun newInstance(bookingCode: Int): ComplainHistoryBottomSheet = ComplainHistoryBottomSheet().apply {
            selectedBookingCode = bookingCode
        }

        val tag: String = ServicesSelectionBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentComplainHistoryBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()
        initClickLister()
        initData()
    }

    private fun initView() {
        binding?.recyclerview?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
            }
        }
    }

    private fun initClickLister(){
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
    }

    private fun initData(){
        viewModel.getComplainHistory(selectedBookingCode).observe(viewLifecycleOwner, Observer { model->
            if (model.isNullOrEmpty()){
                binding?.emptyView?.visibility = View.VISIBLE
            }else{
                binding?.emptyView?.visibility = View.GONE
                dataAdapter.initLoad(model)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parentLayout?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = false

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}