package com.bd.deliverytiger.app.ui.add_order.service_wise_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.location.LocationData
import com.bd.deliverytiger.app.api.model.service_selection.ServiceInfoData
import com.bd.deliverytiger.app.databinding.FragmentServicesSelectionBottomSheetBinding
import com.bd.deliverytiger.app.ui.add_order.district_dialog.LocationSelectionDialog
import com.bd.deliverytiger.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.concurrent.thread
import androidx.lifecycle.Observer

class ServicesSelectionBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentServicesSelectionBottomSheetBinding? = null
    private  var dataAdapter: ServiceSelectionBottomSheetAdapter = ServiceSelectionBottomSheetAdapter()
    private val viewModel: ServiceSelectionBottomSheetViewModel by inject()

    private lateinit var dataList:  List<ServiceInfoData>

    var onServiceSelected: ((position: Int, service: ServiceInfoData, district: LocationData ) -> Unit)? = null

    companion object {

        fun newInstance(serviceList: List<ServiceInfoData>): ServicesSelectionBottomSheet = ServicesSelectionBottomSheet().apply {
            this.dataList = serviceList
        }

        val tag: String = ServicesSelectionBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentServicesSelectionBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickLister()
        initServiceData()
    }

    private fun initView() {
        binding?.recycleView?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                adapter = dataAdapter
            }
        }
    }

    private fun initClickLister() {
       dataAdapter.onDistrictSelectionClick = {_, model ->
           goToDistrictSelectDialogue(model)
       }
    }

    private fun initServiceData(){
        dataAdapter.initLoad(dataList)
        viewModel.fetchServiceWiseDistrict(dataList).observe(viewLifecycleOwner, Observer { model ->
            dataAdapter.updateData(model)
        })
    }

    private fun goToDistrictSelectDialogue(serviceInfo: ServiceInfoData) {

        if (serviceInfo.districtList.isEmpty()) {
            context?.toast("জেলা লোড হচ্ছে অপেক্ষা করুন")
            return
        }

        val locationList: MutableList<LocationData> = mutableListOf()
        serviceInfo.districtList.forEach { model ->
            locationList.add(
                LocationData(
                    model.districtId,
                    model.districtBng,
                    model.district,
                    model.postalCode,
                    model.district?.toLowerCase(Locale.US) ?: ""
                )
            )
        }

        val dialog = LocationSelectionDialog.newInstance(locationList)
        dialog.show(childFragmentManager, LocationSelectionDialog.tag)
        dialog.onLocationPicked = {position, district ->
            onServiceSelected?.invoke(position, serviceInfo, district)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(false)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        val metrics = resources.displayMetrics
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    BottomSheetBehavior.from(bottomSheet).peekHeight = metrics.heightPixels
                }
            }
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = false
            /*BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                    BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                }
                override fun onStateChanged(p0: View, p1: Int) {
                    *//*if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                    }*//*
                }
            })*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}