package com.bd.deliverytiger.app.ui.order_tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.utils.DigitConverter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderTrackingBottomSheet : BottomSheetDialogFragment() {

    private lateinit var orderTrackMainResponse: OrderTrackMainResponse
    private lateinit var rvOrderTrackDetails: RecyclerView
    private lateinit var tvOrderTrackStatusDetails: TextView
    private lateinit var tvOrderTrackDateDetails: TextView

    companion object {
        fun newInstance(orderTrackMainResponse: OrderTrackMainResponse): OrderTrackingBottomSheet {
            val fragment = OrderTrackingBottomSheet()
            fragment.orderTrackMainResponse = orderTrackMainResponse
            return fragment
        }

        val tag: String = OrderTrackingBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_tracking_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvOrderTrackDetails = view.findViewById(R.id.rvOrderTrackDetails)
        tvOrderTrackStatusDetails = view.findViewById(R.id.tvOrderTrackStatusDetails)
        tvOrderTrackDateDetails = view.findViewById(R.id.tvOrderTrackDateDetails)

        tvOrderTrackStatusDetails.text = orderTrackMainResponse.orderTrackStatusGroup

        val formattedDate =
            DigitConverter.toBanglaDate(orderTrackMainResponse?.dateTime, "yyyy-MM-dd'T'HH:mm:ss")
        tvOrderTrackDateDetails.text = formattedDate

        val orderTrackingSubAdapter = OrderTrackingSubAdapter(requireContext(), orderTrackMainResponse.status)

        rvOrderTrackDetails.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderTrackingSubAdapter
        }
    }
}