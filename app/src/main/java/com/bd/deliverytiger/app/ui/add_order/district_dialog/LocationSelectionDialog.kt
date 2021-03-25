package com.bd.deliverytiger.app.ui.add_order.district_dialog


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.location.LocationData
import com.bd.deliverytiger.app.databinding.FragmentLocationSelectionDialogBinding
import com.bd.deliverytiger.app.utils.hideKeyboard

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import kotlin.concurrent.thread

class LocationSelectionDialog : BottomSheetDialogFragment() {

    private var binding: FragmentLocationSelectionDialogBinding? = null

    private lateinit var crossBtn: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var searchBtn: ImageView
    private lateinit var placeListRV: RecyclerView
    private var progressBar: ProgressBar? = null
    private lateinit var extraSpace: View


    private var handler = Handler(Looper.getMainLooper())
    private var workRunnable: Runnable? = null

    private var dataList: MutableList<LocationData> = mutableListOf()
    private var dataListCopy: MutableList<LocationData> = mutableListOf()

    var onLocationPicked: ((position: Int, model: LocationData) -> Unit)? = null

    companion object {

        @JvmStatic
        fun newInstance(dataList: MutableList<LocationData>): LocationSelectionDialog = LocationSelectionDialog().apply {
                this.dataList = dataList
            }

        @JvmField
        val tag: String = LocationSelectionDialog::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme2)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentLocationSelectionDialogBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crossBtn = view.findViewById(R.id.cross_btn)
        searchEditText = view.findViewById(R.id.search_edit_text)
        searchBtn = view.findViewById(R.id.search_btn)
        placeListRV = view.findViewById(R.id.place_list_rv)
        progressBar = view.findViewById(R.id.progress_bar)
        extraSpace = view.findViewById(R.id.extraSpace)


        val locationAdapter = LocationDistrictAdapter(dataList)
        with(placeListRV) {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = locationAdapter
        }
        locationAdapter.onItemClicked = { position, value ->
            hideKeyboard()
            onLocationPicked?.invoke(position,value)
            dismiss()
        }

        manageSearch()

        crossBtn.setOnClickListener {
            hideKeyboard()
            dismiss()
        }
    }

    private fun manageSearch() {

        searchEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                workRunnable?.let { handler.removeCallbacks(it) }
                workRunnable = Runnable {
                    val searchKey = p0.toString()
                    search(searchKey)
                }
                handler.postDelayed(workRunnable!!, 500L)
            }
        })

        searchBtn.setOnClickListener {
            hideKeyboard()
            val searchKey = searchEditText.text.toString()
            search(searchKey)
        }
    }

    private fun search(searchKey: String) {

        progressBar?.visibility = View.VISIBLE
        if (dataListCopy.isEmpty()) {
            dataListCopy.clear()
            dataListCopy.addAll(dataList)
        }
        if (searchKey.isEmpty()) {
            (placeListRV.adapter as LocationDistrictAdapter).setDataList(dataListCopy)
            progressBar?.visibility = View.GONE
            return
        }
        val lowerCaseSearchKey = searchKey.toLowerCase(Locale.US)
        val filteredList = dataListCopy.filter { model ->
            (model.searchKey.contains(lowerCaseSearchKey))
        }
        (placeListRV.adapter as LocationDistrictAdapter).setDataList(filteredList)
        progressBar?.visibility = View.GONE
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
                    //val dynamicHeight = parentLayout.height
                    BottomSheetBehavior.from(bottomSheet).peekHeight = metrics.heightPixels
                }
            }
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = false
            BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                    BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                }
                override fun onStateChanged(p0: View, p1: Int) {
                    /*if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                    }*/
                }
            })
        }
        //extraSpace.minimumHeight = (metrics.heightPixels)
    }

    override fun onStop() {
        super.onStop()
        workRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
